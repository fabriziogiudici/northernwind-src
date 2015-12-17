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
package it.tidalwave.northernwind.core.model.vaadin.urihandler;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Scope;
import it.tidalwave.northernwind.core.model.spi.DefaultMediaRequestProcessor;
import com.vaadin.terminal.DownloadStream;
import static org.springframework.core.Ordered.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Scope(value = "session") @Order(HIGHEST_PRECEDENCE+1)
public class VaadinMediaRequestProcessor extends DefaultMediaRequestProcessor<DownloadStream>
  {
//    @Override @Nonnull
//    protected void createResponse (final @Nonnull ResourceFile file)
//      throws FileNotFoundException
//      {
//        //DownloadStream(file.getInputStream(), file.getNameExt(), file.getMIMEType()); // FIXME: set name?
//        responseHolder.response().withBody(file.getInputStream()).withContentType(file.getMimeType()).put();
//      }
  }
