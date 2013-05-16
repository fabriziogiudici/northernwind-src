/*
 * #%L
 * *********************************************************************************************************************
 * 
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - hg clone https://bitbucket.org/tidalwave/northernwind-src
 * %%
 * Copyright (C) 2011 - 2013 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 * *********************************************************************************************************************
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * *********************************************************************************************************************
 * 
 * $Id$
 * 
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.frontend.filesystem.hg;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import java.beans.PropertyVetoException;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.net.URI;
import java.net.URISyntaxException;
import org.joda.time.DateTime;
import org.openide.filesystems.LocalFileSystem;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.northernwind.core.model.ResourceFileSystemChangedEvent;
import it.tidalwave.northernwind.core.model.ResourceFileSystemProvider;
import it.tidalwave.northernwind.core.model.ResourceFileSystem;
import it.tidalwave.northernwind.frontend.filesystem.impl.ResourceFileSystemNetBeansPlatform;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.DefaultMercurialRepository;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.MercurialRepository;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;

/***********************************************************************************************************************
 *
 * The implementation relies upon two alternate workareas to perform atomic changes:
 *
 * <ol>
 *     <li>the <code>exposedWorkArea</code> is the one whose contents are used for publishing, and it's never touched
 *     </li>
 *     <li>the <code>alternateWorkArea</code> is kept behind the scenes and it's used for updates</li>
 * </ol>
 *
 * When there are changes in the <code>alternateWorkArea</code>, the two workAreas are swapped.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NotThreadSafe @Slf4j
public class MercurialFileSystemProvider implements ResourceFileSystemProvider
  {
    @Getter @Setter
    private String remoteRepositoryUrl;

    @Getter @Setter
    private String workAreaFolder;

    private final LocalFileSystem fileSystemDelegate = new LocalFileSystem();

    @Getter
    private final ResourceFileSystem fileSystem = new ResourceFileSystemNetBeansPlatform(fileSystemDelegate);

    @Inject
    private BeanFactory beanFactory;

//    @Inject @Named("applicationMessageBus") FIXME doesn't work in the test
    private MessageBus messageBus;

    private Path workArea;

    private final MercurialRepository[] workAreas = new MercurialRepository[2];

    private MercurialRepository exposedWorkArea;

    private MercurialRepository alternateWorkArea;

    private int repositorySelector;

    /* package */ int swapCounter;

    /*******************************************************************************************************************
     *
     * Makes sure both repository workAreas are populated and activates one of them.
     *
     ******************************************************************************************************************/
    @PostConstruct
    public void initialize()
      throws IOException, PropertyVetoException, URISyntaxException
      {
        workArea = new File(workAreaFolder).toPath();

        for (int i = 0; i < 2; i++)
          {
            workAreas[i] = new DefaultMercurialRepository(workArea.resolve("" + (i + 1)));

            if (workAreas[i].isEmpty())
              {
                // FIXME: this is inefficient, clones both from the remote repo
                workAreas[i].clone(new URI(remoteRepositoryUrl));
              }
          }

        messageBus = beanFactory.getBean("applicationMessageBus", MessageBus.class); // FIXME

        swapWorkAreas(); // initialization
        swapCounter = 0;
      }

    /*******************************************************************************************************************
     *
     * Checks whether there are incoming changes. Changes are detected when there's a new tag whose name follows the
     * pattern 'published-<version>'. Changes are pulled in the alternate workArea, then workAreas are swapped, at last
     * the alternateWorkArea is updated too.
     *
     ******************************************************************************************************************/
    public void checkForUpdates()
      {
        try
          {
            final Tag newTag = findNewTag();
            log.info(">>>> new tag seen: {}", newTag);
            alternateWorkArea.updateTo(newTag);
            swapWorkAreas();
            messageBus.publish(new ResourceFileSystemChangedEvent(this, new DateTime()));
            alternateWorkArea.pull();
            alternateWorkArea.updateTo(newTag);
          }
        catch (NotFoundException e)
          {
            log.info(">>>> no changes");
          }
        catch (Exception e)
          {
            log.warn(">>>> error when checking for updates", e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    /* package */ Tag getCurrentTag()
      throws IOException, NotFoundException
      {
        return exposedWorkArea.getCurrentTag();
      }

    @Nonnull
    /* package */ Path getCurrentWorkArea()
      {
        return exposedWorkArea.getWorkArea();
      }

    /*******************************************************************************************************************
     *
     * Swaps the workAreas.
     *
     * @throws IOException in case of error
     * @throws PropertyVetoException in case of error
     *
     ******************************************************************************************************************/
    private void swapWorkAreas()
      throws IOException, PropertyVetoException
      {
        exposedWorkArea = workAreas[repositorySelector];
        alternateWorkArea = workAreas[(repositorySelector + 1) % 2];
        repositorySelector = (repositorySelector + 1) % 2;
        fileSystemDelegate.setRootDirectory(exposedWorkArea.getWorkArea().toFile());
        swapCounter++;

        log.info("New exposed repository:   {}", exposedWorkArea.getWorkArea());
        log.info("New alternate repository: {}", alternateWorkArea.getWorkArea());
      }

    /*******************************************************************************************************************
     *
     * Finds a new tag.
     *
     * @return  the new tag
     * @throws NotFoundException if no new tag is found
     * @throws IOException in case of error
     *
     ******************************************************************************************************************/
    @Nonnull
    private Tag findNewTag()
      throws NotFoundException, IOException
      {
        log.info("Checking for updates...");

        alternateWorkArea.pull();

        final Tag latestTag = getLatestPublishingTag(alternateWorkArea); // NotFoundException if no publishing tag

        try
          {
            if (!latestTag.equals(exposedWorkArea.getCurrentTag()))
              {
                return latestTag;
              }
          }
        catch (NotFoundException e)
          {
            log.info(">>>> repo must be initialized");
            return latestTag;
          }

        throw new NotFoundException();
      }

    /*******************************************************************************************************************
     *
     * Returns the latest publishing tag in the given workArea.
     *
     * FIXME: move to MercurialRepository, passing a regexp to match
     *
     * @param workArea  the workArea
     * @return the <code>Tag</code>
     * @throws NotFoundException if no tag is found
     * @throws IOException in case of error
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Tag getLatestPublishingTag (final @Nonnull MercurialRepository workArea)
      throws IOException, NotFoundException
      {
        final List<Tag> tags = workArea.getTags();
        Collections.reverse(tags);

        for (final Tag tag : tags)
          {
            if (tag.getName().startsWith("published-"))
              {
                return tag;
              }
          }

        throw new NotFoundException();
      }
  }
