package net.aerulion.sketchmap.util;

import net.aerulion.nucleus.api.nbt.NbtUtils;
import net.aerulion.nucleus.api.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {

    public static List<ItemStack> getOrderedItemSet(final SketchMap sketchMap) {
        final List<ItemStack> items = new ArrayList<>();
        for (int y = 0; y < sketchMap.getYPanes(); ++y) {
            for (int x = 0; x < sketchMap.getXPanes(); ++x) {
                for (RelativeLocation relativeLocation : sketchMap.getMapViews().keySet()) {
                    if (relativeLocation.getX() == x) {
                        if (relativeLocation.getY() != y)
                            continue;
                        ItemStack itemStack = new ItemStack(Material.FILLED_MAP, 1);
                        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                        mapMeta.setMapView(sketchMap.getMapViews().get(relativeLocation));
                        mapMeta.setLore(Arrays.asList("§7" + StringUtils.generateLine(11), "§7Pos-X: §a" + (x + 1), "§7Pos-Y: §a" + (y + 1), "§7" + StringUtils.generateLine(11), "§8sketchmap:" + sketchMap.getNamespaceID()));
                        itemStack.setItemMeta(mapMeta);
                        items.add(NbtUtils.setNBTInt(NbtUtils.setNBTInt(NbtUtils.setNBTString(itemStack, "SketchMapUUID", sketchMap.getUUID()), "SketchMapX", x), "SketchMapY", y));
                    }
                }
            }
        }
        return items;
    }
}