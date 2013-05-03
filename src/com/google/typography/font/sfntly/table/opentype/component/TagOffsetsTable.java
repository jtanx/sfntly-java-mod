// Copyright 2012 Google Inc. All Rights Reserved.

package com.google.typography.font.sfntly.table.opentype.component;

import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.FontDataTable;
import com.google.typography.font.sfntly.table.SubTable;
import com.google.typography.font.sfntly.table.opentype.component.TagOffsetRecord;
import com.google.typography.font.sfntly.table.opentype.component.TagOffsetRecordList;
import com.google.typography.font.sfntly.table.opentype.featuretable.Header;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public abstract class TagOffsetsTable<S extends SubTable>
    extends SubTable implements Iterable<S>, HtmlDump {
  public final boolean dataIsCanonical;
  public final TagOffsetRecordList recordList;
  public final int base;
  
  /////////////////
  // constructors
  
  public TagOffsetsTable(ReadableFontData data, int base, boolean dataIsCanonical) {
    super(data);
    this.base = base;
    this.dataIsCanonical = dataIsCanonical;
    recordList = new TagOffsetRecordList(data.slice(base));
  }

  public TagOffsetsTable(ReadableFontData data, boolean dataIsCanonical) {
    this(data, 0, dataIsCanonical);
  }
  
  //////////////////
  // public methods

  public int tagAt(int index) {
    return recordList.get(index).tag;
  }
  
  public S subTableAt(int index) {
    TagOffsetRecord record = recordList.get(index);
    return subTableForRecord(record);
  }

  public S subTableForTag(int tag) {
    TagOffsetRecord record = recordList.getRecordForTag(tag);
    if (record == null) {
      return null;
    }
    return subTableForRecord(record);
  }
  
  @Override
  public Iterator<S> iterator() {
    return new Iterator<S>() {
      Iterator<TagOffsetRecord> recordIterator = recordList.iterator();

      @Override
      public boolean hasNext() {
        return recordIterator.hasNext();
      }

      @Override
      public S next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        TagOffsetRecord record = recordIterator.next();
        return subTableForRecord(record);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public String toHtml() {
    Class<? extends TagOffsetsTable> clzz = this.getClass();
    StringBuilder sb = new StringBuilder(clzz.getSimpleName() + '\n');
    sb.append("<div>\n");
    sb.append(recordList.toHtml());
    sb.append("</div>\n");
    return sb.toString();
  }

  //////////////////////////////////////
  // implementations pushed to subclasses

  abstract protected S readSubTable(ReadableFontData data,
      boolean dataIsCanonical);

  //////////////////////////////////////
  // private methods

  private S subTableForRecord(TagOffsetRecord record) {
    ReadableFontData newBase = data.slice(record.offset);
    return readSubTable(newBase, dataIsCanonical);
  }
  
  public abstract static 
  class Builder<T extends SubTable, S extends SubTable> extends VisibleBuilder<T> {
    
    public TreeMap<Integer, VisibleBuilder<S>> builders;
    protected boolean dataIsCanonical;
    protected int serializedLength;
    private int serializedCount;
    private final int base;    

    /////////////////
    // constructors
    
    public Builder() {
      super();
      base = 0;
    }

    public Builder(TagOffsetsTable<T> table) {
      this(table.readFontData(), table.base, table.dataIsCanonical);
    }

    public Builder(ReadableFontData data, int base, boolean dataIsCanonical) {
      super(data);
      this.base = base;
      this.dataIsCanonical = dataIsCanonical;
      if (!dataIsCanonical) {
        prepareToEdit();
      }
    }

    //////////////////
    // public methods

    public int subTableCount() {
      if (builders == null) {
        return new TagOffsetRecordList(internalReadData().slice(base)).count();
      }
      return builders.size();
    }

    public
    SubTable.Builder<? extends SubTable> builderForTag(int tag) {
      prepareToEdit();
      return builders.get(tag);
    }

    public 
    VisibleBuilder<S> addBuilderForTag(int tag) {
      prepareToEdit();
      VisibleBuilder<S> builder = builders.get(tag);
      if (builder == null) {
        builder = createSubTableBuilder();
        builders.put(tag, builder);
      }
      return builder;
    }

    public void removeBuilderForTag(int tag) {
      prepareToEdit();
      builders.remove(tag);
    }

    //////////////////////////////////////
    // overriden methods

    @Override
    public int subDataSizeToSerialize() {
      if (builders != null) {
        computeSizeFromBuilders();
      } else {
        computeSizeFromData(internalReadData().slice(base));
      }
      return serializedLength;
    }

    @Override
    protected boolean subReadyToSerialize() {
      return true;
    }


    @Override
    public int subSerialize(WritableFontData newData) {
      if (serializedLength == 0) {
        return 0;
      }
      
      if (builders != null) {
        return serializeFromBuilders(newData);
      }
      return serializeFromData(newData);
    }

    @Override
    public void subDataSet() {
      builders = null;
    }

    @Override
    public T subBuildTable(ReadableFontData data) {
      return readTable(data, 0, true);
    }

    //////////////////////////////////////
    // implementations pushed to subclasses

    protected abstract T readTable(ReadableFontData data, int base,
        boolean dataIsCanonical);

    protected abstract
    VisibleBuilder<S> createSubTableBuilder();    
    
    protected abstract
    VisibleBuilder<S> createSubTableBuilder(
        ReadableFontData data, int tag, boolean dataIsCanonical);    
    
    //////////////////////////////////////
    // private methods

    private void prepareToEdit() {
      if (builders == null) {
        initFromData(internalReadData(), base);
        setModelChanged();
      }
    }

    private void initFromData(ReadableFontData data, int base) {
      builders = new TreeMap<Integer, VisibleBuilder<S>>();
      if (data == null) {
        return;
      }
      
      data = data.slice(base);
      // Start of the first subtable in the data, if we're canonical.
      TagOffsetRecordList recordList = new TagOffsetRecordList(data);
      if (recordList.count() == 0) {
        return;
      }

      int subTableLimit = recordList.limit();
      Iterator<TagOffsetRecord> recordIterator = recordList.iterator();
      if (dataIsCanonical) {
        do {
          // Each table starts where the previous one ended.
          int offset = subTableLimit;
          TagOffsetRecord record = recordIterator.next();
          int tag = record.tag;
          // Each table ends at the next start, or at the end of the data.
          subTableLimit = record.offset;
          // TODO(cibu): length computation does not seems to be correct.
          int length = subTableLimit - offset;
          VisibleBuilder<S> builder =
              createSubTableBuilder(data, offset, length, tag);
          builders.put(tag, builder);
        } while (recordIterator.hasNext());
      } else {
        do {
          TagOffsetRecord record = recordIterator.next();
          int offset = record.offset;
          int tag = record.tag;
          VisibleBuilder<S> builder =
              createSubTableBuilder(data, offset, -1, tag);
          builders.put(tag, builder);
        } while (recordIterator.hasNext());
      }
    }

    private void computeSizeFromBuilders() {
      // This does not merge LangSysTables that reference the same
      // features.

      // If there is no data in the default LangSysTable or any
      // of the other LangSysTables, the size is zero, and this table
      // will not be written.

      int len = 0;
      int count = 0;
      for (VisibleBuilder<? extends SubTable> builder : builders.values()) {
        int sublen = builder.subDataSizeToSerialize();
        if (sublen > 0) {
          ++count;
          len += sublen;
        }
      }
      if (len > 0) {
        len += TagOffsetRecordList.sizeOfListOfCount(count);
      }
      serializedLength = len;
      serializedCount = count;
    }

    private void computeSizeFromData(ReadableFontData data) {
      // This assumes canonical data.
      int len = 0;
      int count = 0;
      if (data != null) {
        len = data.length();
        count = new TagOffsetRecordList(data).count();
      }
      serializedLength = len;
      serializedCount = count;
    }

    private int serializeFromBuilders(WritableFontData newData) {
      // The canonical form of the data consists of the header,
      // the index, then the
      // scriptTables from the index in index order.  All
      // scriptTables are distinct; there's no sharing of tables.

      // Find size for table
      int tableSize = TagOffsetRecordList.sizeOfListOfCount(serializedCount);
      
      // Fill header in table and serialize its builder.
      int subTableFillPos = tableSize;
      
      TagOffsetRecordList recordList = new TagOffsetRecordList(newData);
      for (Entry<Integer, VisibleBuilder<S>> entry : builders.entrySet()) {
        int tag = entry.getKey();
        VisibleBuilder<? extends SubTable> builder = entry.getValue();
        if (builder.serializedLength > 0) {
          TagOffsetRecord record = new TagOffsetRecord(tag, subTableFillPos);
          recordList.add(record);
          subTableFillPos += builder.subSerialize(newData.slice(subTableFillPos));
        }
      }
      recordList.writeTo(newData);
      return subTableFillPos;
    }

    private int serializeFromData(WritableFontData newData) {
      // The source data must be canonical.
      ReadableFontData data = internalReadData().slice(base);
      data.copyTo(newData);
      return data.length();
    }

    private VisibleBuilder<S> createSubTableBuilder(
        ReadableFontData data, int offset, int length, int tag) {
      boolean dataIsCanonical = (length >= 0);
      ReadableFontData newData = dataIsCanonical ?
          data.slice(offset, length) : data.slice(offset);
      return createSubTableBuilder(newData, tag, dataIsCanonical);
    }
  }
}