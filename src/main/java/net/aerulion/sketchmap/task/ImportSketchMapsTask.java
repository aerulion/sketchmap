package net.aerulion.sketchmap.task;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.aerulion.nucleus.api.base64.Base64Utils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ImportSketchMapsTask extends BukkitRunnable {

  private final CommandSender COMMANDSENDER;

  public ImportSketchMapsTask(CommandSender COMMANDSENDER) {
    this.COMMANDSENDER = COMMANDSENDER;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    File folder = new File("plugins/SketchMap/SketchMaps");
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isFile()) {
          try {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            BufferedImage bufferedImage = Base64Utils.decodeBufferedImage(
                fileConfiguration.getString("IMAGE"));
            Map<Integer, RelativeLocation> mapping = new HashMap<>();
            for (String map : fileConfiguration.getStringList("MAPPING")) {
              String[] split = map.split(" ");
              RelativeLocation loc = RelativeLocation.fromString(split[0]);
              int id = Integer.parseInt(split[1]);
              mapping.put(id, loc);
            }
            String sketchMapID = fileConfiguration.getString("SKETCHMAPID");
            sketchMapID = sketchMapID.toLowerCase();
            sketchMapID = sketchMapID.replace("ä", "ae");
            sketchMapID = sketchMapID.replace("ö", "oe");
            sketchMapID = sketchMapID.replace("ü", "ue");
            sketchMapID = sketchMapID.replace("ß", "ss");
            sketchMapID = sketchMapID.replace("-", "_");
              if (Main.LoadedSketchMaps.containsKey(sketchMapID)) {
                  sketchMapID = sketchMapID + "_duplicate";
              }
            int xPanes = fileConfiguration.getInt("XPANES");
            int yPanes = fileConfiguration.getInt("YPANES");
            Main.LoadedSketchMaps.put(sketchMapID,
                new SketchMap(UUID.randomUUID().toString(), sketchMapID, bufferedImage, xPanes,
                    yPanes, mapping, "CONSOLE", 0L));
            new SaveSketchMapTask(Main.LoadedSketchMaps.get(sketchMapID), COMMANDSENDER);
          } catch (IOException ignored) {
          }
        }
      }
    }
    COMMANDSENDER.sendMessage("Alle SketchMaps wurden importiert.");
  }
}