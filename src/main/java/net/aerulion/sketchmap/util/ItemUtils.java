package net.aerulion.sketchmap.util;

import java.util.ArrayList;
import java.util.List;
import net.aerulion.sketchmap.SketchMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class for item related methods.
 */
public final class ItemUtils {

  @Contract(pure = true)
  private ItemUtils() {
    super();
    throw new UnsupportedOperationException("This utility class cannot be instantiated!");
  }

  /**
   * Gets the list of required item stacks for the given sketch map.
   *
   * @param sketchMap the sketchmap
   * @return the list of item stacks
   */
  public static @NotNull List<ItemStack> getOrderedItemSet(final @NotNull SketchMap sketchMap) {
    final List<ItemStack> itemStacks = new ArrayList<>();
    for (int y = 0; y < sketchMap.getYPanes(); ++y) {
      for (int x = 0; x < sketchMap.getXPanes(); ++x) {
        for (final RelativeLocation loc : sketchMap.getMapViews().keySet()) {
          if (loc.x() == x) {
            if (loc.y() != y) {
              continue;
            }
            final ItemStack itemStack = new ItemStack(Material.FILLED_MAP, 1);
            final MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
            mapMeta.setMapView(sketchMap.getMapViews().get(loc));
            mapMeta.lore(List.of(
                Component.text("                           ").color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.STRIKETHROUGH)
                    .decoration(TextDecoration.ITALIC, State.FALSE),
                Component.text("SketchMap ID: ").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, State.FALSE)
                    .append(Component.text(sketchMap.getID()).color(NamedTextColor.GREEN)),
                Component.text("Pos-X: ").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, State.FALSE)
                    .append(Component.text(x + 1).color(NamedTextColor.GREEN)),
                Component.text("Pos-Y: ").color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, State.FALSE)
                    .append(Component.text(y + 1).color(NamedTextColor.GREEN)),
                Component.text("                           ").color(NamedTextColor.YELLOW)
                    .decorate(TextDecoration.STRIKETHROUGH)
                    .decoration(TextDecoration.ITALIC, State.FALSE)));
            mapMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(mapMeta);
            itemStacks.add(itemStack);
          }
        }
      }
    }
    return itemStacks;
  }

}