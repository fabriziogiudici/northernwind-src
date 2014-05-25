/*
 * #%L
 * *********************************************************************************************************************
 *
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - hg clone https://bitbucket.org/tidalwave/northernwind-src
 * %%
 * Copyright (C) 2011 - 2014 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.core.model.spi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.InputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import it.tidalwave.util.NotFoundException;
import it.tidalwave.northernwind.core.model.ResourceFile;
import it.tidalwave.northernwind.core.model.HttpStatusException;
import it.tidalwave.northernwind.core.model.Request;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * This class holds a response object to be served. It's an abstract class: concrete descendants are supposed to 
 * create concrete responses adapting to a specific technology (e.g. Spring MVC, Jersey, etc...).
 * 
 * @param  <RESPONSE_TYPE> the produced response
 * 
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@Slf4j 
public abstract class ResponseHolder<RESPONSE_TYPE> implements RequestResettable
  {
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();
//    private final ThreadLocal<ResponseType> threadLocal = new ThreadLocal<ResponseType>();

    /*******************************************************************************************************************
     *
     * A support for a builder of {@link ResponseHolder}.
     *
     * @param <RESPONSE_TYPE>  the produced response (may change in function of the technology used for serving the
     *                         results)
     * 
     ******************************************************************************************************************/
    @NotThreadSafe // FIXME: move to Core Default Implementation
    public static abstract class ResponseBuilderSupport<RESPONSE_TYPE>
      {
        protected static final String HEADER_CONTENT_LENGTH = "Content-Length";
        protected static final String HEADER_ETAG = "ETag";
        protected static final String HEADER_CONTENT_TYPE = "Content-Type";
        protected static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
        protected static final String HEADER_LAST_MODIFIED = "Last-Modified";
        protected static final String HEADER_EXPIRES = "Expires";
        protected static final String HEADER_LOCATION = "Location";
        protected static final String HEADER_IF_NOT_MODIFIED_SINCE = "If-Modified-Since"; // FIXME: name
        protected static final String HEADER_IF_NONE_MATCH = "If-None-Match";
        protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

        protected static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

        private static final String[] DATE_FORMATS = new String[] 
          {
            PATTERN_RFC1123,
            "EEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE MMM dd HH:mm:ss yyyy"
          };

        protected Object body = new byte[0];

        protected int httpStatus = HttpServletResponse.SC_OK;

        @Nullable
        protected String requestIfNoneMatch;

        @Nullable
        protected DateTime requestIfModifiedSince;

        @Nonnull
        public abstract ResponseBuilderSupport<RESPONSE_TYPE> withHeader (@Nonnull String header, @Nonnull String value);

        /***************************************************************************************************************
         *
         * Specifies a set of headers.
         *
         * @param   headers             the headers
         * @return                      itself for fluent interface style
         * 
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withHeaders (@Nonnull Map<String, String> headers)
          {
            ResponseBuilderSupport<RESPONSE_TYPE> result = this;

            for (final Entry<String, String> entry : headers.entrySet())
              {
                result = result.withHeader(entry.getKey(), entry.getValue());
              }

            return result;
          }

        /***************************************************************************************************************
         *
         * Specifies the content type.
         * 
         * @param   contentType         the content type
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withContentType (final @Nonnull String contentType)
          {
            return withHeader(HEADER_CONTENT_TYPE, contentType);
          }

        /***************************************************************************************************************
         *
         * Specifies the content length.
         * 
         * @param  contentLength        the content length
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withContentLength (final @Nonnull long contentLength)
          {
            return withHeader(HEADER_CONTENT_LENGTH, "" + contentLength);
          }

        /***************************************************************************************************************
         *
         * Specifies the content disposition.
         * 
         * @param  contentDisposition   the content disposition
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withContentDisposition (final @Nonnull String contentDisposition)
          {
            return withHeader(HEADER_CONTENT_DISPOSITION, contentDisposition);
          }

        /***************************************************************************************************************
         *
         * Specifies the expiration time.
         * 
         * @param  duration             the duration
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withExpirationTime (final @Nonnull Duration duration)
          {
            final DateTime expirationTime = getCurrentTime().plus(duration);
            return withHeader(HEADER_EXPIRES, createFormatter(PATTERN_RFC1123).format(expirationTime.toDate()))
                  .withHeader(HEADER_CACHE_CONTROL, String.format("max-age=%d", duration.toStandardSeconds().getSeconds()));
          }

        /***************************************************************************************************************
         *
         * Specifies the latest modified time.
         * 
         * @param  time                 the time
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withLatestModifiedTime (final @Nonnull DateTime time)
          {
            return withHeader(HEADER_LAST_MODIFIED, createFormatter(PATTERN_RFC1123).format(time.toDate()))
                  .withHeader(HEADER_ETAG, String.format("\"%d\"", time.getMillis()));
          }

        /***************************************************************************************************************
         *
         * Specifies the body of the response. Accepted objects are: {@code byte[]}, {@code String}, 
         * {@code InputStream}.
         * 
         * @param  body                 the body 
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withBody (final @Nonnull Object body)
          {
            this.body = (body instanceof byte[]) ? body : 
                        (body instanceof InputStream) ? body :
                         body.toString().getBytes();
            return this;
          }

        /***************************************************************************************************************
         *
         * Specifies the body of the response as a {@link ResourceFile}.
         * 
         * @param  file                 the file
         * @return                      itself for fluent interface style
         * @throws IOException          if an error occurs when reading the file
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> fromFile (final @Nonnull ResourceFile file)
          throws IOException
          {
            final byte[] bytes = file.asBytes(); // TODO: this always loads, in some cases would not be needed

            return withContentType(file.getMimeType())
                  .withContentLength(bytes.length)
                  .withLatestModifiedTime(file.getLatestModificationTime())
                  .withBody(bytes);
          }

        /***************************************************************************************************************
         *
         * Specifies the {@link Request} we're serving - this makes it possible to read some headers and other
         * configurations needed e.g. for cache control.
         * 
         * @param  request              the request
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> forRequest (final @Nonnull Request request) 
          {    
            try // FIXME: this would be definitely better with Optional
              {
                this.requestIfNoneMatch = request.getHeader(HEADER_IF_NONE_MATCH);
              }
            catch (NotFoundException e)
              {
                // never mind  
              }

            try // FIXME: this would be definitely better with Optional
              {
                this.requestIfModifiedSince = parseDate(request.getHeader(HEADER_IF_NOT_MODIFIED_SINCE));
              }
            catch (NotFoundException e)
              {
                // never mind  
              }
            
            return this;
          }
        
        /***************************************************************************************************************
         *
         * Specifies an exception to create the response from.
         * 
         * @param  e                    the exception
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> forException (final @Nonnull NotFoundException e)
          {
            log.info("NOT FOUND: {}", e.toString());
            return forException(new HttpStatusException(HttpServletResponse.SC_NOT_FOUND));
          }

        /***************************************************************************************************************
         *
         * Specifies an exception to create the response from.
         * 
         * @param  e                    the exception
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> forException (final @Nonnull IOException e)
          {
            log.error("", e);
            return forException(new HttpStatusException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
          }

        /***************************************************************************************************************
         *
         * Specifies an exception to create the response from.
         * 
         * @param  e                    the exception
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> forException (final @Nonnull HttpStatusException e)
          {
            String message = String.format("<h1>HTTP Status: %d</h1>%n", e.getHttpStatus());

            switch (e.getHttpStatus()) // FIXME: get from a resource bundle
              {
                case HttpServletResponse.SC_MOVED_TEMPORARILY:
                  break;

                case HttpServletResponse.SC_NOT_FOUND:
                  message = "<h1>Not found</h1>";
                  break;

                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                default: // FIXME: why?
                  message = "<h1>Internal error</h1>";
                  break;
              }

            return withContentType("text/html")
                  .withHeaders(e.getHeaders())
                  .withBody(message)
                  .withStatus(e.getHttpStatus());
          }
        
        /***************************************************************************************************************
         *
         * Specifies the HTTP status.
         * 
         * @param  httpStatus           the status
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> withStatus (final @Nonnull int httpStatus)
          {
            this.httpStatus = httpStatus;
            return this;
          }
        
        /***************************************************************************************************************
         *
         * Creates a builder for a permanent redirect.
         * 
         * @param  url                  the URL of the redirect       
         * @return                      itself for fluent interface style
         *
         **************************************************************************************************************/
        @Nonnull
        public ResponseBuilderSupport<RESPONSE_TYPE> permanentRedirect (final @Nonnull String url)
          {
            return withHeader(HEADER_LOCATION, url)
                  .withStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
          }

        /***************************************************************************************************************
         *
         * Builds the response.
         * 
         * @return                              the response
         *
         **************************************************************************************************************/
        @Nonnull
        public final RESPONSE_TYPE build()
          {
            return cacheSupport().doBuild();
          }
        
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        public void put()
          {
            threadLocal.set(build());
          }
        
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nonnull
        protected abstract RESPONSE_TYPE doBuild();
        
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nullable
        protected abstract String getHeader (@Nonnull String header);
          
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nullable
        protected final DateTime getDateTimeHeader (final @Nonnull String header)
          {
            final String value = getHeader(header);
            return (value == null) ? null : parseDate(value);
          }
          
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nonnull
        protected ResponseBuilderSupport<RESPONSE_TYPE> cacheSupport()
          {
            final String eTag = getHeader(HEADER_ETAG);
            final DateTime lastModified = getDateTimeHeader(HEADER_LAST_MODIFIED);
            
            log.debug(">>>> eTag: {} - requestIfNoneMatch: {}", eTag, requestIfNoneMatch);
            log.debug(">>>> lastModified: {} - requestIfNotModifiedSince: {}", lastModified, requestIfModifiedSince);
            
            if ( ((eTag != null) && eTag.equals(requestIfNoneMatch)) ||
                 ((requestIfModifiedSince != null) && (lastModified != null) && 
                  (lastModified.isBefore(requestIfModifiedSince) || lastModified.isEqual(requestIfModifiedSince))) )
              {
                return notModified();
              }
            
            return this;
          }

        /*******************************************************************************************************************
         *
         * Returns the current time. This can be overridden for mocking time in tests.
         * 
         * @return  the current time
         *
         ******************************************************************************************************************/
        @Nonnull
        protected DateTime getCurrentTime()
          {
            return new DateTime();
          }

        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nonnull
        private ResponseBuilderSupport<RESPONSE_TYPE> notModified() 
          {
            return withBody(new byte[0])
                  .withContentLength(0)
                  .withStatus(HttpServletResponse.SC_NOT_MODIFIED);
          }
        
        /***************************************************************************************************************
         *
         * Parse a date with one of the valid formats for HTTP headers.
         * 
         * FIXME: we should try to avoid depending on this stuff...
         *
         **************************************************************************************************************/
        @Nonnull
        private DateTime parseDate (final @Nonnull String string)
          {
            for (final String dateFormat : DATE_FORMATS) 
              {
                try
                  {
                    log.debug("Parsing {} with {}...", string, dateFormat);
                    return new DateTime(createFormatter(dateFormat).parse(string));
                  }
                catch (ParseException e) 
                  {
                    log.debug("{}", e.getMessage());
                  }
              }
            
            throw new IllegalArgumentException("Cannot parse date " + string);
          }
        
        /***************************************************************************************************************
         *
         * 
         *
         **************************************************************************************************************/
        @Nonnull
        /* package */ static SimpleDateFormat createFormatter (final @Nonnull String template) 
          {
            final SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            return formatter;
          }
      }

    /*******************************************************************************************************************
     *
     * Start creating a new response.
     *
     * @return  a builder for creating the response
     * 
     ******************************************************************************************************************/
    @Nonnull
    public abstract ResponseBuilderSupport<RESPONSE_TYPE> response();

    /*******************************************************************************************************************
     *
     * Returns the response for the current thread. 
     * 
     * @return  the response
     *
     ******************************************************************************************************************/
    @Nonnull
    public RESPONSE_TYPE get()
      {
        return (RESPONSE_TYPE)threadLocal.get();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public void requestReset()
      {
        threadLocal.remove();
      }
  }
