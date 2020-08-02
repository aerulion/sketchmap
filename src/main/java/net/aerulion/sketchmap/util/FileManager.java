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

    public static void loadSpecificSketchMapFromFile(File SketchMapToLoad) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(SketchMapToLoad);
        BufferedImage image = null;
        try {
            image = Base64Utils.base64StringToImg(cfg.getString("IMAGE"));
        } catch (IOException e) {
            TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "�cFehler: Die Datei " + SketchMapToLoad.getName() + " konnte nicht geladen werden");
        }
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

    public static void loadAllSketchMaps() {
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
        TextUtils.sendColoredConsoleMessage(Lang.CHAT_PREFIX + "�a" + count + Lang.CONSOLE_SKETCHMAPS_LOADED + (System.currentTimeMillis() - start) + "ms");
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

    public static void renameSketchMap(String sketchmapid, String newname) {
        File oldFile = new File("plugins/SketchMap/SketchMaps", sketchmapid + ".sketchmap");
        oldFile.delete();
        Main.LoadedSketchMaps.put(newname, Main.LoadedSketchMaps.remove(sketchmapid));
        SketchMap sketchmap = Main.LoadedSketchMaps.get(newname);
        sketchmap.setID(newname);
        saveSpecificSketchMapToFile(newname);
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
}
