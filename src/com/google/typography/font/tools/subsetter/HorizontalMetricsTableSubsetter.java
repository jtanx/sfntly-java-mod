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
import com.google.typography.font.sfntly.table.core.HorizontalMetricsTable;
import com.google.typography.font.sfntly.table.core.OS2Table;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raph Levien
 */
public class HorizontalMetricsTableSubsetter extends TableSubsetterImpl {

  protected HorizontalMetricsTableSubsetter() {
    // Note: doesn't actually create the hhea table, that should be done in the
    // setUpTables method of the invoking subsetter.
    super(Tag.hmtx, Tag.hhea, Tag.OS_2);
  }
  
  private static Glyph getGlyph(LocaTable locaTable, GlyphTable glyphTable, int glyphId) {
      int offset = locaTable.glyphOffset(glyphId);
      int length = locaTable.glyphLength(glyphId);

      return glyphTable.glyph(offset, length);
  }
  
  /**
   * Subsets the horizontal metrics table. Also sets xAvgCharWidth in the OS/2
   * table, since it iterates through the advance widths in the process.
   * 
   * @param subsetter
   * @param font
   * @param fontBuilder
   * @return 
   */
  @Override
  public boolean subset(Subsetter subsetter, Font font, Builder fontBuilder) {
    List<Integer> permutationTable = subsetter.glyphMappingTable();
    if (permutationTable == null) {
      return false;
    }
    
    OS2Table.Builder os2 = (OS2Table.Builder)fontBuilder.getTableBuilder(Tag.OS_2);
    HorizontalMetricsTable origMetrics = font.getTable(Tag.hmtx);
    GlyphTable glyphTable = font.getTable(Tag.glyf);
    LocaTable locaTable = font.getTable(Tag.loca);
    List<HorizontalMetricsTableBuilder.LongHorMetric> metrics =
        new ArrayList<HorizontalMetricsTableBuilder.LongHorMetric>();
    int nnz = 0, widthsum = 0;
    
    for (int i = 0; i < permutationTable.size(); i++) {
      int origGlyphId = permutationTable.get(i);
      int advanceWidth = origMetrics.advanceWidth(origGlyphId);
      int lsb = origMetrics.leftSideBearing(origGlyphId);
      Glyph glyph = getGlyph(locaTable, glyphTable, origGlyphId);
      //System.out.println(glyph);
      metrics.add(new HorizontalMetricsTableBuilder.LongHorMetric(advanceWidth, lsb, glyph.xMin(), glyph.xMax(), glyph.numberOfContours()));
    
      if (advanceWidth > 0) {
        nnz++;
        widthsum += advanceWidth;
      }
    }
    
    //Technically incorrect for versions < 3
    os2.setXAvgCharWidth(nnz > 0 ? (widthsum / nnz) : 0);
    new HorizontalMetricsTableBuilder(fontBuilder, metrics).build();
    return true;
  }
}
