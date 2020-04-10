package net.aerulion.sketchmap.file;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import net.aerulion.sketchmap.SketchMapAPI;
import net.aerulion.sketchmap.SketchMapPlugin;
import net.aerulion.sketchmap.SketchMapUtils;

public class SketchMapLoader {
	private static File mapsDirectory;
	private static File dataFolder;

	public static File getDataFolder() {
		if (SketchMapLoader.dataFolder != null) {
			return SketchMapLoader.dataFolder;
		}
		SketchMapLoader.dataFolder = SketchMapPlugin.getPlugin().getDataFolder();
		if (SketchMapLoader.dataFolder.exists()) {
			return SketchMapLoader.dataFolder;
		}
		SketchMapLoader.dataFolder.mkdirs();
		return SketchMapLoader.dataFolder;
	}

	public static File getMapsDirectory() {
		if (SketchMapLoader.mapsDirectory != null) {
			return SketchMapLoader.mapsDirectory;
		}
		SketchMapLoader.mapsDirectory = new File(String.valueOf(getDataFolder().toString()) + "/" + "sketchmaps/");
		if (SketchMapLoader.mapsDirectory.exists()) {
			return SketchMapLoader.mapsDirectory;
		}
		SketchMapLoader.mapsDirectory.mkdirs();
		return SketchMapLoader.mapsDirectory;
	}

	public static void loadMaps() {
		Bukkit.getScheduler().runTaskAsynchronously(SketchMapPlugin.plugin, () -> {
			int amount = 0;
			File[] listFiles;
			for (int length = (listFiles = getMapsDirectory().listFiles()).length, i = 0; i < length; ++i) {
				final File file = listFiles[i];
				if (file.getName().endsWith(".sketchmap")) {
					try {
						SketchMapAPI.loadSketchMapFromFile(file);
						amount++;
					} catch (SketchMapFileException ex) {
						Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
					}
				}
			}
			SketchMapUtils.sendColoredConsoleMessage("§e[§aSketchMap§e] Es wurden " + amount + " SketchMaps geladen.");
		});
	}
}
