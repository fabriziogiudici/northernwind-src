/*
 * #%L
 * %%
 * %%
 * #L%
 */

package it.tidalwave.northernwind.core.impl.model;

import it.tidalwave.northernwind.core.model.spi.LinkPostProcessor;
import javax.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
public class MockLinkPostProcessor2 implements LinkPostProcessor
  {
    @Override @Nonnull
    public String postProcess (final @Nonnull String link) 
      {
        return String.format("lpp2-%s", link);
      }
  }
