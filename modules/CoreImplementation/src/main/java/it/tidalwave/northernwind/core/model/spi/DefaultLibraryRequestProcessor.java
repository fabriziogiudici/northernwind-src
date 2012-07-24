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
package it.tidalwave.northernwind.core.model.spi;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import org.joda.time.Duration;
import it.tidalwave.northernwind.core.model.ResourceFile;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.core.model.Request;
import it.tidalwave.northernwind.core.model.RequestProcessor;
import it.tidalwave.northernwind.core.model.Resource;
import it.tidalwave.northernwind.core.model.SiteProvider;
import it.tidalwave.northernwind.core.impl.model.FilterSetExpander;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.core.Ordered.*;
import static it.tidalwave.northernwind.core.model.RequestProcessor.Status.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Order(HIGHEST_PRECEDENCE + 1) @Slf4j
public class DefaultLibraryRequestProcessor implements RequestProcessor 
  {
    @Inject @Nonnull
    private Provider<SiteProvider> siteProvider;
    
    @Inject @Nonnull
    private Provider<FilterSetExpander> macroExpander;
    
    @Inject @Nonnull
    private ResponseHolder<?> responseHolder;

    @Getter @Setter
    private Duration duration = Duration.standardDays(7);

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Status process (final @Nonnull Request request) 
      {
        final String relativePath = request.getRelativeUri();

        try
          {
            final Resource resource = siteProvider.get().getSite().find(Resource.class).withRelativePath(relativePath).result();
            final ResourceFile file = resource.getFile();
            final String mimeType = file.getMimeType();
            final Object content = mimeType.startsWith("text/") ? macroExpander.get().filter(file.asText("UTF-8"), mimeType)
                                                                : file.asBytes();
            responseHolder.response().withContentType(mimeType)
                                     .withLastModified(file.lastModified())
                                     .withExpirationTime(duration)
                                     .withBody(content)
                                     .put();  
            return BREAK;
          }
        catch (IOException | NotFoundException e)
          {
            log.info("Requested URI {} doesn't map to a library resource, continuing...", relativePath);  
          }
        
        return CONTINUE;
      }
  }
