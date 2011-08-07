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
package it.tidalwave.northernwind.importer.infoglue;

import it.tidalwave.northernwind.frontend.impl.ui.DefaultLayout;
import it.tidalwave.role.Marshallable;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class LayoutConverter extends Parser
  {
    private static final List<String> PROPERTIES_REFERRING_RELATIVE_PATHS = Arrays.asList("styleSheets", "items", "content", "contents", "rssFeeds", "inlinedScripts");
    
    private static final Map<String, String> TYPE_MAP = new HashMap<String, String>()
      {{
        put(  "7", "http://northernwind.tidalwave.it/component/NodeContainer/#v1.0");
        put( "67", "http://northernwind.tidalwave.it/component/Sidebar/#v1.0");
        put("104", "http://northernwind.tidalwave.it/component/Blog/#v1.0");
        put( "36", "http://northernwind.tidalwave.it/component/HorizontalMenu/#v1.0");
        put( "21", "http://northernwind.tidalwave.it/component/HtmlFragment/#v1.0");
        put( "44", "http://northernwind.tidalwave.it/component/HtmlTextWithTitle/#v1.0");
        put("853", "http://northernwind.tidalwave.it/component/StatCounter/#v1.0");
        put("883", "http://northernwind.tidalwave.it/component/Top1000Ranking/#v1.0");
        put("873", "http://northernwind.tidalwave.it/component/AddThis/#v1.0");
//        put("", "");
      }};
            
    private final SortedMap<Key<?>, Object> properties;
    private Id componentId;
    private DefaultLayout rootComponent;
    private final Stack<DefaultLayout> componentStack = new Stack<DefaultLayout>();
    private final Map<String, DefaultLayout> wrapperLayouts = new HashMap<String, DefaultLayout>();

    public LayoutConverter (final @Nonnull String xml, 
                            final @Nonnull DateTime modifiedDateTime, 
                            final @Nonnull String path,
                            final @Nonnull SortedMap<Key<?>, Object> properties) 
      {
        super(xml, path, modifiedDateTime, null);
        this.properties = properties;
      }

    @Override
    protected void processStartElement (final @Nonnull String elementName, final @Nonnull XMLStreamReader reader)
      throws Exception
      {
        log.trace("processStartElement({})", elementName);
        
        if ("component".equals(elementName))
          {
            String attrNameValue = "";
            String attrIdValue = "";
            String attrTypeValue = "";

            for (int i = 0; i < reader.getAttributeCount(); i++) // FIXME: use reader.getAttributeValue(String, String)
              {
                final String attrName = reader.getAttributeName(i).getLocalPart();
                final String attrValue = reader.getAttributeValue(i);

                if ("name".equals(attrName))
                  {
                    attrNameValue = attrValue;
                  }
                else if ("id".equals(attrName))
                  {
                    attrIdValue = attrValue;
                  }
                else if ("contentId".equals(attrName))
                  {
                    attrTypeValue = TYPE_MAP.get(attrValue);
                  }
              }
            
            if (componentStack.isEmpty())
              {
                componentId = new Id(attrNameValue);
                final DefaultLayout newComponent = new DefaultLayout(componentId, attrTypeValue);
                componentStack.push(newComponent);
                rootComponent = newComponent;  
              }
            else
              {
                DefaultLayout parentLayout = wrapperLayouts.get(attrNameValue);
                
                if (parentLayout == null)
                  {
                    parentLayout = new DefaultLayout(new Id(attrNameValue), "http://northernwind.tidalwave.it/component/Container/#v1.0"); 
                    wrapperLayouts.put(attrNameValue, parentLayout);
                    componentStack.peek().add(parentLayout);
                  }
                
                // We can't rearrange ids, as subfolders might override this stuff with non-rearranged ids
//                componentName = attrNameValue + "-" + (parentLayout.getChildren().size() + 1);
                componentId = new Id(attrNameValue + "-" + attrIdValue);
                final DefaultLayout newComponent = new DefaultLayout(componentId, attrTypeValue);
                parentLayout.add(newComponent);
                componentStack.push(newComponent);
              }

          }
        else if ("property".equals(elementName))
          {
            String propertyName = toLower(reader.getAttributeValue("", "name"));
            Object propertyValue = reader.getAttributeValue("", "path");
            
            if (propertyValue != null)
              {
                propertyValue = propertyValue.toString().replace("Top, No Local", "Top No Local");
                propertyValue = propertyValue.toString().replace("blueBill Mobile CSS", "blueBill Mobile.css");
                propertyValue = propertyValue.toString().replace("blueBill Mobile Main CSS", "blueBill Mobile Main.css");

                if (PROPERTIES_REFERRING_RELATIVE_PATHS.contains(propertyName))
                  {
                    final List<Object> values = new ArrayList<Object>();

                    for (String spl : propertyValue.toString().split(","))
                      {
                        if ("styleSheets".equals(propertyName))
                          {
                            spl = "css/" + spl.trim();  
                          }

                        spl = "/" + spl.trim();
                        spl = spl.replaceAll("/Mobile", "/"); 

                        if ("styleSheets".equals(propertyName))
                          {
                            spl = spl.replace(" ", "-").replace("(", "").replace(")", "");
                          }
                        
                        values.add(spl);
                      }

                    propertyValue = values;
                  }

                if ("styleSheets".equals(propertyName))
                  {
                    propertyName = "screenStyleSheets";  
                  }

                if ("items".equals(propertyName))
                  {
                    propertyName = "links";  
                  }

                if ("content".equals(propertyName))
                  {
                    propertyName = "contents";  
                  }
                
                properties.put(new Key<Object>(componentId + "." + propertyName), propertyValue);
              }

            else
              {
                properties.put(new Key<Object>(componentId + "." + propertyName), reader.getAttributeValue("", "path_en"));
              }
          }
      }

    @Override
    protected void processEndElement (final @Nonnull String name)
      throws Exception
      {
        log.trace("processEndElement({})", name);
        
        if ("component".equals(name))
          {
            componentStack.pop();
          }
      }

    @Override
    protected void finish() 
      throws Exception
      {
          // TODO: Infoglue generates a sub-layout even when just properties are changed. We put properties in a
          // separate file, so some of those sub-layouts have to be dropped.
          // Do this:
          //   DefaultLayout parentLayout = ...
          //   DefaultLayout thisLayout = ...
          //   DefaultLayout subLayout = parentLayout.withOverride(thisLayout);
          //   if (parentLayout.equals(subLayout)) then do not produce subLayout
          
        if (rootComponent != null) // might be empty
          {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rootComponent.as(Marshallable.class).marshal(baos);
            baos.close();
            ResourceManager.addResource(new Resource(modifiedDateTime, path, baos.toByteArray()));
          }
      }
  }
