package com.google.typography.font.tools.subsetter;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.OS2Table;
import com.google.typography.font.sfntly.table.core.OS2Table.UnicodeRange;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Recalculates some information for the OS2Table. Note that not all info
 * is recalculated in this class. Some info is calculated in other subsetters,
 * as they require information from other tables to be calculated.
 */
public class OS2TableSubsetter extends TableSubsetterImpl {
  private static final Map<IntegerRange, UnicodeRange> unicodeRanges;
  
  static {
    @SuppressWarnings("unchecked")
    Map<IntegerRange, UnicodeRange> tUnicodeRanges = 
            new TreeMap<IntegerRange, UnicodeRange>(new IntegerRangeComparator());
    tUnicodeRanges.put(new IntegerRange(0x0000, 0x007F), UnicodeRange.BasicLatin);
    tUnicodeRanges.put(new IntegerRange(0x0080, 0x00FF), UnicodeRange.Latin1Supplement);
    tUnicodeRanges.put(new IntegerRange(0x0100, 0x017F), UnicodeRange.LatinExtendedA);
    tUnicodeRanges.put(new IntegerRange(0x0180, 0x024F), UnicodeRange.LatinExtendedB);
    tUnicodeRanges.put(new IntegerRange(0x0250, 0x02AF), UnicodeRange.IPAExtensions);
    tUnicodeRanges.put(new IntegerRange(0x1D00, 0x1D7F), UnicodeRange.IPAExtensions);
    tUnicodeRanges.put(new IntegerRange(0x1D80, 0x1DBF), UnicodeRange.IPAExtensions);
    tUnicodeRanges.put(new IntegerRange(0x02B0, 0x02FF), UnicodeRange.SpacingModifierLetters);
    tUnicodeRanges.put(new IntegerRange(0xA700, 0xA71F), UnicodeRange.SpacingModifierLetters);
    tUnicodeRanges.put(new IntegerRange(0x0300, 0x036F), UnicodeRange.CombiningDiacriticalMarks);
    tUnicodeRanges.put(new IntegerRange(0x1DC0, 0x1DFF), UnicodeRange.CombiningDiacriticalMarks);
    tUnicodeRanges.put(new IntegerRange(0x0370, 0x03FF), UnicodeRange.GreekAndCoptic);
    tUnicodeRanges.put(new IntegerRange(0x2C80, 0x2CFF), UnicodeRange.Coptic);
    tUnicodeRanges.put(new IntegerRange(0x0400, 0x04FF), UnicodeRange.Cyrillic);
    tUnicodeRanges.put(new IntegerRange(0x0500, 0x052F), UnicodeRange.Cyrillic);
    tUnicodeRanges.put(new IntegerRange(0x2DE0, 0x2DFF), UnicodeRange.Cyrillic);
    tUnicodeRanges.put(new IntegerRange(0xA640, 0xA69F), UnicodeRange.Cyrillic);
    tUnicodeRanges.put(new IntegerRange(0x0530, 0x058F), UnicodeRange.Armenian);
    tUnicodeRanges.put(new IntegerRange(0x0590, 0x05FF), UnicodeRange.Hebrew);
    tUnicodeRanges.put(new IntegerRange(0xA500, 0xA63F), UnicodeRange.Vai);
    tUnicodeRanges.put(new IntegerRange(0x0600, 0x06FF), UnicodeRange.Arabic);
    tUnicodeRanges.put(new IntegerRange(0x0750, 0x077F), UnicodeRange.Arabic);
    tUnicodeRanges.put(new IntegerRange(0x07C0, 0x07FF), UnicodeRange.NKo);
    tUnicodeRanges.put(new IntegerRange(0x0900, 0x097F), UnicodeRange.Devanagari);
    tUnicodeRanges.put(new IntegerRange(0x0980, 0x09FF), UnicodeRange.Bengali);
    tUnicodeRanges.put(new IntegerRange(0x0A00, 0x0A7F), UnicodeRange.Gurmukhi);
    tUnicodeRanges.put(new IntegerRange(0x0A80, 0x0AFF), UnicodeRange.Gujarati);
    tUnicodeRanges.put(new IntegerRange(0x0B00, 0x0B7F), UnicodeRange.Oriya);
    tUnicodeRanges.put(new IntegerRange(0x0B80, 0x0BFF), UnicodeRange.Tamil);
    tUnicodeRanges.put(new IntegerRange(0x0C00, 0x0C7F), UnicodeRange.Telugu);
    tUnicodeRanges.put(new IntegerRange(0x0C80, 0x0CFF), UnicodeRange.Kannada);
    tUnicodeRanges.put(new IntegerRange(0x0D00, 0x0D7F), UnicodeRange.Malayalam);
    tUnicodeRanges.put(new IntegerRange(0x0E00, 0x0E7F), UnicodeRange.Thai);
    tUnicodeRanges.put(new IntegerRange(0x0E80, 0x0EFF), UnicodeRange.Lao);
    tUnicodeRanges.put(new IntegerRange(0x10A0, 0x10FF), UnicodeRange.Georgian);
    tUnicodeRanges.put(new IntegerRange(0x2D00, 0x2D2F), UnicodeRange.Georgian);
    tUnicodeRanges.put(new IntegerRange(0x1B00, 0x1B7F), UnicodeRange.Balinese);
    tUnicodeRanges.put(new IntegerRange(0x1100, 0x11FF), UnicodeRange.HangulJamo);
    tUnicodeRanges.put(new IntegerRange(0x1E00, 0x1EFF), UnicodeRange.LatinExtendedAdditional);
    tUnicodeRanges.put(new IntegerRange(0x2C60, 0x2C7F), UnicodeRange.LatinExtendedAdditional);
    tUnicodeRanges.put(new IntegerRange(0xA720, 0xA7FF), UnicodeRange.LatinExtendedAdditional);
    tUnicodeRanges.put(new IntegerRange(0x1F00, 0x1FFF), UnicodeRange.GreekExtended);
    tUnicodeRanges.put(new IntegerRange(0x2000, 0x206F), UnicodeRange.GeneralPunctuation);
    tUnicodeRanges.put(new IntegerRange(0x2E00, 0x2E7F), UnicodeRange.GeneralPunctuation);
    tUnicodeRanges.put(new IntegerRange(0x2070, 0x209F), UnicodeRange.SuperscriptsAndSubscripts);
    tUnicodeRanges.put(new IntegerRange(0x20A0, 0x20CF), UnicodeRange.CurrencySymbols);
    tUnicodeRanges.put(new IntegerRange(0x20D0, 0x20FF), UnicodeRange.CombiningDiacriticalMarksForSymbols);
    tUnicodeRanges.put(new IntegerRange(0x2100, 0x214F), UnicodeRange.LetterlikeSymbols);
    tUnicodeRanges.put(new IntegerRange(0x2150, 0x218F), UnicodeRange.NumberForms);
    tUnicodeRanges.put(new IntegerRange(0x2190, 0x21FF), UnicodeRange.Arrows);
    tUnicodeRanges.put(new IntegerRange(0x27F0, 0x27FF), UnicodeRange.Arrows);
    tUnicodeRanges.put(new IntegerRange(0x2900, 0x297F), UnicodeRange.Arrows);
    tUnicodeRanges.put(new IntegerRange(0x2B00, 0x2BFF), UnicodeRange.Arrows);
    tUnicodeRanges.put(new IntegerRange(0x2200, 0x22FF), UnicodeRange.MathematicalOperators);
    tUnicodeRanges.put(new IntegerRange(0x2A00, 0x2AFF), UnicodeRange.MathematicalOperators);
    tUnicodeRanges.put(new IntegerRange(0x27C0, 0x27EF), UnicodeRange.MathematicalOperators);
    tUnicodeRanges.put(new IntegerRange(0x2980, 0x29FF), UnicodeRange.MathematicalOperators);
    tUnicodeRanges.put(new IntegerRange(0x2300, 0x23FF), UnicodeRange.MiscTechnical);
    tUnicodeRanges.put(new IntegerRange(0x2400, 0x243F), UnicodeRange.ControlPictures);
    tUnicodeRanges.put(new IntegerRange(0x2440, 0x245F), UnicodeRange.OCR);
    tUnicodeRanges.put(new IntegerRange(0x2460, 0x24FF), UnicodeRange.EnclosedAlphanumerics);
    tUnicodeRanges.put(new IntegerRange(0x2500, 0x257F), UnicodeRange.BoxDrawing);
    tUnicodeRanges.put(new IntegerRange(0x2580, 0x259F), UnicodeRange.BlockElements);
    tUnicodeRanges.put(new IntegerRange(0x25A0, 0x25FF), UnicodeRange.GeometricShapes);
    tUnicodeRanges.put(new IntegerRange(0x2600, 0x26FF), UnicodeRange.MiscSymbols);
    tUnicodeRanges.put(new IntegerRange(0x2700, 0x27BF), UnicodeRange.Dingbats);
    tUnicodeRanges.put(new IntegerRange(0x3000, 0x303F), UnicodeRange.CJKSymbolsAndPunctuation);
    tUnicodeRanges.put(new IntegerRange(0x3040, 0x309F), UnicodeRange.Hiragana);
    tUnicodeRanges.put(new IntegerRange(0x30A0, 0x30FF), UnicodeRange.Katakana);
    tUnicodeRanges.put(new IntegerRange(0x31F0, 0x31FF), UnicodeRange.Katakana);
    tUnicodeRanges.put(new IntegerRange(0x3100, 0x312F), UnicodeRange.Bopomofo);
    tUnicodeRanges.put(new IntegerRange(0x31A0, 0x31BF), UnicodeRange.Bopomofo);
    tUnicodeRanges.put(new IntegerRange(0x3130, 0x318F), UnicodeRange.HangulCompatibilityJamo);
    tUnicodeRanges.put(new IntegerRange(0xA840, 0xA87F), UnicodeRange.Phagspa);
    tUnicodeRanges.put(new IntegerRange(0x3200, 0x32FF), UnicodeRange.EnclosedCJKLettersAndMonths);
    tUnicodeRanges.put(new IntegerRange(0x3300, 0x33FF), UnicodeRange.CJKCompatibility);
    tUnicodeRanges.put(new IntegerRange(0xAC00, 0xD7AF), UnicodeRange.HangulSyllables);
    tUnicodeRanges.put(new IntegerRange(0xD800, 0xDFFF), UnicodeRange.NonPlane0);
    tUnicodeRanges.put(new IntegerRange(0x10900, 0x1091F), UnicodeRange.Phoenician);
    tUnicodeRanges.put(new IntegerRange(0x4E00, 0x9FFF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x2E80, 0x2EFF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x2F00, 0x2FDF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x2FF0, 0x2FFF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x3400, 0x4DBF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x20000, 0x2A6DF), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0x3190, 0x319F), UnicodeRange.CJKUnifiedIdeographs);
    tUnicodeRanges.put(new IntegerRange(0xE000, 0xF8FF), UnicodeRange.PrivateUseAreaPlane0);
    tUnicodeRanges.put(new IntegerRange(0x31C0, 0x31EF), UnicodeRange.CJKStrokes);
    tUnicodeRanges.put(new IntegerRange(0xF900, 0xFAFF), UnicodeRange.CJKStrokes);
    tUnicodeRanges.put(new IntegerRange(0x2F800, 0x2FA1F), UnicodeRange.CJKStrokes);
    tUnicodeRanges.put(new IntegerRange(0xFB00, 0xFB4F), UnicodeRange.AlphabeticPresentationForms);
    tUnicodeRanges.put(new IntegerRange(0xFB50, 0xFDFF), UnicodeRange.ArabicPresentationFormsA);
    tUnicodeRanges.put(new IntegerRange(0xFE20, 0xFE2F), UnicodeRange.CombiningHalfMarks);
    tUnicodeRanges.put(new IntegerRange(0xFE10, 0xFE1F), UnicodeRange.VerticalForms);
    tUnicodeRanges.put(new IntegerRange(0xFE30, 0xFE4F), UnicodeRange.VerticalForms);
    tUnicodeRanges.put(new IntegerRange(0xFE50, 0xFE6F), UnicodeRange.SmallFormVariants);
    tUnicodeRanges.put(new IntegerRange(0xFE70, 0xFEFF), UnicodeRange.ArabicPresentationFormsB);
    tUnicodeRanges.put(new IntegerRange(0xFF00, 0xFFEF), UnicodeRange.HalfwidthAndFullwidthForms);
    tUnicodeRanges.put(new IntegerRange(0xFFF0, 0xFFFF), UnicodeRange.Specials);
    tUnicodeRanges.put(new IntegerRange(0x0F00, 0x0FFF), UnicodeRange.Tibetan);
    tUnicodeRanges.put(new IntegerRange(0x0700, 0x074F), UnicodeRange.Syriac);
    tUnicodeRanges.put(new IntegerRange(0x0780, 0x07BF), UnicodeRange.Thaana);
    tUnicodeRanges.put(new IntegerRange(0x0D80, 0x0DFF), UnicodeRange.Sinhala);
    tUnicodeRanges.put(new IntegerRange(0x1000, 0x109F), UnicodeRange.Myanmar);
    tUnicodeRanges.put(new IntegerRange(0x1200, 0x137F), UnicodeRange.Ethiopic);
    tUnicodeRanges.put(new IntegerRange(0x1380, 0x139F), UnicodeRange.Ethiopic);
    tUnicodeRanges.put(new IntegerRange(0x2D80, 0x2DDF), UnicodeRange.Ethiopic);
    tUnicodeRanges.put(new IntegerRange(0x13A0, 0x13FF), UnicodeRange.Cherokee);
    tUnicodeRanges.put(new IntegerRange(0x1400, 0x167F), UnicodeRange.UnifiedCanadianAboriginalSyllabics);
    tUnicodeRanges.put(new IntegerRange(0x1680, 0x169F), UnicodeRange.Ogham);
    tUnicodeRanges.put(new IntegerRange(0x16A0, 0x16FF), UnicodeRange.Runic);
    tUnicodeRanges.put(new IntegerRange(0x1780, 0x17FF), UnicodeRange.Khmer);
    tUnicodeRanges.put(new IntegerRange(0x19E0, 0x19FF), UnicodeRange.Khmer);
    tUnicodeRanges.put(new IntegerRange(0x1800, 0x18AF), UnicodeRange.Mongolian);
    tUnicodeRanges.put(new IntegerRange(0x2800, 0x28FF), UnicodeRange.BraillePatterns);
    tUnicodeRanges.put(new IntegerRange(0xA000, 0xA48F), UnicodeRange.YiSyllables);
    tUnicodeRanges.put(new IntegerRange(0xA490, 0xA4CF), UnicodeRange.YiSyllables);
    tUnicodeRanges.put(new IntegerRange(0x1700, 0x171F), UnicodeRange.Tagalog);
    tUnicodeRanges.put(new IntegerRange(0x1720, 0x173F), UnicodeRange.Tagalog);
    tUnicodeRanges.put(new IntegerRange(0x1740, 0x175F), UnicodeRange.Tagalog);
    tUnicodeRanges.put(new IntegerRange(0x1760, 0x177F), UnicodeRange.Tagalog);
    tUnicodeRanges.put(new IntegerRange(0x10300, 0x1032F), UnicodeRange.OldItalic);
    tUnicodeRanges.put(new IntegerRange(0x10330, 0x1034F), UnicodeRange.Gothic);
    tUnicodeRanges.put(new IntegerRange(0x10400, 0x1044F), UnicodeRange.Deseret);
    tUnicodeRanges.put(new IntegerRange(0x1D000, 0x1D0FF), UnicodeRange.MusicalSymbols);
    tUnicodeRanges.put(new IntegerRange(0x1D100, 0x1D1FF), UnicodeRange.MusicalSymbols);
    tUnicodeRanges.put(new IntegerRange(0x1D200, 0x1D24F), UnicodeRange.MusicalSymbols);
    tUnicodeRanges.put(new IntegerRange(0x1D400, 0x1D7FF), UnicodeRange.MathematicalAlphanumericSymbols);
    tUnicodeRanges.put(new IntegerRange(0xFF000, 0xFFFFD), UnicodeRange.PrivateUsePlane15And16);
    tUnicodeRanges.put(new IntegerRange(0x100000, 0x10FFFD), UnicodeRange.PrivateUsePlane15And16);
    tUnicodeRanges.put(new IntegerRange(0xFE00, 0xFE0F), UnicodeRange.VariationSelectors);
    tUnicodeRanges.put(new IntegerRange(0xE0100, 0xE01EF), UnicodeRange.VariationSelectors);
    tUnicodeRanges.put(new IntegerRange(0xE0000, 0xE007F), UnicodeRange.Tags);
    tUnicodeRanges.put(new IntegerRange(0x1900, 0x194F), UnicodeRange.Limbu);
    tUnicodeRanges.put(new IntegerRange(0x1950, 0x197F), UnicodeRange.TaiLe);
    tUnicodeRanges.put(new IntegerRange(0x1980, 0x19DF), UnicodeRange.NewTaiLue);
    tUnicodeRanges.put(new IntegerRange(0x1A00, 0x1A1F), UnicodeRange.Buginese);
    tUnicodeRanges.put(new IntegerRange(0x2C00, 0x2C5F), UnicodeRange.Glagolitic);
    tUnicodeRanges.put(new IntegerRange(0x2D30, 0x2D7F), UnicodeRange.Tifnagh);
    tUnicodeRanges.put(new IntegerRange(0x4DC0, 0x4DFF), UnicodeRange.YijingHexagramSymbols);
    tUnicodeRanges.put(new IntegerRange(0xA800, 0xA82F), UnicodeRange.SylotiNagari);
    tUnicodeRanges.put(new IntegerRange(0x10000, 0x1007F), UnicodeRange.LinearB);
    tUnicodeRanges.put(new IntegerRange(0x10080, 0x100FF), UnicodeRange.LinearB);
    tUnicodeRanges.put(new IntegerRange(0x10100, 0x1013F), UnicodeRange.LinearB);
    tUnicodeRanges.put(new IntegerRange(0x10140, 0x1018F), UnicodeRange.AncientGreekNumbers);
    tUnicodeRanges.put(new IntegerRange(0x10380, 0x1039F), UnicodeRange.Ugaritic);
    tUnicodeRanges.put(new IntegerRange(0x103A0, 0x103DF), UnicodeRange.OldPersian);
    tUnicodeRanges.put(new IntegerRange(0x10450, 0x1047F), UnicodeRange.Shavian);
    tUnicodeRanges.put(new IntegerRange(0x10480, 0x104AF), UnicodeRange.Osmanya);
    tUnicodeRanges.put(new IntegerRange(0x10800, 0x1083F), UnicodeRange.CypriotSyllabary);
    tUnicodeRanges.put(new IntegerRange(0x10A00, 0x10A5F), UnicodeRange.Kharoshthi);
    tUnicodeRanges.put(new IntegerRange(0x1D300, 0x1D35F), UnicodeRange.TaiXuanJingSymbols);
    tUnicodeRanges.put(new IntegerRange(0x12000, 0x123FF), UnicodeRange.Cuneiform);
    tUnicodeRanges.put(new IntegerRange(0x12400, 0x1247F), UnicodeRange.Cuneiform);
    tUnicodeRanges.put(new IntegerRange(0x1D360, 0x1D37F), UnicodeRange.CountingRodNumerals);
    tUnicodeRanges.put(new IntegerRange(0x1B80, 0x1BBF), UnicodeRange.Sudanese);
    tUnicodeRanges.put(new IntegerRange(0x1C00, 0x1C4F), UnicodeRange.Lepcha);
    tUnicodeRanges.put(new IntegerRange(0x1C50, 0x1C7F), UnicodeRange.OlChiki);
    tUnicodeRanges.put(new IntegerRange(0xA880, 0xA8DF), UnicodeRange.Saurashtra);
    tUnicodeRanges.put(new IntegerRange(0xA900, 0xA92F), UnicodeRange.KayahLi);
    tUnicodeRanges.put(new IntegerRange(0xA930, 0xA95F), UnicodeRange.Rejang);
    tUnicodeRanges.put(new IntegerRange(0xAA00, 0xAA5F), UnicodeRange.Charm);
    tUnicodeRanges.put(new IntegerRange(0x10190, 0x101CF), UnicodeRange.AncientSymbols);
    tUnicodeRanges.put(new IntegerRange(0x101D0, 0x101FF), UnicodeRange.PhaistosDisc);
    tUnicodeRanges.put(new IntegerRange(0x102A0, 0x102DF), UnicodeRange.Carian);
    tUnicodeRanges.put(new IntegerRange(0x10280, 0x1029F), UnicodeRange.Carian);
    tUnicodeRanges.put(new IntegerRange(0x10920, 0x1093F), UnicodeRange.Carian);
    tUnicodeRanges.put(new IntegerRange(0x1F030, 0x1F09F), UnicodeRange.DominoTiles);
    tUnicodeRanges.put(new IntegerRange(0x1F000, 0x1F02F), UnicodeRange.DominoTiles);
    
