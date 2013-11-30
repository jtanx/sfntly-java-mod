/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Font.Builder;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMap.CMapFormat;
import com.google.typography.font.sfntly.table.core.CMapFormat4;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.OS2Table;
import com.google.typography.font.sfntly.table.core.OS2Table.UnicodeRange;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raph Levien
 */
public class RenumberingCMapTableSubsetter extends TableSubsetterImpl {

  public RenumberingCMapTableSubsetter() {
    super(Tag.cmap, Tag.OS_2);
  }
 
  private static CMapFormat4 getCMapFormat4(Subsetter subsetter, Font font) {
    CMapTable cmapTable = font.getTable(Tag.cmap);
    
    for (CMapTable.CMapId cmapId : subsetter.cmapId()) {
      CMap cmap = cmapTable.cmap(cmapId);
      if (cmap.format() == CMapFormat.Format4.value())
        return (CMapFormat4) cmap;
    }
    return null;
  }
  
  Map<Integer, Integer> computeMapping(Subsetter subsetter, CMapFormat4 cmap4) {
    Map<Integer, Integer> inverseMapping = subsetter.getInverseMapping();
    Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
    Map<Integer, Integer> remap = subsetter.remappedGlyphs();
    
    for (Integer unicode : remap.keySet()) {
      int glyph = remap.get(unicode);
      if (glyph != 0 && inverseMapping.containsKey(glyph)) {
          mapping.put(unicode, inverseMapping.get(glyph));
      }
    }
    
    for (Integer unicode : cmap4) {
      if (!remap.containsKey(unicode)) {
        int glyph = cmap4.glyphId(unicode);
        if (glyph != 0 && inverseMapping.containsKey(glyph)) {
            mapping.put(unicode, inverseMapping.get(glyph));
        }
      }
    }
    return mapping;
  }
    
  @Override
  public boolean subset(Subsetter subsetter, Font font, Builder fontBuilder) throws IOException {
    CMapFormat4 cmap4 = getCMapFormat4(subsetter, font);
    if (cmap4 == null) {
      throw new RuntimeException("CMap format 4 table in source font not found");
    }
    
    Map<Integer, Integer> mapping = computeMapping(subsetter, cmap4);
    CMapTableBuilder cmapBuilder =
        new CMapTableBuilder(fontBuilder, mapping, cmap4.cmapId());
    cmapBuilder.build();
    
    //OS/2 info. OS/2 info is split across the subsetters
    OS2Table.Builder os2 = (OS2Table.Builder)fontBuilder.getTableBuilder(Tag.OS_2);
    EnumSet<UnicodeRange> unicodeRange = EnumSet.noneOf(UnicodeRange.class);
    
    int min = 0xFFFF, max = 0;
    for (int unicode : mapping.keySet()) {
      if (unicode < min)
        min = unicode;
      if (unicode > max && unicode != 0xFFFF)
        max = unicode;
      
      UnicodeRange range = OS2TableSubsetter.unicodeToRange(unicode);
      if (range != null) {
        unicodeRange.add(range);
      }
    }
    os2.setUsFirstCharIndex(min);
    os2.setUsLastCharIndex(max);
    
    //Version 0 must have these bits set to 0.
    if (os2.tableVersion() > 0)
      os2.setUlUnicodeRange(unicodeRange);
    
    return true;
  }

}
