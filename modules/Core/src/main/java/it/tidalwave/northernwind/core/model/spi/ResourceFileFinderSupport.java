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
package it.tidalwave.northernwind.core.model.spi;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import it.tidalwave.util.spi.FinderSupport;
import it.tidalwave.northernwind.core.model.ResourceFile;
import it.tidalwave.northernwind.core.model.ResourceFile.Finder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Immutable
@RequiredArgsConstructor @ToString(callSuper = true)
public final class ResourceFileFinderSupport extends FinderSupport<ResourceFile, Finder> implements Finder
  {
    private static final long serialVersionUID = -1393470412002725841L;

    protected final transient Function<Finder, List<ResourceFile>> resultComputer;

    @Getter
    protected final boolean recursive;

    @Getter @CheckForNull
    protected final String name;

    /*******************************************************************************************************************
     *
     * Create a new implementation of {@link Finder} with the given {@link Function} to compute results.
     *
     * TODO: with Java 8, move to a default method of Finder
     *
     * @param   resultComputer  the producer of results
     * @return                  the {@code Finder}
     *
     ******************************************************************************************************************/
    public static Finder withComputeResults (final Function<Finder, List<ResourceFile>> resultComputer)
      {
        return new ResourceFileFinderSupport(resultComputer);
      }

    /*******************************************************************************************************************
     *
     * Create a new implementation of {@link Finder} with the given {@link Function} to compute results.
     *
     * TODO: with Java 8, move to a default method of Finder
     *
     * @param   finderName      the name of the {@code Finder} (for logging purpose)
     * @param   resultComputer  the producer of results
     * @return                  the {@code Finder}
     *
     ******************************************************************************************************************/
    public static Finder withComputeResults (final @Nonnull String finderName,
                                             final Function<Finder, List<ResourceFile>> resultComputer)
      {
        return new ResourceFileFinderSupport(finderName, resultComputer);
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private ResourceFileFinderSupport (final Function<Finder, List<ResourceFile>> resultComputer)
      {
        this.resultComputer = resultComputer;
        this.recursive = false;
        this.name = null;
      }

    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    private ResourceFileFinderSupport (final @Nonnull String finderName,
                                       final Function<Finder, List<ResourceFile>> resultComputer)
      {
        super(finderName);
        this.resultComputer = resultComputer;
        this.recursive = false;
        this.name = null;
      }

    /*******************************************************************************************************************
     *
     * Clone constructor. See documentation withComputeResults {@link FinderSupport} for more information.
     *
     * @param other     the {@code Finder} to clone
     * @param override  the override object
     *
     ******************************************************************************************************************/
    // FIXME: should be protected
    public ResourceFileFinderSupport (final @Nonnull ResourceFileFinderSupport other, final @Nonnull Object override)
      {
        super(other, override);
        final ResourceFileFinderSupport source = getSource(ResourceFileFinderSupport.class, other, override);
        this.resultComputer = source.resultComputer;
        this.recursive = source.recursive;
        this.name = source.name;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Finder withRecursion (final boolean recursive)
      {
        return clone(new ResourceFileFinderSupport(resultComputer, recursive, name));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    public Finder withName (final @Nonnull String name)
      {
        return clone(new ResourceFileFinderSupport(resultComputer, recursive, name));
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<? extends ResourceFile> computeResults()
      {
        return Objects.requireNonNull(resultComputer.apply(this));
      }
  }

