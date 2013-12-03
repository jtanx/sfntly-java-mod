package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Font.Builder;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntry;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntryBuilder;
import java.io.IOException;

/**
 * Pass-through. Needed to ensure correct format of name table. Will remove
 * invalid entries.
 * @author 
 */
public class NameTableSubsetter extends TableSubsetterImpl{
  public NameTableSubsetter() {
    super(Tag.name);
  }

  @Override
  public boolean subset(Subsetter subsetter, Font font, Builder fontBuilder) throws IOException {
    NameTable nameTable = font.getTable(Tag.name);
    NameTable.Builder nameTableBuilder = (NameTable.Builder)fontBuilder.newTableBuilder(Tag.name);
    for (NameEntry name : nameTable) {
      if (name.nameAsBytes().length > 0 && name.nameId() >= 0) {
        NameEntryBuilder entry =  nameTableBuilder.nameBuilder(
                                      name.platformId(), name.encodingId(), 
                                      name.languageId(), name.nameId());
        entry.setName(name.nameAsBytes());
      }
    }
    return true;
  }
}
