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
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import org.springframework.beans.BeansException;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import it.tidalwave.messagebus.MessageBus;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.DefaultMercurialRepository;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.MercurialRepository;
import it.tidalwave.northernwind.frontend.filesystem.hg.impl.Tag;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static it.tidalwave.northernwind.frontend.filesystem.hg.impl.TestRepositoryHelper.*;
import static it.tidalwave.northernwind.frontend.filesystem.hg.ResourceFileSystemChangedEventMatcher.*;
import it.tidalwave.util.NotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MercurialFileSystemProviderTest
  {
    private MercurialFileSystemProvider fixture;

    private GenericXmlApplicationContext context;

    private MessageBus messageBus;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @BeforeMethod
    public void setupFixture()
      throws Exception
      {
        prepareSourceRepository(Option.UPDATE_TO_PUBLISHED_0_8);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("test.repositoryUrl", sourceRepository.toUri().toASCIIString());
        properties.put("test.workAreaFolder", Files.createTempDirectory("workarea").toFile().getAbsolutePath());
        context = createContextWithProperties(properties);
        fixture = context.getBean(MercurialFileSystemProvider.class);
        messageBus = context.getBean(MessageBus.class);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_properly_initialize()
      throws Exception
      {
	    assertInvariantPostConditions();
        assertThat(fixture.exposedRepository.getTags(), is(ALL_TAGS_UP_TO_PUBLISHED_0_8));
        assertThat(fixture.alternateRepository.getTags(), is(ALL_TAGS_UP_TO_PUBLISHED_0_8));
		assertThatHasNoCurrentTag(fixture.exposedRepository);
		assertThatHasNoCurrentTag(fixture.alternateRepository);
        assertThat(fixture.swapCounter, is(0));
        verifyZeroInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods="must_properly_initialize")
    public void checkForUpdates_must_do_nothing_when_there_are_no_updates()
      throws Exception
      {
		updateWorkAreaTo(fixture.getCurrentWorkArea(), new Tag("published-0.8"));
        final int previousSwapCounter = fixture.swapCounter;

        fixture.checkForUpdates();

		assertInvariantPostConditions();
        assertThat(fixture.getCurrentTag().getName(), is("published-0.8"));
        assertThat(fixture.swapCounter, is(previousSwapCounter));
        verifyZeroInteractions(messageBus);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dependsOnMethods="must_properly_initialize")
    public void checkForUpdates_must_update_and_fire_event_when_there_are_updates()
      throws Exception
      {
		updateWorkAreaTo(fixture.getCurrentWorkArea(), new Tag("published-0.8"));
        final int previousSwapCounter = fixture.swapCounter;
        prepareSourceRepository(Option.UPDATE_TO_PUBLISHED_0_9);
		final DateTime now = new DateTime();
		DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        fixture.checkForUpdates();

		assertInvariantPostConditions();
        assertThat(fixture.getCurrentTag().getName(), is("published-0.9"));
        assertThat(fixture.swapCounter, is(previousSwapCounter + 1));
        verify(messageBus).publish(is(argThat(fileSystemChangedEvent().withResourceFileSystemProvider(fixture)
																	   .withLatestModificationTime(now))));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
	protected static void updateWorkAreaTo (final @Nonnull Path workArea, final @Nonnull Tag tag)
	  throws IOException
	  {
		new DefaultMercurialRepository(workArea).updateTo(tag);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
	private void assertInvariantPostConditions()
	  {
        assertThat(fixture.exposedRepository.getWorkArea(), is(not(fixture.alternateRepository.getWorkArea())));
		assertThat(fixture.fileSystemDelegate.getRootDirectory().toPath(), is(fixture.exposedRepository.getWorkArea()));
	  }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void assertThatHasNoCurrentTag (final @Nonnull MercurialRepository repository)
	  throws IOException
      {
		try
		  {
			final Tag tag = repository.getCurrentTag();
			fail("Repository should have not current tag, it has " + tag);
		  }
		catch (NotFoundException e)
		  {
			// ok
		  }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
	@Nonnull
    private GenericXmlApplicationContext createContextWithProperties (final @Nonnull Map<String, Object> properties)
        throws IllegalStateException, BeansException
	  {
		final StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test", properties));
		context = new GenericXmlApplicationContext();
		context.setEnvironment(environment);
		context.load("/MercurialFileSystemTestBeans.xml");
		context.refresh();

		return context;
	  }
  }
