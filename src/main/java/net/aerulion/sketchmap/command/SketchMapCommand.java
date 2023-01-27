package net.aerulion.sketchmap.command;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import net.aerulion.sketchmap.SketchMap;
import net.aerulion.sketchmap.SketchMapPlugin;
import net.aerulion.sketchmap.util.BaseFormat;
import net.aerulion.sketchmap.util.ItemUtils;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMapUtils;
import net.aerulion.sketchmap.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the main command for managing sketchmaps.
 */
public class SketchMapCommand implements TabExecutor {

  private final @NotNull SketchMapPlugin sketchMapPlugin;

  /**
   * Instantiates a new sketchmap command.
   *
   * @param sketchMapPlugin the main plugin instance
   */
  @Contract(pure = true)
  public SketchMapCommand(final @NotNull SketchMapPlugin sketchMapPlugin) {
    super();
    this.sketchMapPlugin = sketchMapPlugin;
  }

  @Contract("null -> false")
  private static boolean isAlphanumeric(final @Nullable String str) {
    if (str == null) {
      return false;
    }
    final int sz = str.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isLetterOrDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command,
      final @NotNull String label, final String @NotNull [] args) {

    if (!sender.hasPermission("sketchmap.use")) {
      sender.sendMessage(Messages.ERROR_NO_PERMISSIONS);
      sender.playSound(Sounds.COMMAND_ERROR.sound());
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage(Messages.ERROR_MISSING_ARGS);
      sender.playSound(Sounds.COMMAND_ERROR.sound());
      return true;
    }

    if (args[0].equalsIgnoreCase("create")) {
      if (args.length < 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGS);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (args[1].length() < 3 || args[1].length() > 32) {
        sender.sendMessage(Messages.ERROR_ID_LENGTH);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (!isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
        sender.sendMessage(Messages.ERROR_ID_ALPHA_NUMERIC);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (sketchMapPlugin.getLoadedSketchMaps().containsKey(args[1].toLowerCase())) {
        sender.sendMessage(Messages.ERROR_ID_DUPLICATE);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      final @NotNull URL url;
      try {
        url = new URL(args[2]);
      } catch (final MalformedURLException ex) {
        sender.sendMessage(Messages.ERROR_MALFORMED_URL);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      @Nullable Integer xPanes = null;
      @Nullable Integer yPanes = null;

      if (args.length > 3) {
        final String @NotNull [] split = args[3].split(":");
        if (split.length != 2) {
          sender.sendMessage(Messages.ERROR_WRONG_SCALING_ARGS);
          sender.playSound(Sounds.COMMAND_ERROR.sound());
          return true;
        }
        try {
          xPanes = Integer.parseInt(split[0]);
          yPanes = Integer.parseInt(split[1]);
        } catch (final Exception ex2) {
          sender.sendMessage(Messages.ERROR_WRONG_SCALING_ARGS);
          sender.playSound(Sounds.COMMAND_ERROR.sound());
          return true;
        }
        if (xPanes < 1 || yPanes < 1) {
          sender.sendMessage(Messages.ERROR_NEGATIVE_SCALING_ARGS);
          sender.playSound(Sounds.COMMAND_ERROR.sound());
          return true;
        }
      }

      try {
        final long startDownload = System.currentTimeMillis();
        sender.sendMessage(Messages.DOWNLOADING_IMAGE);
        final BufferedImage image = ImageIO.read(url);
        sender.sendMessage(Messages.DOWNLOADED_IMAGE.asComponent().replaceText(
            builder -> builder.match("%time%")
                .replacement(String.valueOf(System.currentTimeMillis() - startDownload))));
        final long startProcessing = System.currentTimeMillis();
        sender.sendMessage(Messages.PROCESSING_IMAGE);
        final @NotNull String ext = url.getFile().substring(url.getFile().length() - 3);
        @Nullable BaseFormat format;
        try {
          format = BaseFormat.valueOf(ext.toUpperCase());
        } catch (final IllegalArgumentException e) {
          format = null;
        }
        if (format == null) {
          sender.sendMessage(Messages.ERROR_WRONG_FORMAT);
          sender.playSound(Sounds.COMMAND_ERROR.sound());
          return true;
        }
        if (args.length == 3) {
          int imageX = image.getWidth();
          int imageY = image.getHeight();
          while (imageX % 128 != 0) {
            ++imageX;
          }
          while (imageY % 128 != 0) {
            ++imageY;
          }
          xPanes = imageX / 128;
          yPanes = imageY / 128;
        }
        sketchMapPlugin.getFileManager()
            .createNewSketchMap(image, args[1].toLowerCase(), xPanes, yPanes);
        sender.sendMessage(Messages.PROCESSED_IMAGE.asComponent().replaceText(
            builder -> builder.match("%time%")
                .replacement(String.valueOf(System.currentTimeMillis() - startProcessing))));
        sender.sendMessage(Messages.SKETCHMAP_CREATED.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[1])));
        sender.playSound(Sounds.COMMAND_SUCCESS.sound());
        return true;
      } catch (final IOException e) {
        sender.sendMessage(Messages.ERROR_NO_IMAGE_URL);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }
    }

    if (args[0].equalsIgnoreCase("delete")) {

      if (args.length != 2) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGS);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (!sketchMapPlugin.getLoadedSketchMaps().containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_SKETCHMAP_DOES_NOT_EXIST.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[1])));
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      sketchMapPlugin.getFileManager().deleteSketchMap(args[1]);
      sender.sendMessage(Messages.SKETCHMAP_DELETED.asComponent()
          .replaceText(builder -> builder.match("%name%").replacement(args[1])));
      sender.playSound(Sounds.COMMAND_SUCCESS.sound());
      return true;
    }

