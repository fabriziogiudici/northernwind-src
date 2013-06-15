/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - hg clone https://bitbucket.org/tidalwave/northernwind-src
 * %%
 * Copyright (C) 2011 - 2013 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 * *********************************************************************************************************************
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
 * *********************************************************************************************************************
 *
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.frontend.ui.component.nodecontainer;

import java.util.List;
import it.tidalwave.util.Key;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface NodeContainerViewController
  {
    public static final Key<String> PROPERTY_TITLE_PREFIX = new Key<>("titlePrefix");

    public static final Key<List<String>> PROPERTY_SCREEN_STYLE_SHEETS = new Key<>("screenStyleSheets");

    public static final Key<List<String>> PROPERTY_PRINT_STYLE_SHEETS = new Key<>("printStyleSheets");

    public static final Key<List<String>> PROPERTY_RSS_FEEDS = new Key<>("rssFeeds");

    public static final Key<List<String>> PROPERTY_SCRIPTS = new Key<>("scripts");

    public static final Key<List<String>> PROPERTY_INLINED_SCRIPTS = new Key<>("inlinedScripts");
  }
