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
 * $Id$
 *
 * *********************************************************************************************************************
 * #L%
 */
package it.tidalwave.northernwind.importer.infoglue;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import javax.xml.stream.XMLStreamReader;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j @ToString(exclude={"publishDateTime", "expireDateTime"})
class ExportSiteNodeConverter extends Converter
  {
    private String name;

    @Getter
    private ZonedDateTime publishDateTime;

    @Getter
    private ZonedDateTime expireDateTime;

    private final ExportSiteNodeConverter parent;

    // metaInfoContentId?

    public ExportSiteNodeConverter (final @Nonnull Converter parent)
      {
        super(parent);
        this.parent = (parent instanceof ExportSiteNodeConverter) ? ((ExportSiteNodeConverter)parent) : null;
      }

    @Override
    protected void processStartElement (final @Nonnull String elementName, final @Nonnull XMLStreamReader reader)
      throws Exception
      {
        if ("childSiteNodes".equals(elementName))
          {
            new ExportSiteNodeConverter(this).process();
            localLevel--; // FIXME: doesn't properly receive the endElement for this
          }
        else if ("siteNodeVersions".equals(elementName))
          {
            new ExportSiteNodeVersionConverter(this).process();
            localLevel--; // FIXME: doesn't properly receive the endElement for this
          }
      }

    @Override
    protected void processEndElement (final @Nonnull String elementName)
      throws Exception
      {
        if ("name".equals(elementName))
          {
            name = contentAsString();
          }
        else if ("publishDateTime".equals(elementName))
          {
            publishDateTime = contentAsDateTime();
          }
        else if ("expireDateTime".equals(elementName))
          {
            expireDateTime = contentAsDateTime();
          }
      }

    @Nonnull
    public String getPath()
      {
        return ((parent != null) ? parent.getPath() : "") + "/" + name;
      }
  }
