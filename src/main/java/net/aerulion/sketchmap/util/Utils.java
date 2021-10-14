package net.aerulion.sketchmap.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class Utils {

  private static final String VALID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz_/";

  public static boolean isInvalidNamespaceID(String namespaceID) {
      if (namespaceID.startsWith("/") || namespaceID.startsWith("_") || namespaceID.endsWith("/")
          || namespaceID.endsWith("_")) {
          return true;
      }
    for (int i = 0; i < namespaceID.length(); i++) {
        if (!VALID_CHARACTERS.contains(Character.toString(namespaceID.charAt(i)))) {
            return true;
        }
    }
    return false;
  }

  public static List<String> getNamespaceCategories() {
    ArrayList<String> namespaceCategories = new ArrayList<>();
    namespaceCategories.add("<NamespaceID>");
    for (String string : Main.LoadedSketchMaps.keySet()) {
      String[] splitString = string.split("/");
      if (splitString.length > 1) {
        StringBuilder category = new StringBuilder();
        for (int i = 0; i < splitString.length - 1; i++) {
          category.append(splitString[i]).append("/");
            if (!namespaceCategories.contains(category.toString())) {
                namespaceCategories.add(category.toString());
            }
        }
      }
    }
    return namespaceCategories;
  }

  public static void openSketchMapInventory(Player player, SketchMap sketchMap) {
    List<ItemStack> mapItems = ItemUtils.getOrderedItemSet(sketchMap);
    final Inventory inventory = Bukkit.createInventory(null,
        ((int) (Math.ceil(mapItems.size() / 9.0))) * 9, "§8SketchMap:");
      for (ItemStack itemStack : mapItems) {
          inventory.addItem(itemStack);
      }
    player.openInventory(inventory);
    SoundUtils.playSound(player, SoundType.OPEN_CONTAINER);
  }

  public static BufferedImage resize(Image image, int width, int height) {
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

  public static MapView getMapView(int id) {
    return Bukkit.getMap(id);
  }

  public static World getDefaultWorld() {
    return Bukkit.getWorlds().get(0);
  }

  public static String encodeMapping(Map<RelativeLocation, MapView> mapViews) {
    StringBuilder stringBuilder = new StringBuilder();
    for (RelativeLocation loc : mapViews.keySet()) {
      stringBuilder.append(loc.toString()).append("#").append(mapViews.get(loc).getId())
          .append(";");
    }
    return stringBuilder.toString();
  }

  public static Map<Integer, RelativeLocation> decodeMapping(String string) {
    Map<Integer, RelativeLocation> mapping = new HashMap<>();
    for (String s : string.split(";")) {
      if (!s.equals("")) {
        String[] split = s.split("#");
        RelativeLocation relativeLocation = RelativeLocation.fromString(split[0]);
        int id = Integer.parseInt(split[1]);
        mapping.put(id, relativeLocation);
      }
    }
    return mapping;
  }
}