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
package it.tidalwave.northernwind.frontend.ui.component.article;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.frontend.model.Content;
import it.tidalwave.northernwind.frontend.model.Site;
import it.tidalwave.northernwind.frontend.model.SiteNode;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.northernwind.frontend.model.Content.Content;
import static it.tidalwave.northernwind.frontend.ui.component.article.ArticleView.*;

/***********************************************************************************************************************
 *
 * A default implementation of {@link ArticleViewController}.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable(preConstruction=true) @Slf4j
public class DefaultArticleViewController implements ArticleViewController
  {
    @Nonnull @Inject
    private Site site;
    
    /*******************************************************************************************************************
     *
     * Creates an instance for populating the given {@link ArticleView} with the given URI.
     * 
     * @param  view              the related view
     * @param  viewInstanceName  the name of the view instance
     * @param  siteNode          the related {@link SiteNode}
     *
     ******************************************************************************************************************/
    public DefaultArticleViewController (final @Nonnull ArticleView view, 
                                         final @Nonnull String viewInstanceName,
                                         final @Nonnull SiteNode siteNode) 
    {
        try
          {
            final Key<String> PROP_CONTENT = new Key<String>(viewInstanceName + ".content"); // FIXME: have a subproperty group with the name
            final String contentUri = siteNode.getProperty(PROP_CONTENT);
            
            // FIXME: should be fixed in the Infoglue importer
            final String fixedContentUri = r(contentUri.replaceAll("/content/document/Mobile", "").replaceAll("/content/document", ""));
            
            final Content content = site.find(Content).withRelativeUri(fixedContentUri).result();
            view.setText(content.getProperty(PROP_FULL_TEXT));
          }
        catch (NotFoundException e)
          {
            view.setText(e.toString());
            log.error("", e);
          }
        catch (IOException e)
          {
            view.setText(e.toString());
            log.error("", e);
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static String r (final @Nonnull String s)
      {
        return "".equals(s) ? "/" : s;  
      }
  }
