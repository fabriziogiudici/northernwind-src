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
package it.tidalwave.northernwind.frontend.model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import org.openide.filesystems.FileObject;
import org.springframework.beans.factory.annotation.Configurable;
import lombok.Delegate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A node of the website, mapped to a given URL.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable(preConstruction=true) @RequiredArgsConstructor @Slf4j @ToString
public class WebSiteNode
  {
    public static final Class<WebSiteNode> WebSiteNode = WebSiteNode.class;
    
    public static final Key<String> PROP_NAVIGATION_TITLE = new Key<String>("NavigationTitle");
    
    @Nonnull @Inject
    private WebSite webSite;
    
    @Nonnull @Delegate(types=Resource.class)
    private final Resource resource;
    
    @Nonnull @Getter
    private final String relativeUri;

    /*******************************************************************************************************************
     *
     * Creates a new instance with the given configuration file and mapped to the given URI.
     * 
     * @param  file          the file with the configuration
     * @param  relativeUri   the bound URI
     *
     ******************************************************************************************************************/
    public WebSiteNode (final @Nonnull FileObject file, final @Nonnull String relativeUri)
      {
        resource = new Resource(file);  
        this.relativeUri = relativeUri;
      }

    /*******************************************************************************************************************
     *
     * Creates the UI contents for this {@code WebSiteNode}.
     * 
     * @return   the contents
     *
     ******************************************************************************************************************/
    @Nonnull
    public Object createContents()
      throws IOException, NotFoundException
      {
        // FIXME: this is temporary
        final Key<String> K = new Key<String>("main.content");
        final String contentUri = resource.getProperty(K);
        final String fixedContentUri = r(contentUri.replaceAll("/content/document/Mobile", "").replaceAll("/content/document", ""));

        // FIXME: load from the config of this node
        return createContent("it.tidalwave.northernwind.frontend.ui.component.article.vaadin.VaadinArticleView", 
                             "it.tidalwave.northernwind.frontend.ui.component.article.DefaultArticleViewController", 
                             fixedContentUri);
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private Object createContent (final @Nonnull String viewClassName,
                                  final @Nonnull String viewControllerClassName,
                                  final @Nonnull String contentUri)
      {
        try
          { 
            final @Nonnull Class<?> viewClass = Class.forName(viewClassName);
            final @Nonnull Class<?> viewControllerClass = Class.forName(viewControllerClassName);
            final Object view = viewClass.getConstructor(String.class).newInstance("main");
            viewControllerClass.getConstructor(viewClass.getInterfaces()[0], String.class).newInstance(view, contentUri);  
            return view;
          }
        catch (Exception e)
          {
            throw new RuntimeException(e);
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