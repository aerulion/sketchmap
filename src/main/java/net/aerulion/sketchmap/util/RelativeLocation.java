package net.aerulion.sketchmap.util;

public class RelativeLocation {

  private final int x;
  private final int y;

  public RelativeLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static RelativeLocation fromString(final String str) {
    final String[] args = str.split(":");
    if (args.length != 2) {
      return null;
    }
    try {
      int x = Integer.parseInt(args[0]);
      int y = Integer.parseInt(args[1]);
      return new RelativeLocation(x, y);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  @Override
  public String toString() {
    return x + ":" + y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}