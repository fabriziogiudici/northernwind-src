/***********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * Copyright (C) 2011-2011 by Tidalwave s.a.s. (http://www.tidalwave.it)
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
 * WWW: http://northernwind.java.net
 * SCM: http://java.net/hg/northernwind~src
 *
 **********************************************************************************************************************/
package it.tidalwave.northernwind.frontend.model.spi;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Locale;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.northernwind.frontend.model.Request;
import it.tidalwave.northernwind.frontend.model.RequestProcessor;
import it.tidalwave.northernwind.frontend.model.Site;
import it.tidalwave.northernwind.frontend.impl.model.DefaultRequestLocaleManager;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Order(1000) @Slf4j
public class HeaderLanguageOverrideRequestProcessor implements RequestProcessor
  {
    @Inject @Nonnull
    private DefaultRequestLocaleManager requestLocaleManager;

    @Inject @Nonnull
    private Site site;

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public boolean process (final @Nonnull Request request) 
      {
        for (final Locale locale : request.getPreferredLocales())
          {
            if (site.getConfiguredLocales().contains(locale))
              {
                requestLocaleManager.setRequestLocale(locale);
                break;
              }
          }

        return false;
      }
  }
