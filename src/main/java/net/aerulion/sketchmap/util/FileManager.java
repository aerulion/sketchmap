package net.aerulion.sketchmap.util;

import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.task.SaveTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static void loadSketchMapFromFile(File file) throws IOException {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        BufferedImage bufferedImage = Base64Utils.decodeImage(fileConfiguration.getString("IMAGE"));
        Map<Short, RelativeLocation> mapping = new HashMap<>();
        for (String map : fileConfiguration.getStringList("MAPPING")) {
            String[] split = map.split(" ");
            RelativeLocation loc = RelativeLocation.fromString(split[0]);
            Short id = Short.parseShort(split[1]);
            mapping.put(id, loc);
        }
        String sketchMapID = fileConfiguration.getString("SKETCHMAPID");
        int xPanes = fileConfiguration.getInt("XPANES");
        int yPanes = fileConfiguration.getInt("YPANES");
        BaseFormat baseFormat = BaseFormat.valueOf(fileConfiguration.getString("BASEFORMAT"));
        Main.LoadedSketchMaps.put(sketchMapID, new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, baseFormat, mapping));
    }

    public static void loadAllSketchMaps() {
        long start = System.currentTimeMillis();
        int count = 0;
        File folder = new File("plugins/SketchMap/SketchMaps");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        loadSketchMapFromFile(file);
                        count++;
                    } catch (IOException exception) {
                        TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "§cFehler: Die Datei " + file.getName() + " konnte nicht geladen werden");
                    }
                }
            }
        }
        TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "§a" + count + Lang.CONSOLE_SKETCHMAPS_LOADED + (System.currentTimeMillis() - start) + "ms");
    }

    public static void saveSketchMapToFile(String sketchMapID) {
        new SaveTask(sketchMapID);
    }

    public static void deleteSketchMap(String sketchMapID) {
        SketchMap sketchMap = Main.LoadedSketchMaps.get(sketchMapID);
        sketchMap.unloadSketchMap();
        Main.LoadedSketchMaps.remove(sketchMapID);
        File file = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
        file.delete();
    }

    public static void renameSketchMap(String sketchMapID, String newname) {
        File oldFile = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
        oldFile.delete();
        Main.LoadedSketchMaps.put(newname, Main.LoadedSketchMaps.remove(sketchMapID));
        SketchMap sketchmap = Main.LoadedSketchMaps.get(newname);
        sketchmap.setID(newname);
        saveSketchMapToFile(newname);
    }

    public static void createNewSketchMap(BufferedImage bufferedImage, String sketchMapID, int xPanes, int yPanes) {
        Map<Short, RelativeLocation> mapping = new HashMap<>();
        for (int x = 0; x < xPanes; ++x) {
            for (int y = 0; y < yPanes; ++y) {
                mapping.put((short) Bukkit.createMap(SketchMapUtils.getDefaultWorld()).getId(), RelativeLocation.fromString(x + ":" + y));
            }
        }
        Main.LoadedSketchMaps.put(sketchMapID, new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, BaseFormat.PNG, mapping));
        saveSketchMapToFile(sketchMapID);
    }
}