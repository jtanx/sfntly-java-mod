/*
 * Copyright 2010 Google Inc. All Rights Reserved.
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

package com.google.typography.font.sfntly.table.core;

import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.TableBasedTableBuilder;

/**
 * A Vertical Metrics table - 'hmtx'.
 *
 * @author Stuart Gill
 */
public final class VerticalMetricsTable extends Table {

  private int numVMetrics;
  private int numGlyphs;

  /**
   * Offsets to specific elements in the underlying data. These offsets are relative to the
   * start of the table or the start of sub-blocks within the table.
   */
  private enum Offset {
    // vMetrics
    vMetricsStart(0), vMetricsSize(4),

    // Offsets within a vMetric
    vMetricsAdvanceHeight(0),
    vMetricsTopSideBearing(2),

    TopSideBearingSize(2);

    private final int offset;

    private Offset(int offset) {
      this.offset = offset;
    }
  }

  private VerticalMetricsTable(
      Header header, ReadableFontData data, int numVMetrics, int numGlyphs) {
    super(header, data);
    this.numVMetrics = numVMetrics;
    this.numGlyphs = numGlyphs;
  }

  public int numberOfVMetrics() {
    return this.numVMetrics;
  }

  public int numberOfTSBs() {
    return this.numGlyphs - this.numVMetrics;
  }

  public int vMetricAdvanceHeight(int entry) {
    if (entry > this.numVMetrics) {
      throw new IndexOutOfBoundsException();
    }
    int offset = 
      Offset.vMetricsStart.offset + 
      (entry * Offset.vMetricsSize.offset) + Offset.vMetricsAdvanceHeight.offset;
    return this.data.readUShort(offset);
  }

  public int vMetricTSB(int entry) {
    if (entry > this.numVMetrics) {
      throw new IndexOutOfBoundsException();
    }
    int offset = 
      Offset.vMetricsStart.offset + 
      (entry * Offset.vMetricsSize.offset) + Offset.vMetricsTopSideBearing.offset;
    return this.data.readShort(offset);
  }

  public int lsbTableEntry(int entry) {
    if (entry > this.numberOfTSBs()) {
      throw new IndexOutOfBoundsException();
    }
    int offset = 
      Offset.vMetricsStart.offset + 
      (this.numVMetrics * Offset.vMetricsSize.offset) + (entry * Offset.TopSideBearingSize.offset);
    return this.data.readShort(offset);

  }

  public int advanceHeight(int glyphId) {
    if (glyphId < this.numVMetrics) {
      return this.vMetricAdvanceHeight(glyphId);
    }
    return this.vMetricAdvanceHeight(this.numVMetrics - 1);
  }

  public int topSideBearing(int glyphId) {
    if (glyphId < this.numVMetrics) {
      return this.vMetricTSB(glyphId);
    }
    return this.lsbTableEntry(glyphId - this.numVMetrics);
  }

  /**
   * Builder for a Vertical Metrics Table - 'vmtx'.
   *
   */
  public static class 
  Builder extends TableBasedTableBuilder<VerticalMetricsTable> {
    private int numVMetrics = -1;
    private int numGlyphs = -1;

    /**
     * Create a new builder using the header information and data provided.
     *
     * @param header the header information
     * @param data the data holding the table
     * @return a new builder
     */
    public static Builder createBuilder(Header header, WritableFontData data) {
      return new Builder(header, data);
    }
    
    protected Builder(Header header, WritableFontData data) {
      super(header, data);
    }

    protected Builder(Header header, ReadableFontData data) {
      super(header, data);
    }

    @Override
    protected VerticalMetricsTable subBuildTable(ReadableFontData data) {
      return new VerticalMetricsTable(this.header(), data, this.numVMetrics, this.numGlyphs);
    }

    public void setNumberOfVMetrics(int numVMetrics) {
      if (numVMetrics < 0) {
        throw new IllegalArgumentException("Number of metrics can't be negative.");
      }
      this.numVMetrics = numVMetrics;
      this.table().numVMetrics = numVMetrics;
    }

    public void setNumGlyphs(int numGlyphs) {
      if (numGlyphs < 0) {
        throw new IllegalArgumentException("Number of glyphs can't be negative.");        
      }
      this.numGlyphs = numGlyphs;
      this.table().numGlyphs = numGlyphs;
    }
  }
}
