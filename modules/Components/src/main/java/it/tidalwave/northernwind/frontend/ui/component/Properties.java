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
package it.tidalwave.northernwind.frontend.ui.component;

import java.time.ZonedDateTime;
import java.util.List;
import it.tidalwave.util.Key;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access=PRIVATE)
public final class Properties
  {
    public static final Key<String> P_TITLE = new Key<String>("title") {};

    public static final Key<String> P_ID = new Key<String>("id") {};

    public static final Key<String> P_DESCRIPTION = new Key<String>("description") {};

    public static final Key<String> P_FULL_TEXT = new Key<String>("fullText") {};

    public static final Key<String> P_LEADIN_TEXT = new Key<String>("leadinText") {};

    public static final Key<String> P_TEMPLATE_PATH = new Key<String>("templatePath") {};

    public static final Key<String> P_WRAPPER_TEMPLATE_RESOURCE = new Key<String>("wrapperTemplate") {};

    public static final Key<ZonedDateTime> P_CREATION_DATE = new Key<ZonedDateTime>("creationDateTime") {};

    public static final Key<ZonedDateTime> P_PUBLISHING_DATE = new Key<ZonedDateTime>("publishingDateTime") {};

    public static final Key<ZonedDateTime> P_LATEST_MODIFICATION_DATE = new Key<ZonedDateTime>("latestModificationDateTime") {};

    public static final Key<String> P_CLASS = new Key<String>("class") {};

    public static final Key<String> P_DATE_FORMAT = new Key<String>("dateFormat") {};

    public static final Key<String> P_TIME_ZONE = new Key<String>("timeZone") {};

    public static final Key<List<String>> P_CONTENTS = new Key<List<String>>("contents") {};

    public static final Key<List<String>> P_TAGS = new Key<List<String>>("tags") {};
  }
