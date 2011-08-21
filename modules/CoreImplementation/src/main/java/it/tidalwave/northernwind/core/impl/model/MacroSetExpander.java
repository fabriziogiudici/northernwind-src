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
package it.tidalwave.northernwind.core.impl.model;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable
public class MacroSetExpander 
  {
    @Inject @Nonnull 
    private List<MacroExpander> filters;

    @PostConstruct
    /* package */ void initialize()
      {
        Collections.sort(filters, new AnnotationAwareOrderComparator());
      }
    
    @Nonnull
    public String filter (@Nonnull String text) 
      {
//        // FIXME: do this with StringTemplate - remember to escape $'s in the source
//        final String c = site.getContextPath();
//        final STGroup g = new STGroupString("",
//                "mediaLink(relativeUri) ::= " + c + "/media/$relativeUri$\n" +
//                "nodeLink(relativeUri)  ::= " + c + "$relativeUri$\n", '$', '$');
        String result = text;
        
        for (final MacroExpander filter : filters)
          {
            result = filter.filter(result);
          }
        
        return result;
      }
  }
