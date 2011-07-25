/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tidalwave.northernwind.infoglueexporter;

import it.tidalwave.northernwind.frontend.impl.ui.DefaultLayout;
import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author fritz
 */
@RequiredArgsConstructor
public class LayoutXmlMarshaller
  {
    @Nonnull
    private final DefaultLayout layout;
    
    public void marshall (final @Nonnull PrintWriter pw)
      throws IOException
      {
        pw.println("<layout>");
        marshall(pw, layout, "");
        pw.println("</layout>");
      } 
    
    private void marshall (final @Nonnull PrintWriter pw, final @Nonnull DefaultLayout layout, final @Nonnull String leading)
      throws IOException
      {
        pw.printf("%s<component id='%s' type='%s'>\n", leading, layout.getId(), layout.getTypeUri());
        
        for (final DefaultLayout child : layout.getChildren())
          {
            marshall(pw, child, leading + "  ");                
          }
        
        pw.printf("%s</component>\n", leading);
      }
  }
