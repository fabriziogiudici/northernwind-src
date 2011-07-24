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
package it.tidalwave.northernwind.frontend.model.impl;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import org.openide.filesystems.FileObject;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.util.Key;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.TypeSafeHashMap;
import it.tidalwave.util.TypeSafeMap;
import it.tidalwave.northernwind.frontend.model.Resource;
import it.tidalwave.northernwind.frontend.model.Site;
import lombok.Cleanup;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j @ToString(exclude="site")
/* package */ class DefaultResource implements Resource
  {
    @Inject @Nonnull
    private Site site;
    
    @Nonnull @Getter
    private final FileObject file;    
    
    @CheckForNull
    private transient TypeSafeMap properties;

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
    public <Type> Type getProperty (@Nonnull Key<Type> key)
      throws NotFoundException, IOException
      {
        try
          { 
            return properties.get(key);
          }
        catch (NotFoundException e)
          {
            return (Type)getFileBasedProperty(key.stringValue());
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public <Type> Type getProperty (final @Nonnull Key<Type> key, final @Nonnull Type defaultValue)
      throws IOException
      {
        try
          { 
            return getProperty(key);
          }
        catch (NotFoundException e)
          {
            return defaultValue;
          }
      }
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private String getFileBasedProperty (final @Nonnull String attribute)
      throws NotFoundException, IOException
      {
        log.trace("getFileBasedProperty({})", attribute);
        final FileObject attributeFile = file.getFileObject(attribute);
        
        if (attributeFile == null)
          {
            throw new NotFoundException(file.getPath() + "/" + attribute);  
          }
        
        log.trace(">>>> reading from {}", attributeFile.getPath());
        String text = attributeFile.asText();

        // FIXME: this should be done in a specific postprocessor registered only for Content   
        // FIXME: and do this with StringTemplate - remember to escape $'s in the source
        text = text.replaceAll("\\$media\\(([^\\)]*)\\)", site.getContextPath() + "/media/$1");
        
        return text;
      }  
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    private void loadProperties()
      throws IOException
      {
        log.trace("loadProperties() for {}", file.getPath());
                
        final Map<Key<?>, Object> map = new HashMap<Key<?>, Object>();

        for (final FileObject propertyFile : getPropertyFiles())
          {
            log.trace(">>>> reading properties from {}...", propertyFile.getPath());
            @Cleanup final Reader r = new InputStreamReader(propertyFile.getInputStream());
            final Properties tempProperties = new Properties();
            tempProperties.load(r);
            log.trace(">>>> local properties: {}", tempProperties);
            r.close();        
            
            for (final Entry<Object, Object> entry : tempProperties.entrySet())
              {
                map.put(new Key<Object>(entry.getKey().toString()), entry.getValue());
              }
          }

        properties = new TypeSafeHashMap(map);
        log.info(">>>> properties: {}", properties);
      }
    
    /*******************************************************************************************************************
     *
     * Computes a list of property files to implement inheritance. Property files are enumerated starting from the root
     * up to the current folder, plus an eventual local override.
     * 
     * @return  a list of property files
     *
     ******************************************************************************************************************/
    @Nonnull
    private List<FileObject> getPropertyFiles()
      {
        log.trace("getPropertyFiles() for {}", file.getPath());
        
        final List<FileObject> files = new ArrayList<FileObject>();
        
        for (FileObject folder = this.file; folder.getParent() != null; folder = folder.getParent())
          {            
            log.trace(">>>> probing {} ...", folder);
            final FileObject propertyFile = folder.getFileObject("Resource_en.properties");
            
            if (propertyFile != null)
              {  
                files.add(propertyFile);                    
              }
          }
        
        Collections.reverse(files);

        final FileObject propertyFile = file.getFileObject("OverrideResource_en.properties");

        if (propertyFile != null)
          {  
            files.add(propertyFile);                    
          }
        
        log.trace(">>>> property file candidates: {}", files);
        
        return files;
      }
  }