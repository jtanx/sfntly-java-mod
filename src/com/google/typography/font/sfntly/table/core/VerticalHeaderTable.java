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
 * A Vertical Header table - 'vhea'.
 *
 * @author Stuart Gill
 */
public final class VerticalHeaderTable extends Table {

  /**
   * Offsets to specific elements in the underlying data. These offsets are relative to the
   * start of the table or the start of sub-blocks within the table.
   */
  private enum Offset {
    version(0),
    Ascender(4),
    Descender(6),
    LineGap(8),
    advanceHeightMax(10),
    minTopSideBearing(12),
    minBottomSideBearing(14),
    yMaxExtent(16),
    caretSlopeRise(18),
    caretSlopeRun(20),
    caretOffset(22),
    metricDataFormat(32),
    numberOfVMetrics(34);

    private final int offset;

    private Offset(int offset) {
      this.offset = offset;
    }
  }

  private VerticalHeaderTable(Header header, ReadableFontData data) {
    super(header, data);
  }

  public int tableVersion() {
    return this.data.readFixed(Offset.version.offset);
  }

  public int ascender() {
    return this.data.readShort(Offset.Ascender.offset);
  }

  public int descender() {
    return this.data.readShort(Offset.Descender.offset);
  }

  public int lineGap() {
    return this.data.readShort(Offset.LineGap.offset);
  }

  public int advanceHeightMax() {
    return this.data.readUShort(Offset.advanceHeightMax.offset);
  }

  public int minTopSideBearing() {
    return this.data.readShort(Offset.minTopSideBearing.offset);
  }

  public int minBottomSideBearing() {
    return this.data.readShort(Offset.minBottomSideBearing.offset);
  }

  public int yMaxExtent() {
    return this.data.readShort(Offset.yMaxExtent.offset);
  }

  public int caretSlopeRise() {
    return this.data.readShort(Offset.caretSlopeRise.offset);
  }

  public int caretSlopeRun() {
    return this.data.readShort(Offset.caretSlopeRun.offset);
  }

  public int caretOffset() {
    return this.data.readShort(Offset.caretOffset.offset);
  }

  // TODO(stuartg): an enum?
  public int metricDataFormat() {
    return this.data.readShort(Offset.metricDataFormat.offset);
  }

  public int numberOfVMetrics() {
    return this.data.readUShort(Offset.numberOfVMetrics.offset);
  }

  /**
   * Builder for a Vertical Header table - 'hhea'.
   *
   */
  public static class Builder
  extends TableBasedTableBuilder<VerticalHeaderTable> {

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
    protected VerticalHeaderTable subBuildTable(ReadableFontData data) {
      return new VerticalHeaderTable(this.header(), data);
    }

    public int tableVersion() {
      return this.internalReadData().readFixed(Offset.version.offset);
    }

    public void setTableVersion(int version) {
      this.internalWriteData().writeFixed(Offset.version.offset, version);
    }

    public int ascender() {
      return this.internalReadData().readShort(Offset.Ascender.offset);
    }

    public void setAscender(int version) {
      this.internalWriteData().writeShort(Offset.Ascender.offset, version);
    }

    public int descender() {
      return this.internalReadData().readShort(Offset.Descender.offset);
    }

    public void setDescender(int version) {
      this.internalWriteData().writeShort(Offset.Descender.offset, version);
    }

    public int lineGap() {
      return this.internalReadData().readShort(Offset.LineGap.offset);
    }

    public void setLineGap(int version) {
      this.internalWriteData().writeShort(Offset.LineGap.offset, version);
    }

    public int advanceHeightMax() {
      return this.internalReadData().readUShort(Offset.advanceHeightMax.offset);
    }

    public void setAdvanceHeightMax(int version) {
      this.internalWriteData().writeUShort(Offset.advanceHeightMax.offset, version);
    }

    public int minTopSideBearing() {
      return this.internalReadData().readShort(Offset.minTopSideBearing.offset);
    }

    public void setMinTopSideBearing(int version) {
      this.internalWriteData().writeShort(Offset.minTopSideBearing.offset, version);
    }

    public int minBottomSideBearing() {
      return this.internalReadData().readShort(Offset.minBottomSideBearing.offset);
    }

    public void setMinBottomSideBearing(int version) {
      this.internalWriteData().writeShort(Offset.minBottomSideBearing.offset, version);
    }

    public int yMaxExtent() {
      return this.internalReadData().readShort(Offset.yMaxExtent.offset);
    }

    public void setYMaxExtent(int version) {
      this.internalWriteData().writeShort(Offset.yMaxExtent.offset, version);
    }

    public int caretSlopeRise() {
      return this.internalReadData().readUShort(Offset.caretSlopeRise.offset);
    }

    public void setCaretSlopeRise(int version) {
      this.internalWriteData().writeUShort(Offset.caretSlopeRise.offset, version);
    }

    public int caretSlopeRun() {
      return this.internalReadData().readUShort(Offset.caretSlopeRun.offset);
    }

    public void setCaretSlopeRun(int version) {
      this.internalWriteData().writeUShort(Offset.caretSlopeRun.offset, version);
    }

    public int caretOffset() {
      return this.internalReadData().readUShort(Offset.caretOffset.offset);
    }

    public void setCaretOffset(int version) {
      this.internalWriteData().writeUShort(Offset.caretOffset.offset, version);
    }

    // TODO(stuartg): an enum?
    public int metricDataFormat() {
      return this.internalReadData().readUShort(Offset.metricDataFormat.offset);
    }

    public void setMetricDataFormat(int version) {
      this.internalWriteData().writeUShort(Offset.metricDataFormat.offset, version);
    }

    public int numberOfVMetrics() {
      return this.internalReadData().readUShort(Offset.numberOfVMetrics.offset);
    }

    public void setNumberOfVMetrics(int version) {
      this.internalWriteData().writeUShort(Offset.numberOfVMetrics.offset, version);
    }
  }
}
