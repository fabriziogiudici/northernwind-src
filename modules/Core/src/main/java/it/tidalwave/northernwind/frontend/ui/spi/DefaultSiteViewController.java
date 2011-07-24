/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2011 by Tidalwave s.a.s. (http://www.tidalwave.it)
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
 * WWW: http://northernwind.java.net
 * SCM: http://java.net/hg/northernwind~src
 *
 **********************************************************************************************************************/
package it.tidalwave.northernwind.frontend.ui.spi;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.frontend.model.UriHandler;
import it.tidalwave.northernwind.frontend.ui.SiteViewController;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The default implementation of {@link SiteViewController}.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class DefaultSiteViewController implements SiteViewController
  {
    @Getter @Setter @Nonnull
    private List<UriHandler> uriHandlers;
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void handleUri (final @Nonnull URL context, final @Nonnull String relativeUri) 
      {
        try
          {
            log.info("handleUri({}, {})", context, relativeUri);
            
            for (final UriHandler uriHandler : uriHandlers)
              {
                log.debug(">>>> trying {} ...", uriHandler);
                
                if (uriHandler.handleUri(context, relativeUri))
                  {
                    break;  
                  }
              }
          }
        catch (NotFoundException e) 
          {
            log.error("", e); 
            // TODO
          }
        catch (IOException e) 
          {
            log.error("", e);
            // TODO
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    public void logConfiguration()
      {
        log.info(">>>> uriHandlers: {}", uriHandlers);  
      }
  } 
