package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.SketchMapUtils;
import net.aerulion.sketchmap.task.SaveTask;

public class FileManager {

	public static void loadSpecificSketchMapFromFile(File SketchMapToLoad) throws IOException {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(SketchMapToLoad);
		BufferedImage image = Base64Utils.base64StringToImg(cfg.getString("IMAGE"));
		Map<Short, RelativeLocation> mapping = new HashMap<Short, RelativeLocation>();
		for (String map : cfg.getStringList("MAPPING")) {
			String[] split = map.split(" ");
			RelativeLocation loc = RelativeLocation.fromString(split[0]);
			Short id = Short.parseShort(split[1]);
			mapping.put(id, loc);
		}
		String sketchmapid = cfg.getString("SKETCHMAPID");
		int xpanes = cfg.getInt("XPANES");
		int ypanes = cfg.getInt("YPANES");
		BaseFormat baseformat = BaseFormat.valueOf(cfg.getString("BASEFORMAT"));
		Main.LoadedSketchMaps.put(sketchmapid, new SketchMap(image, sketchmapid, xpanes, ypanes, baseformat, mapping));
	}

	public static void loadAllSketchMaps() throws IOException {
		long start = System.currentTimeMillis();
		int count = 0;
		File folder = new File("plugins/SketchMap/SketchMaps");
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile()) {
					loadSpecificSketchMapFromFile(file);
					count++;
				}
			}
		}
		TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "§a" + count + Lang.CONSOLE_SKETCHMAPS_LOADED + (System.currentTimeMillis() - start) + "ms");
	}

	public static void saveSpecificSketchMapToFile(String sketchmapid) {
		new SaveTask(sketchmapid);
	}

	public static void deleteSketchMap(String sketchmapid) {
		SketchMap sketchmap = Main.LoadedSketchMaps.get(sketchmapid);
		sketchmap.unloadSketchMap();
		Main.LoadedSketchMaps.remove(sketchmapid);
		File sketchmapFile = new File("plugins/SketchMap/SketchMaps", sketchmapid + ".sketchmap");
		sketchmapFile.delete();
	}

	public static void createNewSketchMap(BufferedImage image, String sketchmapid, int xpanes, int ypanes) {
		Map<Short, RelativeLocation> mapping = new HashMap<Short, RelativeLocation>();
		for (int x = 0; x < xpanes; ++x) {
			for (int y = 0; y < ypanes; ++y) {
				mapping.put((short) Bukkit.createMap(SketchMapUtils.getDefaultWorld()).getId(), RelativeLocation.fromString(x + ":" + y));
			}
		}
		Main.LoadedSketchMaps.put(sketchmapid, new SketchMap(image, sketchmapid, xpanes, ypanes, BaseFormat.PNG, mapping));
		saveSpecificSketchMapToFile(sketchmapid);
	}

	public static void convertOldData() throws IOException {
		long start = System.currentTimeMillis();
		int count = 0;
		File folder = new File("plugins/SketchMap/CONVERT");
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile()) {
					convertSingleOldFile(file);
					count++;
				}
			}
		}
		TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "§a" + count + Lang.CONSOLE_SKETCHMAPS_LOADED + (System.currentTimeMillis() - start) + "ms");
	}

	public static void convertSingleOldFile(File file) throws IOException {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		BufferedImage image = Base64Utils.base64StringToImgOLD(cfg.getString("map-image"));
		Map<Short, RelativeLocation> mapping = new HashMap<Short, RelativeLocation>();
		for (String map : cfg.getStringList("map-collection")) {
			String[] split = map.split(" ");
			RelativeLocation loc = RelativeLocation.fromString(split[0]);
			Short id = Short.parseShort(split[1]);
			mapping.put(id, loc);
		}
		String sketchmapid = file.getName().substring(0, file.getName().length() - 10);
		int xpanes = cfg.getInt("x-panes");
		int ypanes = cfg.getInt("y-panes");
		BaseFormat baseformat = BaseFormat.PNG;
		Main.LoadedSketchMaps.put(sketchmapid, new SketchMap(image, sketchmapid, xpanes, ypanes, baseformat, mapping));
		saveSpecificSketchMapToFile(sketchmapid);
	}
}
