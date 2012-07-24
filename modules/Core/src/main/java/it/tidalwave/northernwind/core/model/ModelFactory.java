/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2012 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 ***********************************************************************************************************************
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
 ***********************************************************************************************************************
 *
 * WWW: http://northernwind.tidalwave.it
 * SCM: https://bitbucket.org/tidalwave/northernwind-src
 *
 **********************************************************************************************************************/
package it.tidalwave.northernwind.core.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import it.tidalwave.northernwind.core.model.NwFileObject;
import it.tidalwave.util.Id;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.frontend.ui.Layout;

/***********************************************************************************************************************
 *
 * A factory for domain objects.
 * 
 * @author  Fabrizio Giudici
 * @version $Id: $
 *
 **********************************************************************************************************************/
public interface ModelFactory 
  {
    /*******************************************************************************************************************
     *
     * Creates a new {@link Resource}.
     * 
     * @param  file  the file for the {@code Resource}
     * @return       the {@code Resource}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Resource createResource (@Nonnull NwFileObject file);

    /*******************************************************************************************************************
     *
     * Creates a new {@link Content}.
     * 
     * @param  file  the file for the {@code Content}
     * @return       the {@code Content}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Content createContent (@Nonnull NwFileObject folder);

    /*******************************************************************************************************************
     *
     * Creates a new {@link Media}.
     * 
     * @param  file  the file for the {@code Media}
     * @return       the {@code Media}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Media createMedia (@Nonnull NwFileObject file);

    /*******************************************************************************************************************
     *
     * Creates a new {@link SiteNode}.
     * 
     * @param  file  the file for the {@code SiteNode}
     * @return       the {@code SiteNode}
     *
     ******************************************************************************************************************/
    @Nonnull
    public SiteNode createSiteNode (@Nonnull Site site, @Nonnull NwFileObject folder)
      throws IOException, NotFoundException;

    /*******************************************************************************************************************
     *
     * Creates a new {@link Layout}.
     * 
     * @param  id    the id
     * @param  type  the type
     * @return       the {@code Layout}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Layout createLayout (@Nonnull Id id, @Nonnull String type);
    
    /*******************************************************************************************************************
     *
     * Creates a new {@link Request}.
     * 
     * @return       the {@code Request}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Request createRequest();
    
    /*******************************************************************************************************************
     *
     * Creates a new {@link Request} from a given {@link HttpServletRequest}.
     * 
     * @param        httpServletRequest   the {@code HttpServletRequest}
     * @return                            the {@code Request}
     *
     ******************************************************************************************************************/
    @Nonnull
    public Request createRequestFrom (@Nonnull HttpServletRequest httpServletRequest);
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    public ResourceProperties createProperties (@Nonnull Id id);

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull // FIXME: use a SiteCreationParameters bean instead of so many arguments
    public Site createSite (@Nonnull String contextPath, 
                            @Nonnull String documentPath, 
                            @Nonnull String mediaPath, 
                            @Nonnull String libraryPath,
                            @Nonnull String nodePath,
                            boolean logConfigurationEnabled,
                            @Nonnull List<Locale> configuredLocales, 
                            @Nonnull List<String> ignoredFolders);
  }
