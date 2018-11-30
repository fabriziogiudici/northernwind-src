/*
 * #%L
 * *********************************************************************************************************************
 * 
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2018 Tidalwave s.a.s. (http://tidalwave.it)
 * %%
 * *********************************************************************************************************************
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
 * *********************************************************************************************************************
 * 
 * 
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.frontend.media.impl;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.util.Id;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.core.model.ResourceProperties;

/***********************************************************************************************************************
 *
 * This service implements a {@link Metadata} provider with a cache policy for reducing the accesses to the actual data 
 * provider.
 * 
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface MetadataCache 
  {
    /*******************************************************************************************************************
     *
     * Finds a {@link Metadata} item for the given id. The returned item could be cached.
     *
     * @param  mediaId            the media id
     * @param  properties         some configuration properties
     * @return                    the {@code Metadata}
     * @throws NotFoundException  if no {@code Metadata} is found
     *
     ******************************************************************************************************************/
    @Nonnull
    public Metadata findMetadataById (@Nonnull Id mediaId, @Nonnull ResourceProperties properties)
      throws NotFoundException, IOException;
  }
