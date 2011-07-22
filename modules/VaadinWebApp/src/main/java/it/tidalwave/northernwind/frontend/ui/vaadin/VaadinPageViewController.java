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
package it.tidalwave.northernwind.frontend.ui.vaadin;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.URIHandler;
import it.tidalwave.northernwind.frontend.model.Resource;
import java.io.FileNotFoundException;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Configurable;
import it.tidalwave.northernwind.frontend.ui.PageViewController;
import it.tidalwave.northernwind.frontend.ui.spi.DefaultPageViewController;
import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.openide.filesystems.FileObject;

/***********************************************************************************************************************
 *
 * The Vaadin specialization of {@link PageViewController}.
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Configurable @Slf4j
public class VaadinPageViewController extends DefaultPageViewController
  {
    @Nonnull @Inject
    private VaadinPageView pageView;
    
    private final ThreadLocal<DownloadStream> downloadStreamHolder = new ThreadLocal<DownloadStream>();

    /*******************************************************************************************************************
     *
     * Tracks the incoming URI.
     *
     ******************************************************************************************************************/
    private final URIHandler uriHandler = new URIHandler()
      {
        @Override
        public DownloadStream handleURI (final @Nonnull URL context, final @Nonnull String relativeUri) 
          {
            try
              {
                downloadStreamHolder.set(null);
                handleUri(context, relativeUri);
                return downloadStreamHolder.get();
              }
            finally
              {
                downloadStreamHolder.set(null);
              }
          }
      };
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @PostConstruct
    private void initialize()
      {
        pageView.addURIHandler(uriHandler);
        // FIXME: seems to be registered twice? See logs
        log.info(">>>> registered URI handler: {}", uriHandler);
      }
    
    /*******************************************************************************************************************
     *
     *
     *
     ******************************************************************************************************************/
    @Override
    protected void serveResource (final @Nonnull Resource resource)
      throws FileNotFoundException
      {
        log.info("serveResource({})", resource);
        final FileObject file = resource.getFile();
        log.info(">>>> serving contents of {} ...", file.getPath());
        downloadStreamHolder.set(new DownloadStream(file.getInputStream(), null, null));
//                return new DownloadStream(file.getInputStream(), file.getNameExt(), file.getMIMEType());
                // TODO: I suppose DownloadStream closes the stream
      }
  }