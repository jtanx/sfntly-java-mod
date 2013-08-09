package com.google.typography.font.sfntly.table.opentype.component;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GlyphGroup extends BitSet implements Iterable<Integer> {
  private static final long serialVersionUID = 1L;

  private boolean inverse = false;

  public GlyphGroup() {
    super();
  }

  public GlyphGroup(int glyph) {
    super.set(glyph);
  }

  public GlyphGroup(Collection<Integer> glyphs) {
    for (int glyph : glyphs) {
      super.set(glyph);
    }
  }

  public static GlyphGroup inverseGlyphGroup(Collection<GlyphGroup> glyphGroups) {
    GlyphGroup result = new GlyphGroup();
    for(GlyphGroup glyphGroup : glyphGroups) {
      result.or(glyphGroup);
    }
    result.inverse = true;
    return result;
  }

  public GlyphGroup(int[] glyphs) {
    for (int glyph : glyphs) {
      super.set(glyph);
    }
  }

  public void add(int glyph) {
    this.set(glyph);
  }

  public void addAll(Collection<Integer> glyphs) {
    for (int glyph : glyphs) {
      super.set(glyph);
    }
  }

  public void addAll(GlyphGroup other) {
    this.or(other);
  }

  public void copyTo(Collection<Integer> target) {
    List<Integer> list = new LinkedList<>();
    for ( int i = this.nextSetBit( 0 ); i >= 0; i = this.nextSetBit( i + 1 ) ) {
      target.add(i);
    }
  }

  GlyphGroup intersection(GlyphGroup other) {
    GlyphGroup intersection = new GlyphGroup();
    if (this.inverse && !other.inverse) {
      intersection.or(other);
      intersection.andNot(this);
    } else if (other.inverse && !this.inverse) {
      intersection.or(this);
      intersection.andNot(other);
    } else if (other.inverse && this.inverse) {
      intersection.inverse = true;
      intersection.or(this);
      intersection.or(other);
    } else {
      intersection.or(this);
      intersection.and(other);
    }
    return intersection;
  }

  public boolean contains(int glyph) {
    return get(glyph) ^ inverse;
  }

  public boolean intersects(GlyphGroup other) {
    return !intersection(other).isEmpty();
  }

  public boolean isInverse() {
    return inverse;
  }

  @Override
  public int size() {
    return cardinality();
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      int i = 0;
      @Override
      public boolean hasNext() {
        return nextSetBit(i) >= 0 ;
      }

      @Override
      public Integer next() {
        i = nextSetBit(i);
        return i++;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.inverse) {
      sb.append("not-");
    }
    int glyphCount = this.size();
    if (glyphCount > 1) {
      sb.append("[ ");
    }
    for (int glyphId : this) {
      sb.append(glyphId);
      sb.append(" ");
    }
    if (glyphCount > 1) {
      sb.append("] ");
    }
    return sb.toString();
  }
}
