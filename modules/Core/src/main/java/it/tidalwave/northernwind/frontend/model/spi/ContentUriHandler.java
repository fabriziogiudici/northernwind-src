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
package it.tidalwave.northernwind.frontend.model.spi; 

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.frontend.model.UriHandler;
import it.tidalwave.northernwind.frontend.model.WebSite;
import it.tidalwave.northernwind.frontend.model.WebSiteNode;
import it.tidalwave.northernwind.frontend.ui.PageView;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import static it.tidalwave.northernwind.frontend.model.WebSiteNode.WebSiteNode;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Scope(value="session") 
public class ContentUriHandler implements UriHandler
  {
    @Inject @Nonnull
    private WebSite webSite;
    
    @Inject @Nonnull
    private PageView pageView;
        
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public boolean handleUri (final @Nonnull URL context, final @Nonnull String relativeUri)
      throws NotFoundException, IOException 
      {
        final WebSiteNode webSiteNode = webSite.find(WebSiteNode).withRelativeUri("/" + relativeUri).result();            
//            pageView.setCaption(structure.getProperties().getProperty("Title")); TODO
        pageView.setWebSiteNodeView(webSiteNode.createView());
        
        return true;
      }
  }
