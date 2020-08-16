package net.aerulion.sketchmap.cmd;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CMD_sketchmap implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!commandSender.hasPermission("sketchmap.use")) {
            commandSender.sendMessage(Messages.ERROR_NO_PERMISSION.get());
            SoundUtils.playSound(commandSender, SoundType.ERROR);
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
            SoundUtils.playSound(commandSender, SoundType.ERROR);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 4) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (args[1].length() < 3 || args[1].length() > 48) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_LENGTH.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!args[1].toLowerCase().equals(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NO_UPPERCASE.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (Utils.isInvalidNamespaceID(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_ILLEGAL_CHARACTERS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_ALREADY_TAKEN.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            URL imageURL;
            try {
                imageURL = new URL(args[2]);
            } catch (MalformedURLException ex) {
                commandSender.sendMessage(Messages.ERROR_MALFORMED_URL.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            int xPanes;
            int yPanes;
            try {
                String[] split = args[3].split(":");
                if (split.length != 2)
                    throw new IllegalArgumentException();
                xPanes = Integer.parseInt(split[0]);
                yPanes = Integer.parseInt(split[1]);
                if (xPanes < 1 || yPanes < 1)
                    throw new IllegalArgumentException();
            } catch (IllegalArgumentException exception) {
                commandSender.sendMessage(Messages.ERROR_SCALE_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            new CreateSketchMapTask(commandSender, args[1], imageURL, xPanes, yPanes);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            new DeleteSketchMapTask(commandSender, Main.LoadedSketchMaps.get(args[1]));
            return true;
        }

        if (args[0].equalsIgnoreCase("rename")) {
            if (args.length != 3) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (Utils.isInvalidNamespaceID(args[2])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_ILLEGAL_CHARACTERS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (Main.LoadedSketchMaps.containsKey(args[2])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_ALREADY_TAKEN.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            Main.LoadedSketchMaps.put(args[2], Main.LoadedSketchMaps.remove(args[1]));
            SketchMap sketchMap = Main.LoadedSketchMaps.get(args[2]);
            sketchMap.setNamespaceID(args[2]);
            new SaveSketchMapTask(sketchMap, commandSender);
            return true;
        }

        if (args[0].equalsIgnoreCase("exchangeimage")) {
            if (args.length != 3) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            URL imageURL;
            try {
                imageURL = new URL(args[2]);
            } catch (MalformedURLException ex) {
                commandSender.sendMessage(Messages.ERROR_MALFORMED_URL.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            new ExchangeImageTask(commandSender, Main.LoadedSketchMaps.get(args[1]), imageURL);
            return true;
        }

        if (args[0].equalsIgnoreCase("setowner")) {
            if (args.length != 3) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
            if (!offlinePlayer.hasPlayedBefore()) {
                commandSender.sendMessage(Messages.ERROR_OFFLINE_PLAYER_NOT_FOUND.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            SketchMap sketchMap = Main.LoadedSketchMaps.get(args[1]);
            sketchMap.setOwner(offlinePlayer.getUniqueId().toString());
            new SaveSketchMapTask(sketchMap, commandSender);
            return true;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Messages.ERROR_NO_PLAYER.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            Player player = (Player) commandSender;
            if (args.length != 2) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(player, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[1])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[1]);
                SoundUtils.playSound(player, SoundType.ERROR);
                return true;
            }
            Utils.openSketchMapInventory(player, Main.LoadedSketchMaps.get(args[1]));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length != 3) {
                commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                commandSender.sendMessage(Messages.ERROR_PLAYER_NOT_FOUND.get());
                SoundUtils.playSound(commandSender, SoundType.ERROR);
                return true;
            }
            if (!Main.LoadedSketchMaps.containsKey(args[2])) {
                commandSender.sendMessage(Messages.ERROR_NAMESPACE_ID_NOT_FOUND.get() + args[2]);
                SoundUtils.playSound(player, SoundType.ERROR);
                return true;
            }
            Utils.openSketchMapInventory(player, Main.LoadedSketchMaps.get(args[2]));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            ChatUtils.sendChatDividingLine(commandSender, "§e");
            commandSender.sendMessage("");
            ChatUtils.sendCenteredChatMessage(commandSender, "§a§lSketchMap§7 v" + Main.plugin.getDescription().getVersion());
            ChatUtils.sendCenteredChatMessage(commandSender, "§7§oby aerulion");
            commandSender.sendMessage("");
            ChatUtils.sendCenteredChatMessage(commandSender, "§e" + StringUtils.generateLine(30));
            commandSender.sendMessage("");
            commandSender.sendMessage("§e- §a/sketchmap create <NamespaceID> <Bild-URL> [X:Y] §7| Erstellt eine neue SketchMap.");
            commandSender.sendMessage("§e- §a/sketchmap delete <NamespaceID> §7| Löscht die angegebene SketchMap.");
            commandSender.sendMessage("§e- §a/sketchmap get <NamespaceID> §7| Öffnet ein Inventar mit den benötigten Karten.");
            commandSender.sendMessage("§e- §a/sketchmap give <NamespaceID> <Spieler> §7| Öffnet dem angegebenen Spieler ein Inventar mit den benötigten Karten.");
            commandSender.sendMessage("§e- §a/sketchmap rename <AlteNamespaceID> <NeueNamespaceID>§7| Bennent die angegebene SketchMap um.");
            commandSender.sendMessage("§e- §a/sketchmap exchangeimage <NamespaceID> <Bild-URL> §7| Ersetzt das ursprüngliche Bild mit dem neuen Bild.");
            commandSender.sendMessage("§e- §a/sketchmap setowner <NamespaceID> <Spieler> §7| Setzt den angegebenen Spieler als Besitzer der SketchMap.");
            commandSender.sendMessage("§e- §a/sketchmap help §7| Zeigt diese Hilfeseite.");
            commandSender.sendMessage("");
            ChatUtils.sendChatDividingLine(commandSender, "§e");
            return true;
        }

        commandSender.sendMessage(Messages.ERROR_WRONG_ARGUMENTS.get());
        SoundUtils.playSound(commandSender, SoundType.ERROR);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2)
            return CommandUtils.filterForTabCompleter(new ArrayList<>(Arrays.asList("create", "delete", "get", "help", "give", "rename", "exchangeimage", "setowner")), args[0]);
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Collections.singletonList("<NamespaceID>")), args[1]);
            if (args.length == 3)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Collections.singletonList("<Bild-URL>")), args[2]);
            if (args.length == 4)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Collections.singletonList("[X:Y]")), args[3]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[1]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("get")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[1]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 2)
                return null;
            if (args.length == 3)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[2]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("rename")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[1]);
            if (args.length == 3)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Collections.singletonList("<NeuerName>")), args[2]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("exchangeimage")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[1]);
            if (args.length == 3)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Collections.singletonList("<Bild-URL>")), args[2]);
            return Collections.emptyList();
        }
        if (args[0].equalsIgnoreCase("setowner")) {
            if (args.length == 2)
                return CommandUtils.filterForTabCompleter(new ArrayList<>(Main.LoadedSketchMaps.keySet()), args[1]);
            if (args.length == 3)
                return null;
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}