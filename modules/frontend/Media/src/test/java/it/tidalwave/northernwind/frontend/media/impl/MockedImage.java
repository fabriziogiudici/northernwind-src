/*
 * #%L
 * *********************************************************************************************************************
 * 
 * NorthernWind - lightweight CMS
 * http://northernwind.tidalwave.it - hg clone https://bitbucket.org/tidalwave/northernwind-src
 * %%
 * Copyright (C) 2011 - 2013 Tidalwave s.a.s. (http://tidalwave.it)
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
package it.tidalwave.northernwind.frontend.media.impl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.imajine.image.EditableImage;
import org.imajine.image.metadata.Directory;
import org.imajine.image.metadata.EXIF;
import org.imajine.image.metadata.IPTC;
import org.imajine.image.metadata.TIFF;
import org.imajine.image.metadata.XMP;
import org.imajine.image.op.CreateOp;

/***********************************************************************************************************************
 *
 * @author  fritz
 * @version $Id$
 *
 **********************************************************************************************************************/
class MockedImage 
  {
    //        final TIFF tiff = mock(TIFF.class);
    //        final EXIF exif = mock(EXIF.class);
    //        final IPTC iptc = mock(IPTC.class);
    //        final XMP xmp = mock(XMP.class);
    //
    final TIFF tiff = new TIFF();
    final EXIF exif = new EXIF();
    final IPTC iptc = new IPTC();
    final XMP xmp = new XMP();
    final EditableImage image = EditableImage.create(new CreateOp(10, 10, EditableImage.DataType.BYTE)); // mock(EditableImage.class);

    public MockedImage()
      throws Exception 
      {
        // TODO: EditableImage getMetadata() can't be mocked :-( because it's final - use PowerMock?
        final Field metadataMapByClassField = image.getClass().getDeclaredField("metadataMapByClass");
        metadataMapByClassField.setAccessible(true);
        final Map<Class<? extends Directory>, List<? extends Directory>> metadataMapByClass = (Map<Class<? extends Directory>, List<? extends Directory>>) metadataMapByClassField.get(image);
        metadataMapByClass.put(TIFF.class, Collections.singletonList(tiff));
        metadataMapByClass.put(EXIF.class, Collections.singletonList(exif));
        metadataMapByClass.put(IPTC.class, Collections.singletonList(iptc));
        metadataMapByClass.put(XMP.class, Collections.singletonList(xmp));
        //        when(image.getMetadata(eq(TIFF.class))).thenReturn(tiff);
        //        when(image.getMetadata(eq(EXIF.class))).thenReturn(exif);
        //        when(image.getMetadata(eq(IPTC.class))).thenReturn(iptc);
        //        when(image.getMetadata(eq(XMP.class))).thenReturn(xmp);
      }
  }
