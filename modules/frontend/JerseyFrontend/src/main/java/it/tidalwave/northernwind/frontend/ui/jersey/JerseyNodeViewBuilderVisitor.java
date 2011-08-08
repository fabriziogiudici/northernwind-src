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
package it.tidalwave.northernwind.frontend.ui.jersey;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import it.tidalwave.northernwind.core.model.SiteNode;
import it.tidalwave.northernwind.frontend.ui.Layout;
import it.tidalwave.northernwind.frontend.ui.component.htmltemplate.TextHolder;
import it.tidalwave.northernwind.frontend.ui.component.htmltemplate.HtmlHolder;
import it.tidalwave.northernwind.frontend.ui.spi.NodeViewBuilderVisitorSupport;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A visitor for {@link Layout} that builds a Jersey view.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@NotThreadSafe @Slf4j
public class JerseyNodeViewBuilderVisitor extends NodeViewBuilderVisitorSupport<TextHolder, TextHolder> 
  {
    public JerseyNodeViewBuilderVisitor (final @Nonnull SiteNode siteNode) 
      {
        super(siteNode);
      }
    
    // TODO: this could be done in a ViewFactory subclass? Or an aspect?
    @Override @Nonnull
    protected TextHolder createPlaceHolderComponent (final @Nonnull Layout layout)
      {
        return new HtmlHolder("<div>Missing component: " + layout.getTypeUri() + "</div>"); // FIXME
      }

    @Override
    protected void attach (final @Nonnull TextHolder parent, final @Nonnull TextHolder child)
      {
        parent.addComponent(child);
      }
  }
