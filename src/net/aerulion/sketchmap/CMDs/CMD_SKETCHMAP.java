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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.SketchMapUtils;
import net.aerulion.sketchmap.util.BaseFormat;
import net.aerulion.sketchmap.util.FileManager;
import net.aerulion.sketchmap.util.ItemUtils;
import net.aerulion.sketchmap.util.Lang;
import net.aerulion.sketchmap.util.SketchMap;

public class CMD_SKETCHMAP implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.CHAT_PREFIX + "§c Dieser Befehl kann nur als Spieler ausgeführt werden.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("sketchmap.use")) {
			sender.sendMessage(Lang.CHAT_PREFIX + "§c Du hast nicht die Rechte, diesen Befehl zu nutzen.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(Lang.CHAT_PREFIX + "§c Zu wenige Argumente, für mehr Infos nutze /sketchmap help.");
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length < 3) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
				return true;
			}

			if (args[1].length() < 3 || args[1].length() > 16) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Map ID darf zwischen 3-16 Zeichen lang sein.");
				return true;
			}

			if (!StringUtils.isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Map ID muss alphanumerisch sein.");
				return true;
			}

			if (Main.LoadedSketchMaps.containsKey(args[1])) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Diese SketchMap ID existiert bereits.");
				return true;
			}

			URL url = null;
			try {
				url = new URL(args[2]);
			} catch (MalformedURLException ex) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Bild konnte nicht geladen werden. URL erscheint ungültig.");
				return true;
			}

			Integer xPanes = null;
			Integer yPanes = null;

			if (args.length > 3) {
				final String[] split = args[3].split(":");
				if (split.length != 2) {
					player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
					return true;
				}
				try {
					xPanes = Integer.parseInt(split[0]);
					yPanes = Integer.parseInt(split[1]);
				} catch (Exception ex2) {
					player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
					return true;
				}
				if (xPanes < 1 || yPanes < 1) {
					player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Bild konnte nicht skaliert werden. Negative Argumente angegeben.");
					return true;
				}
			}

			try {
				player.sendMessage(Lang.CHAT_PREFIX + "Bild wird heruntergeladen...");
				final BufferedImage image = ImageIO.read(url);
				player.sendMessage(Lang.CHAT_PREFIX + "Bild wird verarbeitet...");
				final String ext = url.getFile().substring(url.getFile().length() - 3);
				final BaseFormat format = BaseFormat.valueOf(ext.toUpperCase());
				if (format == null) {
					player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Bilder werden nur im Format .JPG und .PNG unterstützt.");
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
				FileManager.createNewSketchMap(image, args[1], xPanes, yPanes, format);
				player.sendMessage(Lang.CHAT_PREFIX + "Die SketchMap §a§o" + args[1] + "§7 wurde erfolgreich erstellt.");
			} catch (IOException e) {
				player.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Das Bild konnte an der angegebenen URL nicht gefunden werden, wenn du denkst das wäre ein Fehler, versuche das Bild auf imgur.com hochzuladen.");
			}
			return true;

		}
		if (args[0].equalsIgnoreCase("delete")) {

			if (args.length != 2) {
				sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
				return true;
			}
			if (!Main.LoadedSketchMaps.containsKey(args[1])) {
				sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o" + args[1] + "§c existiert nicht.");
				return true;
			}
			FileManager.deleteSketchMap(args[1]);
			sender.sendMessage(Lang.CHAT_PREFIX + "Die SketchMap §a§o" + args[1] + "§7 wurde erfolreich gelöscht.");

		}
		if (args[0].equalsIgnoreCase("get")) {

			if (args.length != 2) {
				sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Falsche Argumente.");
				return true;
			}
			List<ItemStack> items;
			if (args[1].startsWith("[") && args[1].endsWith("]")) {
				items = new ArrayList<ItemStack>();
				for (SketchMap sketchmap : Main.LoadedSketchMaps.values()) {
					if (sketchmap.getID().contains(args[1].subSequence(1, args[1].length() - 1))) {
						for (ItemStack item : ItemUtils.getOrderedItemSet(sketchmap)) {
							items.add(item);
						}
					}
				}
			} else {
				final SketchMap map = Main.LoadedSketchMaps.get(args[1]);
				if (map == null) {
					sender.sendMessage(Lang.CHAT_PREFIX + "§cFehler: Die SketchMap §o'" + args[1].toLowerCase() + "'§c existiert nicht.");
					return true;
				}
				items = ItemUtils.getOrderedItemSet(map);
			}
			int inventorySize;
			for (inventorySize = items.size() + 1; inventorySize % 9 != 0; ++inventorySize) {
			}
			final Inventory inv = Bukkit.createInventory(null, inventorySize, "§8SketchMap ID: §2§l" + args[1]);
			for (final ItemStack iStack : items) {
				inv.addItem(new ItemStack[] { iStack });
			}
			player.openInventory(inv);

		}
		if (args[0].equalsIgnoreCase("reload")) {
			try {
				FileManager.reloadAllSketchMaps();
			} catch (IOException e) {
				sender.sendMessage(Lang.CHAT_PREFIX + "Fehler: Die SketchMaps konnten nicht geladen werden");
				return true;
			}
			sender.sendMessage(Lang.CHAT_PREFIX + "Die SketchMaps wurden erfolreich neugeladen.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("convert")) {
			try {
				FileManager.convertOldData();
			} catch (IOException e) {
				sender.sendMessage(Lang.CHAT_PREFIX + "Fehler: Die SketchMaps konnten nicht konvertiert werden");
				return true;
			}
			sender.sendMessage(Lang.CHAT_PREFIX + "Die SketchMaps wurden erfolreich konvertiert.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage("§e§m                                                                                ");
			sender.sendMessage("");
			sender.sendMessage("                            §a§lSketchMap §7v1.0");
			sender.sendMessage("                                §7§oby aerulion");
			sender.sendMessage("");
			sender.sendMessage("                        §e§m                              ");
			sender.sendMessage("");
			sender.sendMessage("§e- §a/sketchmap create <Name> <Bild-URL> [X:Y] §7| Erstellt eine neue SketchMap.");
			sender.sendMessage("§e- §a/sketchmap delete <Name> §7| Löscht die angegebene SketchMap.");
			sender.sendMessage("§e- §a/sketchmap get <Name> §7| Öffnet ein Inventar mit den benötigten Karten.");
			sender.sendMessage("§e- §a/sketchmap list §7| Listet alle geladenen SketchMaps auf.");
			sender.sendMessage("§e- §a/sketchmap help §7| Zeigt die Hilfeseite.");
			sender.sendMessage("");
			sender.sendMessage("§e§m                                                                                ");

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

		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length < 2) {
			return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Arrays.asList("create", "delete", "get", "list", "help", "reload")), args[0]);

		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length == 2) {
				return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Arrays.asList("<Name>")), args[1]);
			}

			if (args.length == 3) {
				return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Arrays.asList("<Bild-URL>")), args[2]);
			}

			if (args.length == 4) {
				return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Arrays.asList("[X:Y]")), args[3]);
			}
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("delete")) {
			if (args.length == 2) {
				return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Main.LoadedSketchMaps.keySet()), args[1]);
			}
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("get")) {
			if (args.length == 2) {
				return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Main.LoadedSketchMaps.keySet()), args[1]);
			}
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("help")) {
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("reload")) {
			return Arrays.asList();
		}

		return Arrays.asList();
	}

}
