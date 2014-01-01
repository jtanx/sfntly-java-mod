package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.VerticalHeaderTable;
import com.google.typography.font.sfntly.table.core.VerticalMetricsTable;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Subsets the vertical metrics tables (vhea, vmtx)
 */
public class VerticalMetricsTableSubsetter extends TableSubsetterImpl {
  
  private static class Metrics {
    public final int advanceHeight, topSideBearing, bottomSideBearing;
    public final int yMin, yMax, yExtent, numContours;

    Metrics(int advanceHeight, int topSideBearing, Glyph glyph) {
      this.advanceHeight = advanceHeight;
      this.topSideBearing = topSideBearing;
      yMin = glyph.yMin();
      yMax = glyph.yMax();
      numContours = glyph.numberOfContours();
      bottomSideBearing = advanceHeight - topSideBearing - yMax + yMin;
      yExtent = topSideBearing + yMax - yMin;
    }
  }
  
  protected VerticalMetricsTableSubsetter() {
    super(Tag.vhea, Tag.vmtx);
  }
  
  private static Glyph getGlyph(LocaTable locaTable, GlyphTable glyphTable, int glyphId) {
      int offset = locaTable.glyphOffset(glyphId);
      int length = locaTable.glyphLength(glyphId);

      return glyphTable.glyph(offset, length);
  }
  
  /**
   * Calculates the min/max extents.
   * @see generateMetrics for more info on 'extents'
   * @param extents The extents to be calculated
   * @param metrics The metrics to be added to the calculation
   */
  private static void calculateExtents(int[] extents, Metrics metrics) {
    if (metrics.numContours != 0) {
      extents[0] = Math.max(extents[0], metrics.advanceHeight);
      extents[1] = Math.min(extents[1], metrics.topSideBearing);
      extents[2] = Math.min(extents[2], metrics.advanceHeight - metrics.topSideBearing - metrics.yMax + metrics.yMin);
      extents[3] = Math.max(extents[3], metrics.topSideBearing + metrics.yMax - metrics.yMin);
    }
  }
  
  /**
   * Generates the vertical metrics table 'vmtx'
   * @param vMetrics The metrics to be set in the table
   * @param extents A list of extents to be calculated.
   * extents[0]: maxAdvanceHeight
   * extents[1]: minTopSideBearing
   * extents[2]: minBottomSideBearing
   * extents[3]: yMaxExtent
   * extents[4]: numberOfVMetrics
   * @return The 'vmtx' table data.
   */
  private static WritableFontData generateMetrics(List<Metrics> vMetrics, int[] extents) {
    int nMetrics = vMetrics.size();
    if (nMetrics <= 0) {
      throw new IllegalArgumentException("nMetrics must be positive");
    }
    
    int lastHeight = vMetrics.get(nMetrics - 1).advanceHeight;
    int numberOfVMetrics = nMetrics;
    while (numberOfVMetrics > 1 && vMetrics.get(numberOfVMetrics - 2).advanceHeight == lastHeight) {
      numberOfVMetrics--;
    }
    
    int size = 4 * numberOfVMetrics + 2 * (nMetrics - numberOfVMetrics);
    WritableFontData data = WritableFontData.createWritableFontData(size);
    
    Metrics metrics = vMetrics.get(0);
    extents[0] = metrics.advanceHeight;
    extents[1] = metrics.topSideBearing;
    extents[2] = metrics.bottomSideBearing;
    extents[3] = metrics.yExtent;
    extents[4] = numberOfVMetrics;
    
    int i = 0, index = 0;
    for (; i < numberOfVMetrics; i++) {
      metrics = vMetrics.get(i);
      calculateExtents(extents, metrics);
      index += data.writeUShort(index, metrics.advanceHeight);
      index += data.writeShort(index, metrics.topSideBearing);
    }
    for (; i < nMetrics; i++) {
      metrics = vMetrics.get(i);
      calculateExtents(extents, metrics);
      index += data.writeShort(index, metrics.topSideBearing);
    }
    
    return data;
  }
  
  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) throws IOException {
    List<Integer> permutationTable = subsetter.glyphMappingTable();
    VerticalHeaderTable origHeader = font.getTable(Tag.vhea);
    VerticalMetricsTable origMetrics = font.getTable(Tag.vmtx);
    
    if (permutationTable == null || origHeader == null || origMetrics == null) {
      return false;
    }
    
    GlyphTable glyphTable = font.getTable(Tag.glyf);
    LocaTable locaTable = font.getTable(Tag.loca);
    List<Metrics> vMetrics = new ArrayList<Metrics>(permutationTable.size());
    
    for (int origGlyphId : permutationTable) {
      int advanceHeight = origMetrics.advanceHeight(origGlyphId);
      int topSideBearing = origMetrics.topSideBearing(origGlyphId);
      Glyph glyph = getGlyph(locaTable, glyphTable, origGlyphId);
      vMetrics.add(new Metrics(advanceHeight, topSideBearing, glyph));
    }
    
    int[] extents = new int[5];
    fontBuilder.newTableBuilder(Tag.vmtx, generateMetrics(vMetrics, extents));
    
    VerticalHeaderTable.Builder vheaBuilder = 
      (VerticalHeaderTable.Builder) fontBuilder.newTableBuilder(Tag.vhea, origHeader.readFontData());
    
    vheaBuilder.setAdvanceHeightMax(extents[0]);
    vheaBuilder.setMinTopSideBearing(extents[1]);
    vheaBuilder.setMinBottomSideBearing(extents[2]);
    vheaBuilder.setYMaxExtent(extents[3]);
    vheaBuilder.setNumberOfVMetrics(extents[4]);
    
    return true;
  }
  
}
