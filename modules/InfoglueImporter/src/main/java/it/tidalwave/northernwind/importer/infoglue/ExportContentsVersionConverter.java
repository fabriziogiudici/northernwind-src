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

import it.tidalwave.northernwind.core.impl.util.UriUtilities;
import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamReader;
import lombok.Getter;
import org.joda.time.DateTime;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
class ExportContentsVersionConverter extends Converter
  {
    private int stateId;

    @Getter 
    private DateTime modifiedDateTime;
 
    @Getter 
    private String versionComment;
    
    private boolean checkedOut;
    
    private boolean active;
    
    @Getter 
    private String versionModifier;
    
    private String escapedVersionValue;
    
    private String languageCode;
    
    private final ExportContentConverter parent;
    
    public ExportContentsVersionConverter (final @Nonnull ExportContentConverter parent)
      {
        super(parent);        
        this.parent = parent;
      }

    @Override
    protected void processStartElement (final @Nonnull String elementName, final @Nonnull XMLStreamReader reader)
      throws Exception
      {
        if ("digitalAssets".equals(elementName))
          {
            new ExportDigitalAssetsConverter(this).process();  
            localLevel--; // FIXME: doesn't properly receive the endElement for this
          }
      }
    
    @Override
    protected void processEndElement (final @Nonnull String elementName)
      throws Exception
      {
        if ("stateId".equals(elementName))
          {
            stateId = contentAsInteger();  
          }
        else if ("modifiedDateTime".equals(elementName))
          {
            modifiedDateTime = contentAsDateTime();  
          }
        else if ("versionComment".equals(elementName))
          {
            versionComment = contentAsString();  
          }
        else if ("isCheckedOut".equals(elementName))
          {
            checkedOut = contentAsBoolean();  
          }
        else if ("isActive".equals(elementName))
          {
            active = contentAsBoolean();  
          }
        else if ("versionModifier".equals(elementName))
          {
            versionModifier = contentAsString();  
          }
        else if ("escapedVersionValue".equals(elementName))
          {
            escapedVersionValue = contentAsString();  
          }
        else if ("languageCode".equals(elementName))
          {
            languageCode = contentAsString();  
          }
      }

    @Override
    protected void finish() throws Exception 
      {
        log.info("Now process {} stateId: {}, checkedOut: {}, active: {}, {} {}", 
                new Object[] { parent.getPath(), stateId, checkedOut, active, modifiedDateTime, versionComment });
        String path = parent.getPath() + "/";
      
        // FIXME for blueBill Mobile, put in configuration
//        if (path.equals("/blueBill/License/"))
//          {
//            path = "/blueBill/Mobile/License/";   
//          }
//        else if (path.equals("/blueBill/Mobile/Contact/"))
//          {
//            path = "/blueBill/Mobile/Contacts/";   
//          }
//        else if (path.equals("/blueBill/Meta info folder/blueBill/_Standard Pages/Contacts Metainfo/"))
//          {
//            path = "/blueBill/Meta info folder/blueBill/Mobile/_Standard Pages/Contacts Metainfo/";   
//          }
        // END FIXME for blueBill Mobile, put in configuration

        final String content = escapedVersionValue.replace("cdataEnd", "]]>");
        Main.contentMap.put(parent.getId(), modifiedDateTime, languageCode, content);
  
        if (!path.matches(Main.contentPrefix + ".*"))
          {
            log.warn("Ignoring content: {}", path);
          }
        else
          {
            path = path.replaceAll(Main.contentPrefix, "");
            path = "/content/document" + path;
            log.info("PBD " + parent.getPublishDateTime() + " " + path);
            // FIXME: creationDate
            
            if (!"".equals(content))
              {
                new ContentParser(content, 
                                  modifiedDateTime, 
                                  parent.getPublishDateTime(),
                                  UriUtilities.urlEncodedPath(path) + "/", 
                                  languageCode,
                                  versionComment)
                        .process();
              }
          }
      }

    @Override @Nonnull
    public String toString() 
      {
        return String.format("ExportContentsVersionConverter(%s)", parent.getPath());
      }
  }
