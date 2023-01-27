package net.aerulion.sketchmap.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the relative location of a single sketchmap tile.
 */
public record RelativeLocation(int x, int y) {

  /**
   * Deserializes a relative location from the given string.
   *
   * @param str the string to deserialize
   * @return the relative location or null
   */
  public static @Nullable RelativeLocation fromString(final @NotNull String str) {
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
  @Contract(pure = true)
  public @NotNull String toString() {
    return this.x + ":" + this.y;
  }

}