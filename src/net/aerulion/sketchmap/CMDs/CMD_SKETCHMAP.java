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

import net.aerulion.sketchmap.SketchMapAPI;
import net.aerulion.sketchmap.SketchMapUtils;
import net.aerulion.sketchmap.map.SketchMap;

public class CMD_SKETCHMAP implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(SketchMapUtils.chatPrefix + "§c Dieser Befehl kann nur als Spieler ausgeführt werden.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("sketchmap.use")) {
			sender.sendMessage(SketchMapUtils.chatPrefix + "§c Du hast nicht die Rechte, diesen Befehl zu nutzen.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(SketchMapUtils.chatPrefix + "§c Zu wenige Argumente, für mehr Infos nutze /sketchmap help.");
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length < 3) {
				player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Falsche Argumente.");
				return true;
			}
			if (args[1].length() < 3 || args[1].length() > 16) {
				player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Map ID darf zwischen 3-16 Zeichen lang sein.");
				return true;
			}
			if (!StringUtils.isAlphanumeric(args[1].replace("_", "").replace("-", ""))) {
				player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Map ID muss alphanumerisch sein.");
				return true;
			}
			if (SketchMapAPI.getMapByID(args[1]) == null) {
				URL url = null;
				try {
					url = new URL(args[2]);
				} catch (MalformedURLException ex) {
					player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Bild konnte nicht geladen werden. URL erscheint ungültig.");
					return true;
				}
				Integer xPanes = null;
				Integer yPanes = null;
				if (args.length > 3) {
					final String[] split = args[3].split(":");
					if (split.length != 2) {
						player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
						return true;
					}
					try {
						xPanes = Integer.parseInt(split[0]);
						yPanes = Integer.parseInt(split[1]);
					} catch (Exception ex2) {
						player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben.");
						return true;
					}
					if (xPanes < 1 || yPanes < 1) {
						player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Bild konnte nicht skaliert werden. Negative Argumente angegeben.");
						return true;
					}
				}
				try {
					player.sendMessage(SketchMapUtils.chatPrefix + "Bild wird heruntergeladen...");
					final BufferedImage image = ImageIO.read(url);
					player.sendMessage(SketchMapUtils.chatPrefix + "Bild wird verarbeitet...");
					final String ext = url.getFile().substring(url.getFile().length() - 3);
					final SketchMap.BaseFormat format = SketchMap.BaseFormat.fromExtension(ext);
					if (format == null) {
						player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Bilder werden nur im Format .JPG und .PNG unterstützt.");
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
					new SketchMap(image, args[1], xPanes, yPanes, format);
					player.sendMessage(SketchMapUtils.chatPrefix + "Die SketchMap §a§o" + args[1] + "§7 wurde erfolgreich erstellt.");
				} catch (IOException e) {
					player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Das Bild konnte an der angegebenen URL nicht gefunden werden, wenn du denkst das wäre ein Fehler, versuche das Bild auf imgur.com hochzuladen.");
				}
				return true;
			}
			player.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Diese Map ID existiert bereits.");

		}
		if (args[0].equalsIgnoreCase("delete")) {

			if (args.length != 2) {
				sender.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Falsche Argumente.");
				return true;
			}
			final SketchMap map = SketchMapAPI.getMapByID(args[1]);
			if (map == null) {
				sender.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Die SketchMap §o'" + args[1].toLowerCase() + "'§c existiert nicht.");
				return true;
			}
			final String mapID = map.getID();
			map.delete();
			sender.sendMessage(SketchMapUtils.chatPrefix + "Die SketchMap §a§o'" + mapID + "'§7 wurde erfolreich gelöscht.");

		}
		if (args[0].equalsIgnoreCase("get")) {

			if (args.length != 2) {
				sender.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Falsche Argumente.");
				return true;
			}
			List<ItemStack> items;
			if (args[1].startsWith("[") && args[1].endsWith("]")) {
				items = new ArrayList<ItemStack>();
				for (SketchMap sketchmap : SketchMap.getLoadedMaps()) {
					if (sketchmap.getID().contains(args[1].subSequence(1, args[1].length() - 1))) {
						for (ItemStack item : SketchMapAPI.getOrderedItemSet(sketchmap)) {
							items.add(item);
						}
					}
				}
			} else {
				final SketchMap map = SketchMapAPI.getMapByID(args[1]);
				if (map == null) {
					sender.sendMessage(SketchMapUtils.chatPrefix + "§cFehler: Die SketchMap §o'" + args[1].toLowerCase() + "'§c existiert nicht.");
					return true;
				}
				items = SketchMapAPI.getOrderedItemSet(map);
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
			sender.sendMessage(SketchMapUtils.chatPrefix + "Folgende SketchMaps sind geladen:");
			final List<String> maps = new ArrayList<String>();
			if (SketchMap.getLoadedMaps().isEmpty()) {
				sender.sendMessage(SketchMapUtils.chatPrefix + "§cBis jetzt wurden keine SketchMaps geladen.");
				return true;
			}
			for (final SketchMap map : SketchMap.getLoadedMaps()) {
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
			return SketchMapUtils.filterForTabcomplete(new ArrayList<String>(Arrays.asList("create", "delete", "get", "list", "help")), args[0]);

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
				return SketchMapUtils.filterForTabcomplete(SketchMapUtils.getLoadedMapIDs(), args[1]);
			}
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("get")) {
			if (args.length == 2) {
				return SketchMapUtils.filterForTabcomplete(SketchMapUtils.getLoadedMapIDs(), args[1]);
			}
			return Arrays.asList();
		}

		if (args[0].equalsIgnoreCase("help")) {
			return Arrays.asList();
		}

		return Arrays.asList();
	}

}
