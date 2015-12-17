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
package it.tidalwave.northernwind.core.impl.model;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import com.google.common.base.Predicate;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.util.spi.FinderSupport;
import it.tidalwave.northernwind.core.model.SiteFinder;
import it.tidalwave.northernwind.core.impl.util.RegexTreeMap;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@ToString(callSuper = true, exclude = "mapByRelativePath")
public class DefaultSiteFinder<Type> extends FinderSupport<Type, DefaultSiteFinder<Type>> implements SiteFinder<Type>
  {
    private final static long serialVersionUID = 3242345356779345L;

    @Nonnull
    /* package */ final Map<String, Type> mapByRelativePath;

    @CheckForNull
    /* package */ final RegexTreeMap<Type> mapByRelativeUri;

    @CheckForNull
    private String relativePath;

    @CheckForNull
    private String relativeUri;

    /*******************************************************************************************************************
     *
     *
     ******************************************************************************************************************/
    public DefaultSiteFinder (final @Nonnull String name,
                              final @CheckForNull Map<String, Type> mapByRelativePath,
                              final @CheckForNull RegexTreeMap<Type> mapByRelativeUri)
      {
        super(name);

        if (mapByRelativePath == null)
          {
            throw new IllegalArgumentException("Searching for a relativePath, but no map - " + this);
          }

        this.mapByRelativePath = mapByRelativePath;
        this.mapByRelativeUri = mapByRelativeUri;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public SiteFinder<Type> withRelativePath (final @Nonnull String relativePath)
      {
        final DefaultSiteFinder<Type> clone = (DefaultSiteFinder<Type>)clone();
        clone.relativePath = relativePath;
        return clone;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public SiteFinder<Type> withRelativeUri (final @Nonnull String relativeUri)
      {
        final DefaultSiteFinder<Type> clone = (DefaultSiteFinder<Type>)clone();
        clone.relativeUri = relativeUri;
        return clone;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Type result()
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
    protected List<? extends Type> computeResults()
      {
        final List<Type> results = new ArrayList<>();

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
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void doWithResults (final @Nonnull Predicate<Type> predicate)
      {
        for (final Type object : results())
          {
            predicate.apply(object);
          }
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
