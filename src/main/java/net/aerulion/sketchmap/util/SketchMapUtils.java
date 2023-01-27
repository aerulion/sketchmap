package net.aerulion.sketchmap.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class for sketchmap related methods.
 */
public final class SketchMapUtils {

  @Contract(pure = true)
  private SketchMapUtils() {
    super();
    throw new UnsupportedOperationException("This utility class cannot be instantiated!");
  }

  /**
   * Resizes the given image to the desired size.
   *
   * @param image  the image to resize
   * @param width  the desired width
   * @param height the desired height
   * @return the resized image
   */
  public static @NotNull BufferedImage resize(final @NotNull Image image, final int width,
      final int height) {
    final Image scaledImage = image.getScaledInstance(width, height, 4);
    if (scaledImage instanceof BufferedImage) {
      return (BufferedImage) scaledImage;
    }
    final BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null),
        scaledImage.getHeight(null), 2);
    final Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(scaledImage, 0, 0, null);
    graphics2D.dispose();
    return bufferedImage;
  }

  /**
   * Gets the mapview associated with the given map id.
   *
   * @param id the map id
   * @return the mapview
   */
  @SuppressWarnings("deprecation")
  public static @NotNull MapView getMapView(final int id) {
    final MapView map = Bukkit.getMap(id);
    if (map != null) {
      return map;
    }
    return Bukkit.createMap(getDefaultWorld());
  }

  /**
   * Gets the default world.
   *
   * @return the default world
   */
  public static @NotNull World getDefaultWorld() {
    return Bukkit.getWorlds().get(0);
  }

  /**
   * Filters a list by a given filter
   *
   * @param commandList the list to filter
   * @param filter      the string used as a filter
   * @return the filtered list
   */
  @Contract("_, _ -> param1")
  public static @NotNull List<String> filterForTabComplete(final @NotNull List<String> commandList,
      final @NotNull String filter) {
    commandList.removeIf(value -> !value.toLowerCase().startsWith(filter.toLowerCase()));
    return commandList;
  }

}
