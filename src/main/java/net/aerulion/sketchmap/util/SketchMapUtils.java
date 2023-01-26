package net.aerulion.sketchmap.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public class SketchMapUtils {

  public static BufferedImage resize(Image image, final int width, final int height) {
    image = image.getScaledInstance(width, height, 4);
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }
    final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
        image.getHeight(null), 2);
    final Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(image, 0, 0, null);
    graphics2D.dispose();
    return bufferedImage;
  }

  public static MapView getMapView(final int id) {
    final MapView map = Bukkit.getMap(id);
    if (map != null) {
      return map;
    }
    return Bukkit.createMap(getDefaultWorld());
  }

  public static World getDefaultWorld() {
    return Bukkit.getWorlds().get(0);
  }

  public static ArrayList<String> filterForTabComplete(final ArrayList<String> input, final String filter) {
    if (filter != null) {
      for (final Iterator<String> iterator = input.iterator(); iterator.hasNext(); ) {
        final String value = iterator.next();
        if (!value.toLowerCase().startsWith(filter.toLowerCase())) {
          {
            iterator.remove();
          }
        }
      }
    }
    return input;
  }
}
