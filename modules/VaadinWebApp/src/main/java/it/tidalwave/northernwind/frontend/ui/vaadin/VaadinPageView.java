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
package it.tidalwave.northernwind.frontend.ui.vaadin;

import it.tidalwave.northernwind.frontend.model.WebSiteModel;
import it.tidalwave.northernwind.frontend.ui.PageView;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.AbstractLayout;
import javax.annotation.Nonnull;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import it.tidalwave.northernwind.frontend.model.Media;
import it.tidalwave.northernwind.frontend.ui.component.menu.vaadin.VaadinHorizontalMenuView;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable(preConstruction=true) @Slf4j
public class VaadinPageView extends Window implements PageView
  {
    @Nonnull @Inject
    private WebSiteModel webSiteModel;
    
    private final VaadinPageViewController controller;
    
    public VaadinPageView() 
      {
        log.info("VaadinPageView()");
        controller = new VaadinPageViewController(this); 
        setStyleName("component-" + "page");
        ((AbstractLayout)getContent()).setMargin(false);
      }

    @Override
    public void setContents (final @Nonnull Object content) 
      throws IOException
      {
        removeAllComponents();
        final VaadinHorizontalMenuView menuView = new VaadinHorizontalMenuView("nav");
        
        try {
        menuView.setLinks(Arrays.asList
          (
            "/",
            "/Features",
            "/Download",
            "/Screenshots",
            "/Getting started",
            "/Blog & News (new)",
            "/Contact",
            "/License",
            "/Developers"
          )); }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        final Media media = webSiteModel.getMedia("/content/media/blueBill_Mobile-Banner.png");
        addComponent(new Embedded("", new FileResource(media.getFile(), getApplication())));
        addComponent(menuView);
        addComponent((Component)content);
      }
  }
