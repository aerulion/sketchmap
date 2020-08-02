package net.aerulion.sketchmap.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class SketchMapUtils {

    public static BufferedImage resize(Image image, int width, int height) {
        image = image.getScaledInstance(width, height, 4);
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 2);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public static MapView getMapView(int id) {
        final MapView map = Bukkit.getMap(id);
        if (map != null) {
            return map;
        }
        return Bukkit.createMap(getDefaultWorld());
    }

    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static ArrayList<String> filterForTabComplete(ArrayList<String> input, String filter) {
        if (filter != null) {
            for (Iterator<String> iterator = input.iterator(); iterator.hasNext(); ) {
                String value = iterator.next();
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
