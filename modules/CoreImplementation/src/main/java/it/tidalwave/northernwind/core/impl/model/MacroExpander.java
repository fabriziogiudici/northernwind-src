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
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.northernwind.core.model.Site;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: $
 *
 **********************************************************************************************************************/
@Configurable(preConstruction=true) @ThreadSafe
public class MacroExpander 
  {
    @Inject @Nonnull
    protected Site site;
    
    @Nonnull
    protected String contextPath;
    
    @Nonnull
    private final Pattern pattern;
    
    public MacroExpander (final @Nonnull String regexp)
      {
        pattern = Pattern.compile(regexp);
        contextPath = site.getContextPath();
      }
    
    @Nonnull
    public String filter (final @Nonnull String text)
      {
        final Matcher matcher = pattern.matcher(text);
        final StringBuffer buffer = new StringBuffer();
        
        while (matcher.find())
          {
            matcher.appendReplacement(buffer, filter(matcher));
          }
        
        matcher.appendTail(buffer);
        
        return buffer.toString();
      }
    
    @Nonnull
    protected String filter (final @Nonnull Matcher matcher)
      {
        return "";  
      }
  }
