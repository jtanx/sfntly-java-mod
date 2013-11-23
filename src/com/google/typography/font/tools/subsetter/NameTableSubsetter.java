package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Font.Builder;
import com.google.typography.font.sfntly.Font.PlatformId;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntry;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntryBuilder;
import com.google.typography.font.sfntly.table.core.NameTable.NameId;
import java.io.IOException;

/**
 *
 * @author 
 */
public class NameTableSubsetter extends TableSubsetterImpl{
  public NameTableSubsetter() {
    super(Tag.name);
  }

  public boolean subset(Subsetter subsetter, Font font, Builder fontBuilder) throws IOException {
    NameTable nameTable = font.getTable(Tag.name);
    NameTable.Builder nameTableBuilder = (NameTable.Builder)fontBuilder.newTableBuilder(Tag.name);
    for (NameEntry name : nameTable) {
      /*if (name.nameId() != NameId.PreferredFamily.value() && 
              name.nameId() != NameId.PreferredSubfamily.value()) {*/
      if (name.platformId() == PlatformId.Macintosh.value()){
        NameEntryBuilder nameEntryBuilder = 
                nameTableBuilder.nameBuilder(name.platformId(), 
                name.encodingId(), name.languageId(), name.nameId());
        nameEntryBuilder.setName(name.nameAsBytes());
      }
    }
    return true;
  }
}
