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
package it.tidalwave.northernwind.frontend.util;

import javax.annotation.Nonnull;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.ContextLoaderListener;

/***********************************************************************************************************************
 *
 * A decorator for the Spring {@code ContextLoaderListener} that catches any error occurring at Spring boot and makes
 * it available to the {@link InitializationDiagnosticsFilter}.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j
public class InitializationDiagnosticsServletContextListenerDecorator implements ServletContextListener
  {
    public static final String ATTRIBUTE_BOOT_THROWABLE = "it.tidalwave.northernwind.bootThrowable";
    
    private final ContextLoaderListener delegate = new ContextAttributeContextLoaderListener();

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void contextInitialized (final @Nonnull ServletContextEvent event)
      {
        try
          {
            delegate.contextInitialized(event);
          }
        catch (Throwable t)
          {
            log.error("Cannot initialize Spring", t);  
            event.getServletContext().setAttribute(ATTRIBUTE_BOOT_THROWABLE, t);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void contextDestroyed (final @Nonnull ServletContextEvent event) 
      {
        delegate.contextDestroyed(event);
      }
  }
