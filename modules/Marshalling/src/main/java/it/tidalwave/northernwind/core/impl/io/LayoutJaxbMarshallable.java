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
package it.tidalwave.northernwind.core.impl.io;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.role.annotation.RoleImplementation;
import it.tidalwave.role.Marshallable;
import it.tidalwave.northernwind.frontend.ui.Layout;
import it.tidalwave.northernwind.core.impl.io.jaxb.ComponentJaxb;
import it.tidalwave.northernwind.core.impl.io.jaxb.ComponentsJaxb;
import it.tidalwave.northernwind.core.impl.io.jaxb.ObjectFactory;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RoleImplementation(ownerClass=Layout.class) @Configurable @Slf4j
public class LayoutJaxbMarshallable implements Marshallable 
  {
    @Nonnull
    private final Layout layout;
    
    @Inject @Nonnull
    private ObjectFactory objectFactory;

    @Inject @Nonnull
    private Marshaller marshaller;

    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    public LayoutJaxbMarshallable (final @Nonnull Layout layout) 
      {
        this.layout = layout;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void marshal (final @Nonnull OutputStream os) 
      throws IOException
      {
        try 
          {
            final ComponentsJaxb componentsJaxb = objectFactory.createComponentsJaxb();        
            componentsJaxb.setVersion("1.0");            
            componentsJaxb.setComponent(marshal(layout));
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // FIXME: set in Spring
            marshaller.marshal(objectFactory.createComponents(componentsJaxb), os);
          }
        catch (IOException | JAXBException e) 
          {
            throw new IOException("", e);
          }
      }

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private ComponentJaxb marshal (final @Nonnull Layout layout) 
      throws IOException
      {
        final ComponentJaxb componentJaxb = objectFactory.createComponentJaxb();        
        componentJaxb.setId(layout.getId().stringValue());
        componentJaxb.setType(layout.getTypeUri());

        for (final Layout child : layout.getChildren())
          {
            componentJaxb.getComponent().add(marshal(child));
          }
        
        return componentJaxb;
      }
  }