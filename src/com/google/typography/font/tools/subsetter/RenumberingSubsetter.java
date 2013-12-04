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
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.MaximumProfileTable;
import com.google.typography.font.sfntly.table.core.OS2Table;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Raph Levien
 */
public class RenumberingSubsetter extends Subsetter {

  {
    //There must be an order to the subsetting process.
    List<TableSubsetter> temp = new ArrayList<TableSubsetter>();//new HashSet<TableSubsetter>();
    temp.add(new GlyphTableSubsetter());
    temp.add(new RenumberingCMapTableSubsetter());
    temp.add(new PostScriptTableSubsetter());
    temp.add(new HorizontalMetricsTableSubsetter());
    temp.add(new NameTableSubsetter());
    temp.add(new OS2TableSubsetter());
    temp.add(new HeadTableSubsetter());
    tableSubsetters = temp;
  }

  public RenumberingSubsetter(Font font, FontFactory fontFactory) {
    super(font, fontFactory);
  }

  @Override
  protected void setUpTables(Font.Builder fontBuilder) {
    fontBuilder.newTableBuilder(Tag.hhea, font.getTable(Tag.hhea).readFontData());
    if (font.getTable(Tag.maxp) == null) {
      MaximumProfileTable.Builder maxp = (MaximumProfileTable.Builder) fontBuilder.newTableBuilder(Tag.maxp);
      maxp.setData(WritableFontData.createWritableFontData(32));
      maxp.setTableVersion(0x00010000);
      
    } else {
      fontBuilder.newTableBuilder(Tag.maxp, font.getTable(Tag.maxp).readFontData());
    }
    if (font.getTable(Tag.OS_2) != null) {
      fontBuilder.newTableBuilder(Tag.OS_2, font.getTable(Tag.OS_2).readFontData());
    }  
  }
}
