package net.aerulion.sketchmap.cmd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.aerulion.nucleus.api.chat.ChatUtils;
import net.aerulion.nucleus.api.command.CommandUtils;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.nucleus.api.string.StringUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.task.CreateSketchMapTask;
import net.aerulion.sketchmap.task.DeleteSketchMapTask;
import net.aerulion.sketchmap.task.ExchangeImageTask;
import net.aerulion.sketchmap.task.SaveSketchMapTask;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMD_sketchmap implements CommandExecutor, TabCompleter {

  @Override
  public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command,
      final @NotNull String label, final String @NotNull [] args) {

    if (!sender.hasPermission("sketchmap.use")) {
      sender.sendMessage(Messages.ERROR_NO_PERMISSION.get());
      SoundUtils.playSound(sender, SoundType.ERROR);
      return true;
    }

    if (args.length < 1) {
      sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
      SoundUtils.playSound(sender, SoundType.ERROR);
      return true;
    }

    if (args[0].equalsIgnoreCase("create")) {
      if (args.length != 4) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (args[1].length() < 3 || args[1].length() > 128) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_LENGTH.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!args[1].toLowerCase().equals(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NO_UPPERCASE.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (Utils.isInvalidNamespaceID(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_ILLEGAL_CHARACTERS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_ALREADY_TAKEN.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final @NotNull URL imageURL;
      try {
        imageURL = new URL(args[2]);
      } catch (final MalformedURLException ex) {
        sender.sendMessage(Messages.ERROR_MALFORMED_URL.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final int xPanes;
      final int yPanes;
      try {
        final String @NotNull [] split = args[3].split(":");
        if (split.length != 2) {
          throw new IllegalArgumentException();
        }
        xPanes = Integer.parseInt(split[0]);
        yPanes = Integer.parseInt(split[1]);
        if (xPanes < 1 || yPanes < 1) {
          throw new IllegalArgumentException();
        }
      } catch (final IllegalArgumentException exception) {
        sender.sendMessage(Messages.ERROR_SCALE_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      new CreateSketchMapTask(sender, args[1], imageURL, xPanes, yPanes);
      return true;
    }

    if (args[0].equalsIgnoreCase("delete")) {
      if (args.length != 2) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      new DeleteSketchMapTask(sender, Main.LOADED_SKETCH_MAPS.get(args[1]));
      return true;
    }

    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (args[2].length() < 3 || args[2].length() > 128) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_LENGTH.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (Utils.isInvalidNamespaceID(args[2])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_ILLEGAL_CHARACTERS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (Main.LOADED_SKETCH_MAPS.containsKey(args[2])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_ALREADY_TAKEN.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      Main.LOADED_SKETCH_MAPS.put(args[2], Main.LOADED_SKETCH_MAPS.remove(args[1]));
      final SketchMap sketchMap = Main.LOADED_SKETCH_MAPS.get(args[2]);
      sketchMap.setNamespaceID(args[2]);
      new SaveSketchMapTask(sketchMap, sender);
      return true;
    }

    if (args[0].equalsIgnoreCase("exchangeimage")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final @NotNull URL imageURL;
      try {
        imageURL = new URL(args[2]);
      } catch (final MalformedURLException ex) {
        sender.sendMessage(Messages.ERROR_MALFORMED_URL.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      new ExchangeImageTask(sender, Main.LOADED_SKETCH_MAPS.get(args[1]), imageURL);
      return true;
    }

    if (args[0].equalsIgnoreCase("setowner")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final @NotNull OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
      if (!offlinePlayer.hasPlayedBefore()) {
        sender.sendMessage(Messages.ERROR_OFFLINE_PLAYER_NOT_FOUND.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final SketchMap sketchMap = Main.LOADED_SKETCH_MAPS.get(args[1]);
      sketchMap.setOwner(offlinePlayer.getUniqueId().toString());
      new SaveSketchMapTask(sketchMap, sender);
      return true;
    }

    if (args[0].equalsIgnoreCase("get")) {
      if (!(sender instanceof final @NotNull Player player)) {
        sender.sendMessage(Messages.ERROR_NO_PLAYER.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (args.length != 2) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(player, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[1])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
        SoundUtils.playSound(player, SoundType.ERROR);
        return true;
      }
      Utils.openSketchMapInventory(player, Main.LOADED_SKETCH_MAPS.get(args[1]));
      return true;
    }

    if (args[0].equalsIgnoreCase("give")) {
      if (args.length != 3) {
        sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      final @Nullable Player player = Bukkit.getPlayer(args[1]);
      if (player == null) {
        sender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND.get());
        SoundUtils.playSound(sender, SoundType.ERROR);
        return true;
      }
      if (!Main.LOADED_SKETCH_MAPS.containsKey(args[2])) {
        sender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[2]);
        SoundUtils.playSound(player, SoundType.ERROR);
        return true;
      }
      Utils.openSketchMapInventory(player, Main.LOADED_SKETCH_MAPS.get(args[2]));
      return true;
    }

    if (args[0].equalsIgnoreCase("help")) {
      ChatUtils.sendChatDividingLine(sender, "§e");
      sender.sendMessage("");
      ChatUtils.sendCenteredChatMessage(sender,
          "§a§lSketchMap§7 v" + Main.plugin.getDescription().getVersion());
      ChatUtils.sendCenteredChatMessage(sender, "§7§oby aerulion");
      sender.sendMessage("");
      ChatUtils.sendCenteredChatMessage(sender, "§e" + StringUtils.generateLine(30));
      sender.sendMessage("");
      sender.sendMessage(
          "§e- §a/sketchmap create <NamespaceID> <Bild-URL> [X:Y] §7| Erstellt eine neue SketchMap.");
      sender.sendMessage(
          "§e- §a/sketchmap delete <NamespaceID> §7| Löscht die angegebene SketchMap.");
      sender.sendMessage(
          "§e- §a/sketchmap get <NamespaceID> §7| Öffnet ein Inventar mit den benötigten Karten.");
      sender.sendMessage(
          "§e- §a/sketchmap give <NamespaceID> <Spieler> §7| Öffnet dem angegebenen Spieler ein Inventar mit den benötigten Karten.");
      sender.sendMessage(
          "§e- §a/sketchmap rename <AlteNamespaceID> <NeueNamespaceID>§7| Bennent die angegebene SketchMap um.");
      sender.sendMessage(
          "§e- §a/sketchmap exchangeimage <NamespaceID> <Bild-URL> §7| Ersetzt das ursprüngliche Bild mit dem neuen Bild.");
      sender.sendMessage(
          "§e- §a/sketchmap setowner <NamespaceID> <Spieler> §7| Setzt den angegebenen Spieler als Besitzer der SketchMap.");
      sender.sendMessage("§e- §a/sketchmap help §7| Zeigt diese Hilfeseite.");
      sender.sendMessage("");
      ChatUtils.sendChatDividingLine(sender, "§e");
      return true;
    }

    sender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
    SoundUtils.playSound(sender, SoundType.ERROR);
    return true;
  }

  @Override
  public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
      final @NotNull String alias, final String @NotNull [] args) {
    if (args.length < 2) {
      return CommandUtils.filterForTabCompleter(new ArrayList<>(
          Arrays.asList("create", "delete", "get", "help", "give", "rename", "exchangeimage",
              "setowner")), args[0]);
    }
    if (args[0].equalsIgnoreCase("create")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(Utils.getNamespaceCategories(), args[1]);
      }
      if (args.length == 3) {
        return CommandUtils.filterForTabCompleter(
            new ArrayList<>(Collections.singletonList("<Bild-URL>")), args[2]);
      }
      if (args.length == 4) {
        return CommandUtils.filterForTabCompleter(
            new ArrayList<>(Collections.singletonList("[X:Y]")), args[3]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("delete")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[1]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("get")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[1]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("give")) {
      if (args.length == 2) {
        return null;
      }
      if (args.length == 3) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[2]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("rename")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[1]);
      }
      if (args.length == 3) {
        return CommandUtils.filterForTabCompleter(Utils.getNamespaceCategories(), args[2]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("exchangeimage")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[1]);
      }
      if (args.length == 3) {
        return CommandUtils.filterForTabCompleter(
            new ArrayList<>(Collections.singletonList("<Bild-URL>")), args[2]);
      }
      return Collections.emptyList();
    }
    if (args[0].equalsIgnoreCase("setowner")) {
      if (args.length == 2) {
        return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LOADED_SKETCH_MAPS.keySet()),
            args[1]);
      }
      if (args.length == 3) {
        return null;
      }
      return Collections.emptyList();
    }
    return Collections.emptyList();
  }
}