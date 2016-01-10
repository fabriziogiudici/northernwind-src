/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - git clone https://bitbucket.org/tidalwave/northernwind-src.git
 * %%
 * Copyright (C) 2011 - 2016 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.core.impl.model.mock;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Predicate;
import it.tidalwave.northernwind.core.impl.model.DefaultSiteFinder;
import it.tidalwave.util.spi.FinderSupport;
import it.tidalwave.northernwind.core.model.ResourcePath;
import it.tidalwave.northernwind.core.model.SiteFinder;
import it.tidalwave.northernwind.core.model.SiteNode;
import lombok.RequiredArgsConstructor;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class MockSiteNodeSiteFinder extends FinderSupport<SiteNode, DefaultSiteFinder<SiteNode>>
                                    implements SiteFinder<SiteNode>
  {
    private final static long serialVersionUID = 1L;

    private final String relativePath;

    private final String relativeUri;

    public MockSiteNodeSiteFinder()
      {
        this.relativePath = null;
        this.relativeUri = null;
      }

    public MockSiteNodeSiteFinder (final @Nonnull MockSiteNodeSiteFinder other, final @Nonnull Object override)
      {
        super(other, override);
        final MockSiteNodeSiteFinder source = getSource(MockSiteNodeSiteFinder.class, other, override);
        this.relativePath = source.relativePath;
        this.relativeUri = source.relativeUri;
      }

    @Override @Nonnull
    public SiteFinder<SiteNode> withRelativePath (final @Nonnull String relativePath)
      {
        return clone(new MockSiteNodeSiteFinder(relativePath, relativeUri));
      }

    @Override @Nonnull
    public SiteFinder<SiteNode> withRelativeUri (final @Nonnull String relativeUri)
      {
        return clone(new MockSiteNodeSiteFinder(relativePath, relativeUri));
      }

    @Override @Nonnull
    protected List<? extends SiteNode> computeResults()
      {
        final SiteNode content = mock(SiteNode.class);
        when(content.getRelativeUri()).thenReturn(new ResourcePath("URI-" + relativePath.substring(1)));
        return Arrays.asList(content);
      }

    @Override
    public void doWithResults (final @Nonnull Predicate<SiteNode> predicate)
      {
        throw new UnsupportedOperationException("Not supported.");
      }
  }
