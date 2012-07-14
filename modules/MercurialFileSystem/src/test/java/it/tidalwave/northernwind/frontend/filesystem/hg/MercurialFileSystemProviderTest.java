/***********************************************************************************************************************
 *
 * PROJECT NAME
 * PROJECT COPYRIGHT
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
 * WWW: PROJECT URL
 * SCM: PROJECT SCM
 *
 **********************************************************************************************************************/
package it.tidalwave.northernwind.frontend.filesystem.hg;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static it.tidalwave.northernwind.frontend.filesystem.hg.impl.TestRepositoryHelper.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MercurialFileSystemProviderTest 
  {
    private MercurialFileSystemProvider fixture;
    
    @BeforeMethod
    public void setupFixture()
      throws Exception
      {
        prepareSourceRepository(Option.STRIP);
        fixture = new MercurialFileSystemProvider();
        fixture.setRemoteRepositoryUrl(sourceRepository.toUri().toASCIIString());
        fixture.initialize();
      }
    
    @Test
    public void must_properly_initialize()
      throws Exception
      {
        assertThat(fixture.getCurrentTag().getName(), is("published-0.8"));
        assertThat(fixture.swapCounter, is(1));
      }
    
    @Test(dependsOnMethods="must_properly_initialize")
    public void checkForUpdates_must_do_nothing_when_there_are_no_updates()
      throws Exception
      {
        fixture.swapCounter = 0;
        
        fixture.checkForUpdates();
        
        assertThat(fixture.getCurrentTag().getName(), is("published-0.8"));
        assertThat(fixture.swapCounter, is(0));
      }
    
    @Test(dependsOnMethods="must_properly_initialize")
    public void checkForUpdates_must_update_and_fire_event_when_there_are_updates()
      throws Exception
      {
        fixture.swapCounter = 0;
        prepareSourceRepository(Option.DONT_STRIP);
        
        fixture.checkForUpdates();
        
        assertThat(fixture.getCurrentTag().getName(), is("published-0.9"));
        assertThat(fixture.swapCounter, is(1));
      }
  }
