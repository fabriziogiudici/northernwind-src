/*
 * #%L
 * *********************************************************************************************************************
 * 
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.frontend.ui.component.htmlfragment;

import javax.annotation.Nonnull;
import it.tidalwave.role.Identifiable;

/***********************************************************************************************************************
 *
 * An {@code HtmlFragmentView} is a simple text.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface HtmlFragmentView extends Identifiable
  {
    /*******************************************************************************************************************
     *
     * Sets the text content.
     *
     * @param  content  the content
     *
     ******************************************************************************************************************/
    public void setContent (@Nonnull String content);

    /*******************************************************************************************************************
     *
     * Sets the CSS class name.
     *
     * @param  className  the class name
     *
     ******************************************************************************************************************/
    public void setClassName (@Nonnull String className);
  }
