package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapFormat4;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.OS2Table;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a format 4 symbol cmap to standard version
 */
public class CMapModifier extends TableSubsetterImpl {
  public CMapModifier() {
    super(Tag.cmap, Tag.OS_2);
  }
  
  private static CMapFormat4 getCMapFormat4(Subsetter subsetter, Font font) {
    CMapTable cmapTable = font.getTable(Tag.cmap);
    
    for (CMapTable.CMapId cmapId : subsetter.cmapId()) {
      CMap cmap = cmapTable.cmap(cmapId);
      if (cmap.format() == CMap.CMapFormat.Format4.value())
        return (CMapFormat4) cmap;
    }
    return null;
  }
  
  Map<Integer, Integer> computeMapping(Subsetter subsetter, CMapFormat4 cmap4) {
    Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
    
    for (Integer unicode : cmap4) {
      int real = unicode;
      if (cmap4.encodingId() == 0 && cmap4.platformId() == 3 && unicode >= 0xF000 && unicode <= 0xF0FF) {
        real -= 0xF000;
      }
      mapping.put(real, cmap4.glyphId(unicode));
    }
    return mapping;
  }
  
  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) throws IOException {
    CMapFormat4 cmap4 = getCMapFormat4(subsetter, font);
    if (cmap4 == null) {
      throw new RuntimeException("CMap format 4 table in source font not found");
    }
    
    Map<Integer, Integer> mapping = computeMapping(subsetter, cmap4);
    CMapTableBuilder cmapBuilder =
        new CMapTableBuilder(fontBuilder, mapping, CMapTable.CMapId.WINDOWS_BMP);//cmap4.cmapId());
    cmapBuilder.build();
    
    //OS/2 info. OS/2 info is split across the subsetters
    OS2Table.Builder os2 = (OS2Table.Builder)fontBuilder.getTableBuilder(Tag.OS_2);
    if (os2 != null) {
      EnumSet<OS2Table.UnicodeRange> unicodeRange = EnumSet.noneOf(OS2Table.UnicodeRange.class);

      int min = 0xFFFF, max = 0;
      for (int unicode : mapping.keySet()) {
        if (unicode < min)
          min = unicode;
        if (unicode > max && unicode != 0xFFFF)
          max = unicode;

        OS2Table.UnicodeRange range = OS2TableSubsetter.unicodeToRange(unicode);
        if (range != null) {
          unicodeRange.add(range);
        }
      }
      unicodeRange.remove(OS2Table.UnicodeRange.Specials);
    
      os2.setUsFirstCharIndex(min);
      os2.setUsLastCharIndex(max);
      
      //Version 0 must have these bits set to 0.
      if (os2.tableVersion() > 0) {
        long codepage = os2.ulCodePageRange1() & ~(1 << 31) | 1;
        os2.setUlCodePageRange1(codepage);
        os2.setUlUnicodeRange(unicodeRange);
      }
    }
  
    return true;
  }
  
}
