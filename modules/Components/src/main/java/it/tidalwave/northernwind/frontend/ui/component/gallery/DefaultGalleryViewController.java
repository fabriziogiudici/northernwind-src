/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2012 by Tidalwave s.a.s. (http://www.tidalwave.it)
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
package it.tidalwave.northernwind.frontend.ui.component.gallery;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Finder;
import it.tidalwave.util.spi.SimpleFinderSupport;
import it.tidalwave.northernwind.core.model.RequestLocaleManager;
import it.tidalwave.northernwind.core.model.Site;
import it.tidalwave.northernwind.core.model.SiteNode;
import it.tidalwave.northernwind.frontend.ui.component.gallery.spi.GalleryAdapter;
import it.tidalwave.northernwind.frontend.ui.component.gallery.spi.GalleryLoader;
import it.tidalwave.northernwind.frontend.ui.component.gallery.spi.loader.SlideShowProPlayerGalleryLoader;
import it.tidalwave.northernwind.frontend.ui.component.nodecontainer.DefaultNodeContainerViewController;
import it.tidalwave.northernwind.frontend.ui.component.nodecontainer.NodeContainerView;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class DefaultGalleryViewController extends DefaultNodeContainerViewController implements GalleryViewController
  {
    @Nonnull
    private final SiteNode siteNode;
    
//    @Nonnull
//    private final NodeContainerView view;
//    
    protected GalleryAdapter galleryAdapter;
    
    protected final List<Item> items = new ArrayList<Item>();
   
    protected final SortedMap<String, Item> itemMapByKey = new TreeMap<String, Item>();

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DefaultGalleryViewController (final @Nonnull NodeContainerView view, 
                                         final @Nonnull SiteNode siteNode,
                                         final @Nonnull Site site, 
                                         final @Nonnull RequestLocaleManager requestLocaleManager)
      {
        super(view, siteNode, site, requestLocaleManager);
        this.siteNode = siteNode;
//        this.view = view;
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    private void initialize()
      {
        loadItems();  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Finder<SiteNode> findChildrenSiteNodes()
      {
        return new SimpleFinderSupport<SiteNode>()
          {
            @Override
            protected List<? extends SiteNode> computeResults()
              {
                log.info("findChildrenSiteNodes()");
                final List<SiteNode> results = new ArrayList<SiteNode>();

                for (final Item item : itemMapByKey.values())
                  {
                    final String relativeUri = siteNode.getRelativeUri() + "/"  + item.getRelativePath() + "/";
                    results.add(new ChildSiteNode(siteNode, relativeUri, siteNode.getProperties()));
                  }

                log.info(">>>> returning: {}", results);

                return results;
              }
          };
      }
    
    /*******************************************************************************************************************
     *
     * Loads the items in the gallery.
     *
     ******************************************************************************************************************/
    private void loadItems()
      {
        items.clear();
        itemMapByKey.clear();
        final GalleryLoader loader = new SlideShowProPlayerGalleryLoader(); // FIXME: make it configurable
        items.addAll(loader.loadGallery(siteNode));
        
        for (final Item item : items)
          {
            itemMapByKey.put(item.getRelativePath(), item);   
          }
      }
  }
