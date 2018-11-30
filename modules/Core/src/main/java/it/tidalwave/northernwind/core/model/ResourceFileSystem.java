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
package it.tidalwave.northernwind.core.model;

import javax.annotation.Nonnull;
import it.tidalwave.util.As;

/***********************************************************************************************************************
 *
 * The file system to which a {@link ResourceFile} belongs to.
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface ResourceFileSystem extends As
  {
    /*******************************************************************************************************************
     *
     * Returns the root of this file system.
     *
     * @return  the root folder
     *
     ******************************************************************************************************************/
    @Nonnull
    public ResourceFile getRoot();

    /*******************************************************************************************************************
     *
     * Returns the file with the given path
     * TODO replace with a Finder
     *
     * @param   path  the file path
     * @return        the file, null if not found
     *
     ******************************************************************************************************************/
    public ResourceFile findFileByPath (@Nonnull String path);
  }
