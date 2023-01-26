package net.aerulion.sketchmap.util;

public class RelativeLocation {

  private final int x;
  private final int y;

  public RelativeLocation(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public static RelativeLocation fromString(final String str) {
    final String[] args = str.split(":");
    if (args.length != 2) {
      return null;
    }
    final int x;
    final int y;
    try {
      x = Integer.parseInt(args[0]);
      y = Integer.parseInt(args[1]);
    } catch (final Exception ex) {
      return null;
    }
    return new RelativeLocation(x, y);
  }

  @Override
  public String toString() {
    return this.x + ":" + this.y;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }
}