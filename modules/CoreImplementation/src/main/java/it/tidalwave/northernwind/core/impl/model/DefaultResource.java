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
import java.util.Locale;
import java.io.InputStream;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Id;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.core.model.RequestLocaleManager;
import it.tidalwave.northernwind.core.model.Resource;
import it.tidalwave.northernwind.core.model.ResourceProperties;
import lombok.Cleanup;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.role.Unmarshallable.Unmarshallable;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j @ToString(exclude={"site", "localeRequestManager", "properties", "propertyResolver"})
/* package */ class DefaultResource implements Resource
  {
    @Inject @Nonnull
    private RequestLocaleManager localeRequestManager;
    
    @Nonnull @Getter
    private final FileObject file;    
    
    @Nonnull @Getter
    private ResourceProperties properties;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private DefaultResourceProperties.PropertyResolver propertyResolver = new DefaultResourceProperties.PropertyResolver()
      {
        @Override
        public <Type> Type resolveProperty (final @Nonnull Id propertyGroupId, final @Nonnull Key<Type> key) 
          throws NotFoundException, IOException
          {
            return (Type)getFileBasedProperty(key.stringValue()); // FIXME: use also Id
          }
      };
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DefaultResource (final @Nonnull FileObject file) 
      {
        this.file = file;
      }
    
    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public ResourceProperties getPropertyGroup (final @Nonnull Id id)
      throws NotFoundException
      {
        return properties.getGroup(id);   
      }
    
    /*******************************************************************************************************************
     *
     * 
     *
     ******************************************************************************************************************/
    @Nonnull
    private String getFileBasedProperty (final @Nonnull String propertyName)
      throws NotFoundException, IOException
      {
        log.trace("getFileBasedProperty({})", propertyName);
        
        final FileObject propertyFile = findLocalizedFile(propertyName);
        log.trace(">>>> reading from {}", propertyFile.getPath());
        final MacroSetExpander macroExpander = new MacroSetExpander(); // FIXME: inject
        
        return macroExpander.filter(propertyFile.asText());
      }  
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    /* package */ void loadProperties()
      throws IOException
      {
        log.trace("loadProperties() for /{}", file.getPath());
                
        properties = new DefaultResourceProperties(new Id(""), propertyResolver);

        for (final FileObject propertyFile : Utilities.getInheritedPropertyFiles(file, "Properties_en.xml"))
          {
            log.trace(">>>> reading properties from /{}...", propertyFile.getPath());
            @Cleanup final InputStream is = propertyFile.getInputStream();
            final ResourceProperties tempProperties = new DefaultResourceProperties(propertyResolver).as(Unmarshallable).unmarshal(is);
            log.trace(">>>>>>>> read properties: {}", tempProperties);
            properties = properties.merged(tempProperties);
          }

        if (log.isDebugEnabled())
          {
            log.debug(">>>> properties for /{}:", file.getPath());
            logProperties(">>>>>>>>", properties);
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private FileObject findLocalizedFile (final @Nonnull String fileName)
      throws NotFoundException
      {
        FileObject localizedFile = null;
        final StringBuilder fileNamesNotFound = new StringBuilder();
        String separator = "";
        
        for (final Locale locale : localeRequestManager.getLocales())
          {
            final String localizedFileName = fileName.replace(".", "_" + locale.getLanguage() + ".");
            localizedFile = file.getFileObject(localizedFileName);
            
            if (localizedFile != null)
              {
                break;  
              }
            
            fileNamesNotFound.append(separator);
            fileNamesNotFound.append(localizedFileName);
            separator = ",";
          }

        return NotFoundException.throwWhenNull(localizedFile, String.format("%s/{%s}", file.getPath(), fileNamesNotFound));  
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    private void logProperties (final @Nonnull String indent ,final @Nonnull ResourceProperties properties)
      {
        log.debug("{} simple property items:", indent);
        
        for (final Key<?> key : properties.getKeys())
          {
            try 
              {
                log.debug("{}>>>> {} = {}", new Object[] { indent, key, properties.getProperty(key) });
              }
            catch (NotFoundException e) 
              {
                log.error("", e);
              }
            catch (IOException e) 
              {
                log.error("", e);
              }
          }
        
        log.debug("{} property groups: {}", indent, properties.getGroupIds());
        
        for (final Id groupId : properties.getGroupIds())
          {
            log.debug("{}>>>> group: {}", indent, groupId);
            
            try 
              {
                logProperties(indent + ">>>>", properties.getGroup(groupId));
              }
            catch (NotFoundException e) 
              {
                log.error("", e);
              }
          }
      }
  }
