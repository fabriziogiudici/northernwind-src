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
package it.tidalwave.northernwind.core.model.spi;

import javax.annotation.Nonnull;
import java.util.function.Supplier;
import java.time.Clock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/***********************************************************************************************************************
 *
 * A mock {@link ResponseHolder} that allows to use a mock clock for testing.
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class MockResponseHolder extends ResponseHolder<byte[]>
  {
    @Nonnull
    private final Supplier<ResponseBuilderSupport<?>> responseBuilderSupplier;

    @Getter @Setter
    private Supplier<Clock> clockSupplier = Clock::systemDefaultZone;

    public MockResponseHolder()
      {
        this(ResponseBuilderTestable::new);
      }

    @Override
    public ResponseBuilder response()
      {
        final ResponseBuilderSupport responseBuilder = responseBuilderSupplier.get();
        responseBuilder.setClockSupplier(clockSupplier);
        return responseBuilder;
      }
  }
