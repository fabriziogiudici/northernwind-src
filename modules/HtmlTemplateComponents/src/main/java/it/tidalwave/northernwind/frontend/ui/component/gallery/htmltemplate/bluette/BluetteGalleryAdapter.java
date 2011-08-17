/***********************************************************************************************************************
 *
 * PROJECT NAME
 * PROJECT COPYRIGHT
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
 * WWW: PROJECT URL
 * SCM: PROJECT SCM
 *
 **********************************************************************************************************************/
package it.tidalwave.northernwind.frontend.ui.component.gallery.htmltemplate.bluette;

import java.io.IOException;
import javax.annotation.Nonnull;
import it.tidalwave.northernwind.frontend.ui.component.gallery.htmltemplate.spi.GalleryAdapter;
import it.tidalwave.northernwind.frontend.ui.component.gallery.htmltemplate.spi.GalleryAdapterContext;
import java.io.InputStreamReader;
import java.io.Reader;
import lombok.Cleanup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class BluetteGalleryAdapter implements GalleryAdapter
  {
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
    
    @Override
    public void initialize (final @Nonnull GalleryAdapterContext context) 
      {
        context.addAttribute("screenCssSection", "      @import url(\"/nw/css/bluette.css\");\n");
        context.addAttribute("inlinedScripts", "    <script type=\"text/javascript\">\n"
           + "var catalogUrl = \"/nw/diary/2011/01/13/images.xml\";\n"
           + "var photoPrefix = \"http://stoppingdown.net/media/stillimages/\";\n"
           + "var home = \"/nw/blog/\";\n"
           + "var titlePrefix = \"Stopping Down: \";\n"
           + "var slideshowSpeed = 8000;\n"
           + "var sizes = [1920, 1280, 800, 400, 200];\n"
           + "var thumbnailsPerRow = 10;\n"
           + "var availWidthPercentage = 1.0;\n"
           + "var availHeightPercentage = 0.85;\n"
           + "var borderScale = 6.0 / 1920.0;\n"
           + "var captionFontSizeScale = 25.0 / 1280.0;\n"
           + "var headerFontSizeScale = 25.0 / 1280.0;\n"
           + "var titleVisible = true;\n"
           + "var logging = true;\n"
           + "</script>\n");
        
        context.addAttribute("content", content);        
        context.addAttribute("scripts", "<script type=\"text/javascript\" src=\"/nw/js/tracker.js\"></script>\n"
                                    + "<script type=\"text/javascript\" src=\"/nw/js/jquery/1.4.2/jquery-1.4.2.min.js\"></script>\n"
                                    + "<script type=\"text/javascript\" src=\"/nw/js/bluette/bluette.js\"></script>\n");
      }
  }
