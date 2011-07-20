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
package it.tidalwave.northernwind.frontend.vaadin;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.URIHandler;
import it.tidalwave.northernwind.frontend.vaadin.component.article.DefaultArticleViewController;
import it.tidalwave.northernwind.frontend.vaadin.component.article.VaadinArticleView;
import java.io.File;
import java.net.URL;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor @Slf4j
public class DefaultPageViewController implements PageViewController 
  {
    private final File root = new File("/workarea/home/fritz/Business/Tidalwave/Projects/WorkAreas/Tidalwave/tidalwave~other/InfoglueExporter/target/export");

    private final URIHandler uriHandler = new URIHandler()
      {
        @Override
        public DownloadStream handleURI (final @Nonnull URL context,
                                         final @Nonnull String relativeUri) 
          {
            log.info("uri: {}", relativeUri);
            setRelativeUri(relativeUri);
            return null; 
          }
      };
    
    @Nonnull
    private final PageView view;
    
    @CheckForNull
    private WebSiteModel pageModel;

    public DefaultPageViewController (final @Nonnull NorthernWindMainWindow view) 
      {
        this.view = view;
        view.addURIHandler(uriHandler);
      }
    
    private void setRelativeUri (final @Nonnull String relativeUri) 
      {
        pageModel = new DefaultWebSiteModel(relativeUri);
        view.setCaption(relativeUri);
        final VaadinArticleView vaadinArticleView = new VaadinArticleView();
        final Resource resource = new Resource(new File(root, "content/document/" + relativeUri));
        new DefaultArticleViewController(vaadinArticleView, resource);
        view.setContent(vaadinArticleView);
      } 
  }
