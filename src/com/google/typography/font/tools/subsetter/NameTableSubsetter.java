package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Font.Builder;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntry;
import com.google.typography.font.sfntly.table.core.NameTable.NameEntryBuilder;
import com.google.typography.font.sfntly.table.core.NameTable.NameId;
import java.io.IOException;
import java.util.Arrays;

/**
 * Pass-through. Needed to ensure correct format of name table. Will remove
 * invalid entries.
 *
 * @author
 */
public class NameTableSubsetter extends TableSubsetterImpl {

  public NameTableSubsetter() {
    super(Tag.name);
  }

  @Override
  public boolean subset(Subsetter subsetter, Font font, Builder fontBuilder) throws IOException {
    NameTable nameTable = font.getTable(Tag.name);
    NameTable.Builder nameTableBuilder = (NameTable.Builder) fontBuilder.newTableBuilder(Tag.name);
   
    for (NameEntry name : nameTable) {
      //Skip unknown name entries.
      if (NameId.valueOf(name.nameId()) != NameId.Unknown) {
        byte[] data = name.nameAsBytes();
        if (data.length > 0 && name.nameId() >= 0) {
          int encodingId = name.encodingId();
          //Truncate entry to <= 600 bytes.
          data = Arrays.copyOf(data, Math.min(data.length, 600));
          //if (name.platformId() == 3)
          //  encodingId = 1;
          NameEntryBuilder entry = nameTableBuilder.nameBuilder(
                  name.platformId(), encodingId,
                  name.languageId(), name.nameId());
          entry.setName(data);
        }
      }
    }
    return true;
  }
}
