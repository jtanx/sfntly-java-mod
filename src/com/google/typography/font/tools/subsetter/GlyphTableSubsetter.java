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
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.table.core.MaximumProfileTable;
import com.google.typography.font.sfntly.table.truetype.*;
import com.google.typography.font.sfntly.table.truetype.Glyph.GlyphType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Stuart Gill
 */
public class GlyphTableSubsetter extends TableSubsetterImpl {

  private static final boolean DEBUG = false;

  /**
   * Constructor.
   */
  protected GlyphTableSubsetter() {
    // Note: doesn't actually create the maxp table, that should be done in the
    // setUpTables method of the invoking subsetter.
    super(Tag.glyf, Tag.loca, Tag.maxp);
  }

  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder)
      throws IOException {
    if (DEBUG) {
      System.out.println("GlyphTableSubsetter.subset()");
    }
    List<Integer> permutationTable = subsetter.glyphMappingTable();
    if (permutationTable == null) {
      return false;
    }

    GlyphTable glyphTable = font.getTable(Tag.glyf);
    LocaTable locaTable = font.getTable(Tag.loca);
    if (glyphTable == null || locaTable == null) {
      throw new RuntimeException("Font to subset is not valid.");
    }

    GlyphTable.Builder glyphTableBuilder =
        (GlyphTable.Builder) fontBuilder.newTableBuilder(Tag.glyf);
    LocaTable.Builder locaTableBuilder = (LocaTable.Builder) fontBuilder.newTableBuilder(Tag.loca);
    if (glyphTableBuilder == null || locaTableBuilder == null) {
      throw new RuntimeException("Builder for subset is not valid.");
    }
    Map<Integer, Integer> inverseMap = subsetter.getInverseMapping();

    int maxPoints = 0, maxContours = 0, maxCompositePoints = 0, maxCompositeContours = 0;
    int maxSizeOfInstructions = 0;
    List<Glyph.Builder<? extends Glyph>> glyphBuilders = glyphTableBuilder.glyphBuilders();
    for (int oldGlyphId : permutationTable) {
      // TODO(stuartg): add subsetting individual glyph data - remove hints etc.

      int oldOffset = locaTable.glyphOffset(oldGlyphId);
      int oldLength = locaTable.glyphLength(oldGlyphId);
      Glyph glyph = glyphTable.glyph(oldOffset, oldLength);
      ReadableFontData data = glyph.readFontData();
      ReadableFontData renumberedData = GlyphRenumberer.renumberGlyph(data, inverseMap);
      Glyph.Builder<? extends Glyph> glyphBuilder = glyphTableBuilder.glyphBuilder(renumberedData);
      if (DEBUG) {
        System.out.println("\toldGlyphId = " + oldGlyphId);
        System.out.println("\toldOffset = " + oldOffset);
        System.out.println("\toldLength = " + oldLength);
        System.out.println("\told glyph = " + glyph);
        System.out.println("\tnew glyph builder = " + glyphBuilder);
      }
      glyphBuilders.add(glyphBuilder);
      
      maxSizeOfInstructions = Math.max(glyph.instructionSize(), maxSizeOfInstructions);
      if (glyph.glyphType() == GlyphType.Simple){
        SimpleGlyph simple  = (SimpleGlyph) glyph;
        int nPoints = 0;
        
        maxContours = Math.max(simple.numberOfContours(), maxContours);
        for (int i = 0; i < simple.numberOfContours(); i++) {
          nPoints += simple.numberOfPoints(i);
        }
        maxPoints = Math.max(nPoints, maxPoints);
      }
    }
    List<Integer> locaList = glyphTableBuilder.generateLocaList();
    if (DEBUG) {
      System.out.println("\tlocaList = " + locaList);
    }
    locaTableBuilder.setLocaList(locaList);
    MaximumProfileTable.Builder maxpBuilder =
      (MaximumProfileTable.Builder) fontBuilder.getTableBuilder(Tag.maxp);
    maxpBuilder.setNumGlyphs(locaTableBuilder.numGlyphs());
    maxpBuilder.maxPoints(maxPoints);
    maxpBuilder.setMaxContours(maxContours);
    maxpBuilder.setMaxSizeOfInstructions(maxSizeOfInstructions);
    return true; //TODO set maxp for composite values
  }
}