    if (args[0].equalsIgnoreCase("get")) {
      if (!(sender instanceof final @NotNull Player player)) {
        sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (args.length != 2) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGS);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }
      final @NotNull List<ItemStack> items;
      if (args[1].startsWith("[") && args[1].endsWith("]")) {
        items = new ArrayList<>();
        for (final @NotNull SketchMap sketchmap : sketchMapPlugin.getLoadedSketchMaps().values()) {
          if (sketchmap.getID().contains(args[1].subSequence(1, args[1].length() - 1))) {
            items.addAll(ItemUtils.getOrderedItemSet(sketchmap));
          }
        }
      } else {
        final SketchMap map = sketchMapPlugin.getLoadedSketchMaps().get(args[1]);
        if (map == null) {
          sender.sendMessage(Messages.ERROR_SKETCHMAP_DOES_NOT_EXIST.asComponent()
              .replaceText(builder -> builder.match("%name%").replacement(args[1])));
          sender.playSound(Sounds.COMMAND_ERROR.sound());
          return true;
        }
        items = ItemUtils.getOrderedItemSet(map);
      }
      int inventorySize;
      for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
      }
      final @NotNull Inventory inv = Bukkit.createInventory(null, inventorySize,
          "§8SketchMap ID: §2§l" + args[1]);
      for (final ItemStack iStack : items) {
        inv.addItem(iStack);
      }
      player.openInventory(inv);
      return true;
    }

    if (args[0].equalsIgnoreCase("give")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGS);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }
      final @Nullable Player player = Bukkit.getPlayer(args[1]);

      if (player == null) {
        sender.sendMessage(Messages.ERROR_PLAYER_NOT_ONLINE.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[1])));
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      final SketchMap map = sketchMapPlugin.getLoadedSketchMaps().get(args[2]);
      if (map == null) {
        sender.sendMessage(Messages.ERROR_SKETCHMAP_DOES_NOT_EXIST.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[1])));
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }
      final @NotNull List<ItemStack> items = ItemUtils.getOrderedItemSet(map);

      int inventorySize;
      for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
      }
      final @NotNull Inventory inv = Bukkit.createInventory(null, inventorySize,
          "§8SketchMap ID: §2§l" + args[2]);
      for (final ItemStack iStack : items) {
        inv.addItem(iStack);
      }
      player.openInventory(inv);
      return true;
    }

    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGS);
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      final SketchMap sketchmap = sketchMapPlugin.getLoadedSketchMaps().get(args[1]);

      if (sketchmap == null) {
        sender.sendMessage(Messages.ERROR_SKETCHMAP_DOES_NOT_EXIST.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[1])));
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      if (sketchMapPlugin.getLoadedSketchMaps().containsKey(args[2].toLowerCase())) {
        sender.sendMessage(Messages.ERROR_SKETCHMAP_ALREADY_EXISTS.asComponent()
            .replaceText(builder -> builder.match("%name%").replacement(args[2])));
        sender.playSound(Sounds.COMMAND_ERROR.sound());
        return true;
      }

      sketchMapPlugin.getFileManager().renameSketchMap(args[1], args[2].toLowerCase());
      sender.sendMessage(Messages.SKETCHMAP_RENAMED.asComponent()
          .replaceText(builder -> builder.match("%name%").replacement(args[1]))
          .replaceText(builder -> builder.match("%newName%").replacement(args[2])));
      sender.playSound(Sounds.COMMAND_SUCCESS.sound());
      return true;
    }

    if (args[0].equalsIgnoreCase("help")) {
      sender.sendMessage(
          "§e§m                                                                                ");
      sender.sendMessage("");
      sender.sendMessage(
          "                            §a§lSketchMap §7v" + sketchMapPlugin.getDescription()
              .getVersion());
      sender.sendMessage("                                §7§oby aerulion");
      sender.sendMessage("");
      sender.sendMessage("                        §e§m                              ");
      sender.sendMessage("");
      sender.sendMessage(
          "§e- §a/sketchmap create <Name> <Bild-URL> [X:Y] §7| Erstellt eine neue SketchMap.");
      sender.sendMessage("§e- §a/sketchmap delete <Name> §7| Löscht die angegebene SketchMap.");
      sender.sendMessage(
          "§e- §a/sketchmap get <Name> §7| öffnet ein Inventar mit den benötigten Karten.");
      sender.sendMessage("§e- §a/sketchmap list §7| Listet alle geladenen SketchMaps auf.");
      sender.sendMessage("§e- §a/sketchmap help §7| Zeigt die Hilfeseite.");
      sender.sendMessage("");
      sender.sendMessage(
          "§e§m                                                                                ");
      return true;
    }

    if (args[0].equalsIgnoreCase("list")) {
      if (sketchMapPlugin.getLoadedSketchMaps().isEmpty()) {
        sender.sendMessage(Messages.NO_LOADED_SKETCHMAPS);
        return true;
      }
      sender.sendMessage(Messages.LOADED_SKETCHMAPS);
      final @NotNull List<String> maps = new ArrayList<>();
      for (final @NotNull SketchMap map : sketchMapPlugin.getLoadedSketchMaps().values()) {
        maps.add(map.getID());
      }
      Collections.sort(maps);
      for (final String map2 : maps) {
        sender.sendMessage("§a- §e" + map2);
      }
      sender.sendMessage(Messages.LOADED_SKETCHMAPS_COUNT.asComponent().replaceText(
          builder -> builder.match("%count%").replacement(String.valueOf(maps.size()))));
      return true;
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(final @NotNull CommandSender sender,
      final @NotNull Command command, final @NotNull String label, final String @NotNull [] args) {

    if (args.length < 2) {
      return SketchMapUtils.filterForTabComplete(
          new ArrayList<>(List.of("create", "delete", "get", "list", "help", "give", "rename")),
          args[0]);
    }

    if (args[0].equalsIgnoreCase("create")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(List.of("<Name>")), args[1]);
      }

      if (args.length == 3) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(List.of("<Bild-URL>")), args[2]);
      }

      if (args.length == 4) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(List.of("[X:Y]")), args[3]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("delete")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(
            new ArrayList<>(sketchMapPlugin.getLoadedSketchMaps().keySet()), args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("get")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(
            new ArrayList<>(sketchMapPlugin.getLoadedSketchMaps().keySet()), args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("give")) {
      if (args.length == 2) {
        return null;
      }
      if (args.length == 3) {
        return SketchMapUtils.filterForTabComplete(
            new ArrayList<>(sketchMapPlugin.getLoadedSketchMaps().keySet()), args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(
            new ArrayList<>(sketchMapPlugin.getLoadedSketchMaps().keySet()), args[1]);
      }
      if (args.length == 3) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(List.of("<NeuerName>")),
            args[1]);
      }
      return List.of();
    }

    return List.of();
  }

}