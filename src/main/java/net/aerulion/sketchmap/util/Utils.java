package net.aerulion.sketchmap.util;

import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    private final static String VALID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz_/";

    public static boolean isInvalidNamespaceID(String namespaceID) {
        for (int i = 0; i < namespaceID.length(); i++) {
            if (!VALID_CHARACTERS.contains(Character.toString(namespaceID.charAt(i))))
                return true;
        }
        return false;
    }

    public static void openSketchMapInventory(Player player, SketchMap sketchMap) {
        List<ItemStack> mapItems = ItemUtils.getOrderedItemSet(sketchMap);
        final Inventory inventory = Bukkit.createInventory(null, ((int) (Math.ceil(mapItems.size() / 9.0))) * 9, "§8SketchMap:");
        for (ItemStack itemStack : mapItems)
            inventory.addItem(itemStack);
        player.openInventory(inventory);
        SoundUtils.playSound(player, SoundType.OPEN_CONTAINER);
    }

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
        return Bukkit.getMap(id);
    }

    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static String encodeMapping(Map<RelativeLocation, MapView> mapViews) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RelativeLocation loc : mapViews.keySet()) {
            stringBuilder.append(loc.toString()).append("#").append(mapViews.get(loc).getId()).append(";");
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