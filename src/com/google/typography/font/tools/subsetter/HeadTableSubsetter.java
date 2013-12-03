package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.FontHeaderTable;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author 
 */
public class HeadTableSubsetter extends TableSubsetterImpl {
  //Offset between 00:00 1/1/1904 and 00:00 1/1/1970 in seconds
  private static final long EPOCH_OFFSET = 2082844800L;
  
  public HeadTableSubsetter() {
    super(Tag.head);
  }
  
  private static Glyph getGlyph(LocaTable locaTable, GlyphTable glyphTable, int glyphId) {
      int offset = locaTable.glyphOffset(glyphId);
      int length = locaTable.glyphLength(glyphId);

      return glyphTable.glyph(offset, length);
  }
  
  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) throws IOException {
    LocaTable locaTable = font.getTable(Tag.loca);
    GlyphTable glyphTable = font.getTable(Tag.glyf);
    List<Integer> glyphIds = subsetter.glyphMappingTable();
    if (glyphIds.isEmpty() || locaTable == null || glyphTable == null)
      return false;
    
    Glyph glyph;
    int xMin = 0, xMax = 0, yMin = 0, yMax = 0, i;
    
    for (i = 0; i < glyphIds.size(); i++) {
      glyph = getGlyph(locaTable, glyphTable, glyphIds.get(i));
      if (glyph != null && glyph.numberOfContours() != 0) {
        xMin = glyph.xMin();
        xMax = glyph.xMax();
        yMin = glyph.yMin();
        yMax = glyph.yMax();
        break;
      }
    }
    if (i == glyphIds.size())
      return false;
    
    for (; i < glyphIds.size(); i++) {
      glyph = getGlyph(locaTable, glyphTable, glyphIds.get(i));
      if (glyph != null && glyph.numberOfContours() != 0) {
        if (xMin > glyph.xMin())
          xMin = glyph.xMin();
        if (xMax < glyph.xMax())
          xMax = glyph.xMax();
        if (yMin > glyph.yMin())
          yMin = glyph.yMin();
        if (yMax < glyph.yMax())
          yMax = glyph.yMax();
      }
    }
    
    FontHeaderTable.Builder head = 
            (FontHeaderTable.Builder) fontBuilder.newTableBuilder(Tag.head, 
                font.getTable(Tag.head).readFontData());
    //long modified = System.currentTimeMillis() / 1000L + EPOCH_OFFSET;
    head.setXMin(xMin);
    head.setXMax(xMax);
    head.setYMin(yMin);
    head.setYMax(yMax);
    
    int flags = head.flagsAsInt();
    if (!font.hasTable(Tag.hdmx) && !font.hasTable(Tag.LTSH))
      flags &= ~(1<<4); //Can't be nonlinear if hdmx and LTSH are not present.
    head.setFlagsAsInt(flags);
   // head.setModified(modified);
    return true;
  }
}
