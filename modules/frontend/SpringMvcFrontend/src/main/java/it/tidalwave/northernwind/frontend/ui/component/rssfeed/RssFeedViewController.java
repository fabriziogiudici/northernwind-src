/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2013 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.frontend.ui.component.rssfeed;

import it.tidalwave.util.Key;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface RssFeedViewController
  {
    public static final Key<String> PROPERTY_CREATOR = new Key<String>("creator");

    public static final Key<String> PROPERTY_DESCRIPTION = new Key<String>("description");

    public static final Key<String> PROPERTY_LINK = new Key<String>("link");

    public static final Key<String> PROPERTY_TITLE = new Key<String>("title");

//    public static final Key<String> PROPERTY_X = new Key<String>("");
//        <property name="contents"
//            <values>
//                <value>/Blog</value>
//            </values>
//        </property>

//    <property name="copyright">
//        <property name="">
//        <property name="">
//        <property name="">
//        <property name="maxItems">
//        <property name="">

  }
