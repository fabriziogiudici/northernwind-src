/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - hg clone https://bitbucket.org/tidalwave/northernwind-src
 * %%
 * Copyright (C) 2011 - 2013 Tidalwave s.a.s. (http://tidalwave.it)
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

import java.util.List;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.testng.annotations.DataProvider;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class ResourcePathTest
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test
    public void must_properly_create_an_empty_path()
      {
        final ResourcePath fixture = new ResourcePath();

        assertThat(fixture.segments, is(not(nullValue())));
        assertThat(fixture.segments.isEmpty(), is(true));
        assertThat(fixture.asString(), is("/"));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "segmentProvider",
          dependsOnMethods = "must_properly_create_an_empty_path")
    public void must_properly_create_an_empty_path_from_string (final @Nonnull String path,
                                                                final @Nonnull String expectedAsString,
                                                                final @Nonnull List<String> expectedSegments)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.segments, is(not(nullValue())));
        assertThat(fixture.segments, is(expectedSegments));
        assertThat(fixture.asString(), is(expectedAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "asStringDataProvider",
          dependsOnMethods = { "must_properly_create_an_empty_path", "must_properly_create_an_empty_path_from_string" })
    public void must_properly_compute_asString (final @Nonnull List<String> segments,
                                                final @Nonnull String expectedAsString)
      {
        final ResourcePath fixture = new ResourcePath(segments);

        assertThat(fixture.asString(), is(expectedAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "relativePathProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_compute_relative_paths (final @Nonnull String path,
                                                      final @Nonnull String referencePath,
                                                      final @Nonnull String expectedPathAsString)
      {
        final ResourcePath fixture = new ResourcePath(path);
        final ResourcePath r = new ResourcePath(referencePath);
        final ResourcePath relativePath = fixture.relativeTo(r);

        assertThat(relativePath.asString(), is(expectedPathAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "leadingAndTrailingProvider")
    public void must_properly_compute_leading_segment (final @Nonnull String path,
                                                       final @Nonnull String expectedLeadingSegment,
                                                       final @Nonnull String expectedTrailingSegment)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.getLeading(), is(expectedLeadingSegment));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "leadingAndTrailingProvider")
    public void must_properly_compute_trailing_segment (final @Nonnull String path,
                                                       final @Nonnull String expectedLeadingSegment,
                                                       final @Nonnull String expectedTrailingSegment)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.getTrailing(), is(expectedTrailingSegment));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "withoutLeadingAndTrailingProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_compute_without_leading (final @Nonnull String path,
                                                       final @Nonnull String expectedWitoutLeadingPath,
                                                       final @Nonnull String expectedWithoutTrailingPath)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.withoutLeading().asString(), is(expectedWitoutLeadingPath));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "withoutLeadingAndTrailingProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_compute_without_trailing (final @Nonnull String path,
                                                        final @Nonnull String expectedWitoutLeadingPath,
                                                        final @Nonnull String expectedWithoutTrailingPath)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.withoutTrailing().asString(), is(expectedWithoutTrailingPath));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "startsWithDataProvider")
    public void must_properly_compute_startsWith (final @Nonnull String path,
                                                  final @Nonnull String leadingSegment,
                                                  final @Nonnull boolean expectedResult)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.startsWith(leadingSegment), is(expectedResult));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "appendDataProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_append1 (final @Nonnull String path,
                                       final @Nonnull String appendingPathAsString,
                                       final @Nonnull String expectedAppendedPathAsString,
                                       final @Nonnull String expectedPrependedPathAsString)
      {
        final ResourcePath fixture = new ResourcePath(path);
        final ResourcePath appendingPath = new ResourcePath(appendingPathAsString);

        assertThat(fixture.appendedWith(appendingPath).asString(), is(expectedAppendedPathAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "appendPrependDataProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_append2 (final @Nonnull String path,
                                       final @Nonnull String appendingPathAsString,
                                       final @Nonnull String expectedAppendedPathAsString,
                                       final @Nonnull String expectedPrependedPathAsString)
      {
        final ResourcePath fixture = new ResourcePath(path);
        final String[] segments = appendingPathAsString.split("/");

        assertThat(fixture.appendedWith(segments).asString(), is(expectedAppendedPathAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "appendPrependDataProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_prepend (final @Nonnull String path,
                                       final @Nonnull String appendingPathAsString,
                                       final @Nonnull String expectedAppendedPathAsString,
                                       final @Nonnull String expectedPrependedPathAsString)
      {
        final ResourcePath fixture = new ResourcePath(path);
        final String[] segments = appendingPathAsString.split("/");

        assertThat(fixture.prependedWith(segments).asString(), is(expectedPrependedPathAsString));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Test(dataProvider = "segmentProvider",
          dependsOnMethods = "must_properly_compute_asString")
    public void must_properly_count_segments (final @Nonnull String path,
                                              final @Nonnull String expectedAsString,
                                              final @Nonnull List<String> expectedSegments)
      {
        final ResourcePath fixture = new ResourcePath(path);

        assertThat(fixture.getPartsCount(), is(expectedSegments.size()));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "segmentProvider")
    private Object[][] segmentProvider()
      {
        return new Object[][]
          {
          //  path              exp. asString       exp. segments
            { "/foo",           "/foo",             asList("foo")               },
            { "/foo/bar",       "/foo/bar",         asList("foo", "bar")        },
            { "/foo/bar/baz",   "/foo/bar/baz",     asList("foo", "bar", "baz") }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "relativePathProvider")
    private Object[][] relativePathProvider()
      {
        return new Object[][]
          {
          //  path            parent      relative
            { "/foo/bar/baz", "/foo/bar", "/baz" }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "leadingAndTrailingProvider")
    private Object[][] leadingAndTrailingProvider()
      {
        return new Object[][]
          {
          // path             leading   trailing
//            { "/",            "", "" }
            { "/foo",         "foo",    "foo" },
            { "/foo/bar",     "foo",    "bar" },
            { "/foo/bar/baz", "foo",    "baz" }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "withoutLeadingAndTrailingProvider")
    private Object[][] withoutLeadingAndTrailingProvider()
      {
        return new Object[][]
          {
          // path             w/o leading   w/o trailing
//            { "/",            "", "" }
            { "/foo",         "/",          "/" },
            { "/foo/bar",     "/bar",       "/foo" },
            { "/foo/bar/baz", "/bar/baz",   "/foo/bar" }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "startsWithDataProvider")
    private Object[][] startsWithDataProvider()
      {
        return new Object[][]
          {
            { "/foo",         "foo",  true },
            { "/foo/bar",     "foo",  true },
            { "/foo/bar/baz", "foo",  true },

            { "/foo",         "foot", false },
            { "/foo/bar",     "foot", false },
            { "/foo/bar/baz", "foot", false },

            { "/foo",         "",     false }
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "asStringDataProvider")
    private Object[][] asStringDataProvider()
      {
        return new Object[][]
          {
            { asList(""),                   "/"},
            { asList("foo"),                "/foo"},
            { asList("foo", "bar"),         "/foo/bar"},
            { asList("foo", "bar", "baz"),  "/foo/bar/baz"},
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @DataProvider(name = "appendPrependDataProvider")
    private Object[][] appendPrependDataProvider()
      {
        return new Object[][]
          {
          //  path            param    appended              prepended
            { "/foo",         "a",     "/foo/a",             "/a/foo"             },
            { "/foo",         "a/b",   "/foo/a/b",           "/a/b/foo"           },
            { "/foo/bar",     "a",     "/foo/bar/a",         "/a/foo/bar"         },
            { "/foo/bar",     "a/b",   "/foo/bar/a/b",       "/a/b/foo/bar"       },
            { "/foo/bar/baz", "a",     "/foo/bar/baz/a",     "/a/foo/bar/baz"     },
            { "/foo/bar/baz", "a/b",   "/foo/bar/baz/a/b",   "/a/b/foo/bar/baz"   },
            { "/foo/bar/baz", "a/b/c", "/foo/bar/baz/a/b/c", "/a/b/c/foo/bar/baz" },
          };
      }
  }