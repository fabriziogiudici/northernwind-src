/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2012 by Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.core.impl.model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import it.tidalwave.northernwind.core.model.ResourceFile;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.northernwind.core.model.Media;
import it.tidalwave.northernwind.core.model.ModelFactory;
import it.tidalwave.northernwind.core.model.Resource;
import lombok.Delegate;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A {@code DefaultMedia} item is a document that is served as-is, without any processing. It's typically an image or such.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable(preConstruction=true) @Slf4j @ToString
/* package */ class DefaultMedia implements Media
  {
    @Nonnull @Getter @Delegate(types=Resource.class)
    private final Resource resource;

    @Inject @Nonnull
    private ModelFactory modelFactory;
    
    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DefaultMedia (final @Nonnull ResourceFile file)
      {
        resource = modelFactory.createResource(file);  
      }
  }