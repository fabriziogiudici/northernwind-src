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
package it.tidalwave.northernwind.frontend.ui.component.gallery.htmltemplate.bluette;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import it.tidalwave.util.Id;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.Key;
import it.tidalwave.northernwind.core.model.ResourceProperties;
import it.tidalwave.northernwind.core.model.SiteNode;
import it.tidalwave.northernwind.core.model.SiteProvider;
import it.tidalwave.northernwind.frontend.ui.component.gallery.spi.GalleryAdapterContext;
import it.tidalwave.northernwind.frontend.ui.component.gallery.spi.GalleryAdapterSupport;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class BluetteGalleryAdapter extends GalleryAdapterSupport
  {
    private GalleryAdapterContext context;
    
    @Inject @Nonnull
    private Provider<SiteProvider> siteProvider;
    
    private final String content;
    
    public BluetteGalleryAdapter() 
      throws IOException
      {
        final Resource resource = new ClassPathResource("/" + getClass().getPackage().getName().replace('.', '/') + "/Bluette.txt");
        final @Cleanup Reader r = new InputStreamReader(resource.getInputStream());
        final char[] buffer = new char[(int)resource.contentLength()]; 
        r.read(buffer);
        content = new String(buffer);        
      }
    
    @Override // FIXME: what about @PostConstruct and injecting the context?
    public void initialize (final @Nonnull GalleryAdapterContext context) 
      {
        this.context = context;
        context.addAttribute("content", content);        
      }
    
    @Override @Nonnull
    public String getInlinedScript()
      {
        final StringBuilder builder = new StringBuilder();
        
        try 
          {
            final SiteNode siteNode = context.getSiteNode();
            final String link = siteProvider.get().getSite().createLink(siteNode.getRelativeUri() + "/images.xml");

            builder.append("<script type=\"text/javascript\">\n//<![CDATA[\n");
            builder.append(String.format("var catalogUrl = \"%s\";\n", link));
            
            final ResourceProperties bluetteConfiguration = siteNode.getPropertyGroup(new Id("bluetteConfiguration"));
            
            // FIXME: since key doesn't have dynamic type, we can't properly escape strings.
            for (final Key<?> key : bluetteConfiguration.getKeys())
              {
                final Object value = bluetteConfiguration.getProperty(key);
                builder.append(String.format("var %s = %s;\n", key.stringValue(), value));
              }

            builder.append("//]]>\n</script>\n");
          } 
        catch (NotFoundException e) 
          {
            // ok, no configuration
          }
        catch (IOException e) 
          {
            // ok, no configuration
          }
        
        return builder.toString();
      }
  }
