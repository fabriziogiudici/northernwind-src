/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2015 Tidalwave s.a.s. (http://tidalwave.it)
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
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.core.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.nio.file.spi.FileSystemProvider;
import it.tidalwave.util.Finder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Wither;

/***********************************************************************************************************************
 *
 * The model for the whole site, it contains a collection of {@link Content}s, {@link Media} items and
 * {@link SiteNode}s.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public interface Site
  {
    /*******************************************************************************************************************
     *
     * A builder of a {@link Site}.
     *
     ******************************************************************************************************************/
    @AllArgsConstructor(access = AccessLevel.PRIVATE) @RequiredArgsConstructor
    @Getter @ToString(exclude = "callBack")
    public final class Builder
      {
        // Workaround for a Lombok limitation with Wither and subclasses
        public static interface CallBack
          {
            @Nonnull
            public Site build (@Nonnull Builder builder);
          }

        @Nonnull
        private final ModelFactory modelFactory;

        @Nonnull
        private final CallBack callBack;

        @Wither
        private String contextPath;
        @Wither
        private String documentPath;
        @Wither
        private String mediaPath;
        @Wither
        private String libraryPath;
        @Wither
        private String nodePath;
        @Wither
        private boolean logConfigurationEnabled;
        @Wither
        private List<Locale> configuredLocales;
        @Wither
        private List<String> ignoredFolders;

        @Nonnull
        public Site build()
          {
            return callBack.build(this);
          }
      }

    /*******************************************************************************************************************
     *
     * Returns the context path for this web site.
     *
     ******************************************************************************************************************/
    @Nonnull
    public String getContextPath();

    /*******************************************************************************************************************
     *
     * Creates an HTML link to the node mapped to the given URI.
     *
     * @param  relativeUri  the target URI
     * @return              the link
     *
     ******************************************************************************************************************/
    @Nonnull
    public String createLink (@Nonnull ResourcePath relativeUri);

    /*******************************************************************************************************************
     *
     * Finds something.
     *
     * @param  type  the type of thing to find
     * @return       the {@link Finder} for that thing.
     *
     ******************************************************************************************************************/
    @Nonnull
    public <Type> SiteFinder<Type> find (@Nonnull Class<Type> type);

    /*******************************************************************************************************************
     *
     * Returns the {@link FileSystemProvider} used by this {@code Site}.
     *
     * @return  the {@code FileSystemProvider}
     *
     ******************************************************************************************************************/
    @Nonnull
    public ResourceFileSystemProvider getFileSystemProvider();

    /*******************************************************************************************************************
     *
     * Returns the {@link Locale}s configured for this site.
     *
     * @return   the {@code Locale}s.
     *
     ******************************************************************************************************************/
    @Nonnull
    public List<Locale> getConfiguredLocales();
  }
