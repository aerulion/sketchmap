package net.aerulion.sketchmap.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {

  private static final String VALID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz_/";

  public static boolean isInvalidNamespaceID(final @NotNull String namespaceID) {
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

  public static @NotNull List<String> getNamespaceCategories() {
    final @NotNull ArrayList<String> namespaceCategories = new ArrayList<>();
    namespaceCategories.add("<NamespaceID>");
    for (final @NotNull String string : Main.LOADED_SKETCH_MAPS.keySet()) {
      final String @NotNull [] splitString = string.split("/");
      if (splitString.length > 1) {
        final @NotNull StringBuilder category = new StringBuilder();
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

  public static void openSketchMapInventory(final @NotNull Player player,
      final @NotNull SketchMap sketchMap) {
    final @NotNull List<ItemStack> mapItems = ItemUtils.getOrderedItemSet(sketchMap);
    final @NotNull Inventory inventory = Bukkit.createInventory(null,
        ((int) (Math.ceil(mapItems.size() / 9.0))) * 9, "§8SketchMap:");
    for (final ItemStack itemStack : mapItems) {
      inventory.addItem(itemStack);
    }
    player.openInventory(inventory);
    SoundUtils.playSound(player, SoundType.OPEN_CONTAINER);
  }

  public static @NotNull BufferedImage resize(Image image, final int width, final int height) {
    image = image.getScaledInstance(width, height, 4);
    if (image instanceof BufferedImage) {
      return (BufferedImage) image;
    }
    final @NotNull BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
        image.getHeight(null), 2);
    final Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(image, 0, 0, null);
    graphics2D.dispose();
    return bufferedImage;
  }

  public static @Nullable MapView getMapView(final int id) {
    return Bukkit.getMap(id);
  }

  public static World getDefaultWorld() {
    return Bukkit.getWorlds().get(0);
  }

  public static @NotNull String encodeMapping(
      final @NotNull Map<RelativeLocation, MapView> mapViews) {
    final @NotNull StringBuilder stringBuilder = new StringBuilder();
    for (final Entry<RelativeLocation, MapView> entry : mapViews.entrySet()) {
      stringBuilder.append(entry.getKey()).append("#").append(entry.getValue().getId())
          .append(";");
    }
    return stringBuilder.toString();
  }

  public static @NotNull Map<Integer, RelativeLocation> decodeMapping(
      final @NotNull String string) {
    final @NotNull Map<Integer, RelativeLocation> mapping = new HashMap<>();
    for (final @NotNull String s : string.split(";")) {
      if (!s.isEmpty()) {
        final String @NotNull [] split = s.split("#");
        final @Nullable RelativeLocation relativeLocation = RelativeLocation.fromString(split[0]);
        final int id = Integer.parseInt(split[1]);
        mapping.put(id, relativeLocation);
      }
    }
    return mapping;
  }
}