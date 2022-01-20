package net.aerulion.sketchmap.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelativeLocation {

  private final int x;
  private final int y;

  public RelativeLocation(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public static @Nullable RelativeLocation fromString(final @NotNull String str) {
    final String @NotNull [] args = str.split(":");
    if (args.length != 2) {
      return null;
    }
    try {
      final int x = Integer.parseInt(args[0]);
      final int y = Integer.parseInt(args[1]);
      return new RelativeLocation(x, y);
    } catch (final NumberFormatException exception) {
      return null;
    }
  }

  @Override
  public @NotNull String toString() {
    return x + ":" + y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}