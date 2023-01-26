package net.aerulion.sketchmap.CMDs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.BaseFormat;
import net.aerulion.sketchmap.util.CommandSound;
import net.aerulion.sketchmap.util.FileManager;
import net.aerulion.sketchmap.util.ItemUtils;
import net.aerulion.sketchmap.util.Lang;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.SketchMapUtils;
import net.aerulion.sketchmap.util.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CMD_SKETCHMAP implements CommandExecutor, TabCompleter {

  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String label,
      final String[] args) {

    if (!sender.hasPermission("sketchmap.use")) {
      sender.sendMessage(
          Lang.CHAT_PREFIX + "§c Du hast nicht die Rechte, diesen Befehl zu nutzen.");
      SoundUtils.playCommandSound(sender, CommandSound.ERROR);
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage(
          Lang.CHAT_PREFIX + "§c Zu wenige Argumente, für mehr Infos nutze /sketchmap help.");
      SoundUtils.playCommandSound(sender, CommandSound.ERROR);
      return true;
    }

    if (args[0].equalsIgnoreCase("create")) {
      if (args.length < 3) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      if (args[1].length() < 3 || args[1].length() > 32) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§cFehler: Map ID darf zwischen 3-32 Zeichen lang sein.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      if (!isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Map ID muss alphanumerisch sein.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      if (Main.LoadedSketchMaps.containsKey(args[1].toLowerCase())) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Diese SketchMap ID existiert bereits.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      URL url = null;
      try {
        url = new URL(args[2]);
      } catch (final MalformedURLException ex) {
        sender.sendMessage(Lang.CHAT_PREFIX
            + "§cFehler: Bild konnte nicht geladen werden. URL erscheint ungültig.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      Integer xPanes = null;
      Integer yPanes = null;

      if (args.length > 3) {
        final String[] split = args[3].split(":");
        if (split.length != 2) {
          sender.sendMessage(Lang.CHAT_PREFIX
              + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
          SoundUtils.playCommandSound(sender, CommandSound.ERROR);
          return true;
        }
        try {
          xPanes = Integer.parseInt(split[0]);
          yPanes = Integer.parseInt(split[1]);
        } catch (final Exception ex2) {
          sender.sendMessage(Lang.CHAT_PREFIX
              + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
          SoundUtils.playCommandSound(sender, CommandSound.ERROR);
          return true;
        }
        if (xPanes < 1 || yPanes < 1) {
          sender.sendMessage(Lang.CHAT_PREFIX
              + "§cFehler: Bild konnte nicht skaliert werden. Negative Argumente angegeben.");
          SoundUtils.playCommandSound(sender, CommandSound.ERROR);
          return true;
        }
      }

      try {
        final long startDownload = System.currentTimeMillis();
        sender.sendMessage(Lang.CHAT_PREFIX + "Bild wird heruntergeladen...");
        final BufferedImage image = ImageIO.read(url);
        sender.sendMessage(
            Lang.CHAT_PREFIX + "Bild wurde heruntergeladen. §8[" + (System.currentTimeMillis()
                - startDownload) + "ms]");
        final long startProccessing = System.currentTimeMillis();
        sender.sendMessage(Lang.CHAT_PREFIX + "Bild wird verarbeitet...");
        final String ext = url.getFile().substring(url.getFile().length() - 3);
        BaseFormat format;
        try {
          format = BaseFormat.valueOf(ext.toUpperCase());
        } catch (final IllegalArgumentException e) {
          format = null;
        }
        if (format == null) {
          sender.sendMessage(Lang.CHAT_PREFIX
              + "§cFehler: Bilder werden nur im Format .JPG und .PNG unterstützt.");
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
        FileManager.createNewSketchMap(image, args[1].toLowerCase(), xPanes, yPanes);
        sender.sendMessage(
            Lang.CHAT_PREFIX + "Bild wurde verarbeitet. §8[" + (System.currentTimeMillis()
                - startProccessing) + "ms]");
        sender.sendMessage(
            Lang.CHAT_PREFIX + "Die SketchMap §a§o" + args[1] + "§7 wurde erfolgreich erstellt.");
        SoundUtils.playCommandSound(sender, CommandSound.SUCCESS);
        return true;
      } catch (final IOException e) {
        sender.sendMessage(Lang.CHAT_PREFIX
            + "§cFehler: Das Bild konnte an der angegebenen URL nicht gefunden werden, wenn du denkst das wäre ein Fehler, versuche das Bild auf imgur.com hochzuladen.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }
    }

    if (args[0].equalsIgnoreCase("delete")) {

      if (args.length != 2) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      if (!Main.LoadedSketchMaps.containsKey(args[1])) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o" + args[1] + "§c existiert nicht.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      FileManager.deleteSketchMap(args[1]);
      sender.sendMessage(
          Lang.CHAT_PREFIX + "Die SketchMap §a§o" + args[1] + "§7 wurde erfolreich gelöscht.");
      SoundUtils.playCommandSound(sender, CommandSound.SUCCESS);
      return true;
    }

    if (args[0].equalsIgnoreCase("get")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§c Dieser Befehl kann nur als Spieler ausgeführt werden.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }
      final Player player = (Player) sender;

      if (args.length != 2) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }
      final List<ItemStack> items;
      if (args[1].startsWith("[") && args[1].endsWith("]")) {
        items = new ArrayList<ItemStack>();
        for (final SketchMap sketchmap : Main.LoadedSketchMaps.values()) {
          if (sketchmap.getID().contains(args[1].subSequence(1, args[1].length() - 1))) {
            for (final ItemStack item : ItemUtils.getOrderedItemSet(sketchmap)) {
              items.add(item);
            }
          }
        }
      } else {
        final SketchMap map = Main.LoadedSketchMaps.get(args[1]);
        if (map == null) {
          sender.sendMessage(
              Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o'" + args[1] + "'§c existiert nicht.");
          SoundUtils.playCommandSound(sender, CommandSound.ERROR);
          return true;
        }
        items = ItemUtils.getOrderedItemSet(map);
      }
      int inventorySize;
      for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
      }
      final Inventory inv = Bukkit.createInventory(null, inventorySize,
          "§8SketchMap ID: §2§l" + args[1]);
      for (final ItemStack iStack : items) {
        inv.addItem(iStack);
      }
      player.openInventory(inv);
      return true;
    }

    if (args[0].equalsIgnoreCase("give")) {
      if (args.length != 3) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }
      final Player player = Bukkit.getPlayer(args[1]);

      if (player == null) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§cFehler: Der Spieler §o'" + args[1] + "'§c ist nicht online.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      final SketchMap map = Main.LoadedSketchMaps.get(args[2]);
      if (map == null) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o'" + args[1] + "'§c existiert nicht.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }
      final List<ItemStack> items = ItemUtils.getOrderedItemSet(map);

      int inventorySize;
      for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
      }
      final Inventory inv = Bukkit.createInventory(null, inventorySize,
          "§8SketchMap ID: §2§l" + args[2]);
      for (final ItemStack iStack : items) {
        inv.addItem(iStack);
      }
      player.openInventory(inv);
      return true;
    }

    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length != 3) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      final SketchMap sketchmap = Main.LoadedSketchMaps.get(args[1]);

      if (sketchmap == null) {
        sender.sendMessage(
            Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o'" + args[1] + "'§c existiert nicht.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      if (Main.LoadedSketchMaps.containsKey(args[2].toLowerCase())) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Die SketchMap-ID §o'" + args[2]
            + "'§c existiert bereits.");
        SoundUtils.playCommandSound(sender, CommandSound.ERROR);
        return true;
      }

      FileManager.renameSketchMap(args[1], args[2].toLowerCase());
      sender.sendMessage(Lang.CHAT_PREFIX + "Die SketchMap §a§o'" + args[1] + "'§7 wurde in §a§o'"
          + args[2].toLowerCase() + "'§7 umbenannt.");
      SoundUtils.playCommandSound(sender, CommandSound.SUCCESS);
      return true;
    }

    if (args[0].equalsIgnoreCase("help")) {
      sender.sendMessage(
          "§e§m                                                                                ");
      sender.sendMessage("");
      sender.sendMessage("                            §a§lSketchMap §7v1.0");
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
      sender.sendMessage(Lang.CHAT_PREFIX + "Folgende SketchMaps sind geladen:");
      final List<String> maps = new ArrayList<String>();
      if (Main.LoadedSketchMaps.isEmpty()) {
        sender.sendMessage(Lang.CHAT_PREFIX + "§cBis jetzt wurden keine SketchMaps geladen.");
        return true;
      }
      for (final SketchMap map : Main.LoadedSketchMaps.values()) {
        maps.add(map.getID());
      }
      Collections.sort(maps);
      for (final String map2 : maps) {
        sender.sendMessage("§a- §e" + map2);
      }
      sender.sendMessage("§7Insgesamt sind §a" + maps.size() + "§7 SketchMaps geladen.");
      return true;
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(final CommandSender sender, final Command cmd,
      final String label, final String[] args) {

    if (args.length < 2) {
      return SketchMapUtils.filterForTabComplete(new ArrayList<String>(
          Arrays.asList("create", "delete", "get", "list", "help", "give", "rename")), args[0]);
    }

    if (args[0].equalsIgnoreCase("create")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<String>(Arrays.asList("<Name>")),
            args[1]);
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
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(Main.LoadedSketchMaps.keySet()),
            args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("get")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(Main.LoadedSketchMaps.keySet()),
            args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("give")) {
      if (args.length == 2) {
        return null;
      }
      if (args.length == 3) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(Main.LoadedSketchMaps.keySet()),
            args[1]);
      }
      return List.of();
    }

    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length == 2) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(Main.LoadedSketchMaps.keySet()),
            args[1]);
      }
      if (args.length == 3) {
        return SketchMapUtils.filterForTabComplete(new ArrayList<>(List.of("<NeuerName>")),
            args[1]);
      }
      return List.of();
    }

    return List.of();
  }

  public static boolean isAlphanumeric(String str) {
    if (str == null) {
      return false;
    }
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isLetterOrDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}