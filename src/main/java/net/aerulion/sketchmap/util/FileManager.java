package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.task.SaveTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {

  public static void loadSketchMapFromFile(final File file) throws IOException {
    final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
    final BufferedImage bufferedImage = Base64Utils.decodeImage(fileConfiguration.getString("IMAGE"));
    final Map<Short, RelativeLocation> mapping = new HashMap<>();
    for (final String map : fileConfiguration.getStringList("MAPPING")) {
      final String[] split = map.split(" ");
      final RelativeLocation loc = RelativeLocation.fromString(split[0]);
      final Short id = Short.parseShort(split[1]);
      mapping.put(id, loc);
    }
    final String sketchMapID = fileConfiguration.getString("SKETCHMAPID");
    final int xPanes = fileConfiguration.getInt("XPANES");
    final int yPanes = fileConfiguration.getInt("YPANES");
    final BaseFormat baseFormat = BaseFormat.valueOf(fileConfiguration.getString("BASEFORMAT"));
    Main.LoadedSketchMaps.put(sketchMapID,
        new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, baseFormat, mapping));
  }

  public static void loadAllSketchMaps() {
    final long start = System.currentTimeMillis();
    int count = 0;
    final File folder = new File("plugins/SketchMap/SketchMaps");
    final File[] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (final File file : listOfFiles) {
        if (file.isFile()) {
          try {
            loadSketchMapFromFile(file);
            count++;
          } catch (final IOException exception) {
            TextUtils.sendColoredConsoleMessage(
                Lang.CHAT_PREFIX + "§cFehler: Die Datei " + file.getName()
                    + " konnte nicht geladen werden");
          }
        }
      }
    }
    TextUtils.sendColoredConsoleMessage(
        Lang.CHAT_PREFIX + "§a" + count + Lang.CONSOLE_SKETCHMAPS_LOADED + (
            System.currentTimeMillis() - start) + "ms");
  }

  public static void saveSketchMapToFile(final String sketchMapID) {
    new SaveTask(sketchMapID);
  }

  public static void deleteSketchMap(final String sketchMapID) {
    final SketchMap sketchMap = Main.LoadedSketchMaps.get(sketchMapID);
    sketchMap.unloadSketchMap();
    Main.LoadedSketchMaps.remove(sketchMapID);
    final File file = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
    file.delete();
  }

  public static void renameSketchMap(final String sketchMapID, final String newname) {
    final File oldFile = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
    oldFile.delete();
    Main.LoadedSketchMaps.put(newname, Main.LoadedSketchMaps.remove(sketchMapID));
    final SketchMap sketchmap = Main.LoadedSketchMaps.get(newname);
    sketchmap.setID(newname);
    saveSketchMapToFile(newname);
  }

  public static void createNewSketchMap(final BufferedImage bufferedImage, final String sketchMapID, final int xPanes,
      final int yPanes) {
    final Map<Short, RelativeLocation> mapping = new HashMap<>();
    for (int x = 0; x < xPanes; ++x) {
      for (int y = 0; y < yPanes; ++y) {
        mapping.put((short) Bukkit.createMap(SketchMapUtils.getDefaultWorld()).getId(),
            RelativeLocation.fromString(x + ":" + y));
      }
    }
    Main.LoadedSketchMaps.put(sketchMapID,
        new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, BaseFormat.PNG, mapping));
    saveSketchMapToFile(sketchMapID);
  }
}