    unicodeRanges = tUnicodeRanges;
  }
  
  public OS2TableSubsetter() {
    super(Tag.OS_2);
  }
  
  /**
   * Determines the OS/2 UniCode range that contains the specified
   * UniCode character point.
   * @param unicode The unicode character point to lookup
   * @return The UnicodeRange that contains the character point, or null if no
   *         such UnicodeRange exists.
   */
  public static UnicodeRange unicodeToRange(int unicode) {
    return unicodeRanges.get(unicode);
  }
  
  @Override
  public boolean subset(Subsetter subsetter, Font font, Font.Builder fontBuilder) throws IOException {
    OS2Table.Builder os2 = (OS2Table.Builder)fontBuilder.getTableBuilder(Tag.OS_2);
    if (os2 != null) {
      int fsType = os2.fsTypeAsInt();
      fsType &= ~0x0001; //Bit 0 is reserved, and must be zero;
      os2.setFsType(fsType);
    }
    return true;
  }
  
  
  private static class IntegerRange implements Comparable {
    private int min, max;
    
    public IntegerRange(int min, int max) {
      this.min = min;
      this.max = max;
    }

    @Override
    public int compareTo(Object o) {
      if (o instanceof Integer) {
        int value = (Integer) o;
        
        if (value < min) return -1;
        else if (value > max) return 1;
        else return 0;
      } else if (o instanceof IntegerRange) {
        IntegerRange other = (IntegerRange) o;
        if (other.min < min) return -1;
        else if (other.max > max) return 1;
        else return 0;
      }
      
      throw new ClassCastException("Cannot be compared with this type");
    }
    
    @Override
    public boolean equals(Object o) {
      if (o instanceof Integer) {
        int value = (Integer) o;
        return value >= min && value <= max;
      } else if (o instanceof IntegerRange) {
        IntegerRange other = (IntegerRange) o;
        return min == other.min && max == other.max;
      }
      return false;
    }
  }
  
  private static class IntegerRangeComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
      if (o2 instanceof IntegerRange) {
        IntegerRange range = (IntegerRange) o2;
      
        if (o1 instanceof Integer) {
          int value = (Integer) o1;

          if (value < range.min) return -1;
          else if (value > range.max) return 1;
          else return 0;
        } else if (o1 instanceof IntegerRange) {
          IntegerRange other = (IntegerRange) o1;
          if (other.min < range.min) return -1;
          else if (other.max > range.max) return 1;
          else return 0;
        }
      }
      
      throw new ClassCastException("Cannot be compared with this type");
    }
    
  }
}
