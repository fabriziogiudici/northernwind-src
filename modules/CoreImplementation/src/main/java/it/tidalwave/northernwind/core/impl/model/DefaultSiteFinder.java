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
package it.tidalwave.northernwind.core.impl.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.Finder8Support;
import it.tidalwave.northernwind.core.impl.util.RegexTreeMap;
import it.tidalwave.northernwind.core.model.SiteFinder;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable @ToString(callSuper = true, exclude = { "mapByRelativePath", "mapByRelativeUri" })
public class DefaultSiteFinder<T> extends Finder8Support<T, SiteFinder<T>> implements SiteFinder<T>
  {
    private static final long serialVersionUID = 3242345356779345L;

    @Nonnull
    /* package */ transient final Map<String, T> mapByRelativePath;

    @CheckForNull
    /* package */ transient final RegexTreeMap<T> mapByRelativeUri;

    @CheckForNull
    private final String relativePath;

    @CheckForNull
    private final String relativeUri;

    /*******************************************************************************************************************
     *
     * Constructor used to create an instance with the given data.
     *
     * @param finderName            the name (for debugging)
     * @param mapByRelativePath     the map of resources by relative path
     * @param mapByRelativeUri      the map of resources by relative uri
     *
     ******************************************************************************************************************/
    public DefaultSiteFinder (final @Nonnull String finderName,
                              final @CheckForNull Map<String, T> mapByRelativePath,
                              final @CheckForNull RegexTreeMap<T> mapByRelativeUri)
      {
        super(finderName);
        this.mapByRelativePath = mapByRelativePath;
        this.mapByRelativeUri = mapByRelativeUri;
        this.relativePath = null;
        this.relativeUri = null;
      }

    /*******************************************************************************************************************
     *
     * Clone constructor. This could be generated by Lombok, but we need a custom message in parameter validation.
     *
     * @param mapByRelativePath
     * @param mapByRelativeUri
     * @param relativePath
     * @param relativeUri
     *
     ******************************************************************************************************************/
    protected DefaultSiteFinder (final @CheckForNull Map<String, T> mapByRelativePath,
                                 final @CheckForNull RegexTreeMap<T> mapByRelativeUri,
                                 final @CheckForNull String relativePath,
                                 final @CheckForNull String relativeUri)
      {
        if (mapByRelativePath == null)
          {
            throw new IllegalArgumentException("Searching for a relativePath, but no map - " + this);
          }

        this.mapByRelativePath = mapByRelativePath;
        this.mapByRelativeUri = mapByRelativeUri;
        this.relativePath = relativePath;
        this.relativeUri = relativeUri;
      }

    /*******************************************************************************************************************
     *
     * Clone constructor. See documentation of {@link FinderSupport} for more information.
     *
     * @param other     the {@code Finder} to clone
     * @param override  the override object
     *
     ******************************************************************************************************************/
    // FIXME: should be protected
    public DefaultSiteFinder (final @Nonnull DefaultSiteFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final DefaultSiteFinder source = getSource(DefaultSiteFinder.class, other, override);
        this.mapByRelativePath = source.mapByRelativePath;
        this.mapByRelativeUri = source.mapByRelativeUri;
        this.relativePath = source.relativePath;
        this.relativeUri = source.relativeUri;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public SiteFinder<T> withRelativePath (final @Nonnull String relativePath)
      {
        return clone(new DefaultSiteFinder<>(mapByRelativePath, mapByRelativeUri, relativePath, relativeUri));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public SiteFinder<T> withRelativeUri (final @Nonnull String relativeUri)
      {
        return clone(new DefaultSiteFinder<>(mapByRelativePath, mapByRelativeUri, relativePath, relativeUri));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public T result()
      throws NotFoundException
      {
        try
          {
            return super.result();
          }
        catch (NotFoundException e)
          {
            String message = "????";

            if (relativePath != null)
              {
                message = String.format("relativePath: %s", relativePath);
//                message = String.format("relativePath: %s, set: %s", relativePath, mapByRelativePath.keySet());
              }
            else if (relativeUri != null)
              {
                message = String.format("relativeUri: %s", relativeUri);
//                message = String.format("relativeUri: %s, set: %s", relativeUri, mapByRelativeUri.keySet());
              }

            throw new NotFoundException(message);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends T> computeResults()
      {
        final List<T> results = new ArrayList<>();

        if (relativePath != null)
          {
            addResults(results, mapByRelativePath, relativePath);
          }

        else if (relativeUri != null)
          {
            if (mapByRelativeUri == null)
              {
                throw new IllegalArgumentException("Searching for a relativeUri, but no map - " + this);
              }

            addResults(results, mapByRelativeUri, relativeUri);
          }

        else
          {
            results.addAll(mapByRelativePath.values());
          }

        return results;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Nonnull
    private static <Type> void addResults (final @Nonnull List<Type> results,
                                           final @Nonnull Map<String, Type> map,
                                           final @Nonnull String relativePath)
      {
        if (!relativePath.contains("*")) // FIXME: better way to guess a regexp?
          {
            final Type result = map.get(relativePath);

            if (result != null)
              {
                results.add(result);
              }
          }

        else
          {
            final Pattern pattern = Pattern.compile(relativePath);

            for (final Entry<String, Type> entry : map.entrySet())
              {
                if (pattern.matcher(entry.getKey()).matches())
                  {
                    results.add(entry.getValue());
                  }
              }
          }
      }
  }
