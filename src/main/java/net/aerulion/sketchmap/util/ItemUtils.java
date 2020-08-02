package net.aerulion.sketchmap.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {

    public static List<ItemStack> getOrderedItemSet(final SketchMap map) {
        final List<ItemStack> items = new ArrayList<ItemStack>();
        for (int y = 0; y < map.getYPanes(); ++y) {
            for (int x = 0; x < map.getXPanes(); ++x) {
                for (final RelativeLocation loc : map.getMapViews().keySet()) {
                    if (loc.getX() == x) {
                        if (loc.getY() != y) {
                            continue;
                        }
                        final ItemStack iStack = new ItemStack(Material.FILLED_MAP, 1);
                        final MapMeta iMeta = (MapMeta) iStack.getItemMeta();
                        iMeta.setMapView(map.getMapViews().get(loc));
                        iMeta.setLore(Arrays.asList("§e§m                           ", "§7SketchMap ID: §a" + map.getID(), "§7Pos-X: §a" + (x + 1), "§7Pos-Y: §a" + (y + 1), "§e§m                           "));
                        iStack.setItemMeta(iMeta);
                        items.add(iStack);
                    }
                }
            }
        }
        return items;
    }
}
