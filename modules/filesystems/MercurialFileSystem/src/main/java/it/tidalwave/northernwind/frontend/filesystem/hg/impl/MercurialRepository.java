/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2018 Tidalwave s.a.s. (http://tidalwave.it)
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
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.frontend.filesystem.hg.impl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;
import java.net.URI;

/***********************************************************************************************************************
 *
 * A repository based on Mercurial.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MercurialRepository
  {
    public boolean isEmpty();

    /*******************************************************************************************************************
     *
     * Clones the repo given the source URL.
     *
     * @param       url                     the URL of the source repo
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    public void clone (@Nonnull URI url)
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Pulls changes from the remote repository into the working area.
     *
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    public void pull()
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Returns the current tag of the working area, if present.
     *
     * @return                              the current tag
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Tag> getCurrentTag()
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Returns all the tags of the working area.
     *
     * @return                              the tags
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    @Nonnull
    public List<Tag> getTags()
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Returns the latest tag of the working area matching the given regular expression, if present.
     *
     * @param   regexp                      the regular expression
     * @return                              the tag
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    @Nonnull
    public Optional<Tag> getLatestTagMatching (@Nonnull String regexp)
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Updates the working area to the given tag.
     *
     * @param       tag                     the tag
     * @throws      InterruptedException    if the operation has been interrupted
     * @throws      IOException             if something fails
     *
     ******************************************************************************************************************/
    public void updateTo (@Nonnull Tag tag)
      throws InterruptedException, IOException;

    /*******************************************************************************************************************
     *
     * Returns the path of the working area.
     *
     * @return                              the path to the working area.
     *
     ******************************************************************************************************************/
    @Nonnull
    public Path getWorkArea();
  }
