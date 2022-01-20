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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImportSketchMapsTask extends BukkitRunnable {

  private final CommandSender commandSender;

  public ImportSketchMapsTask(final CommandSender commandSender) {
    this.commandSender = commandSender;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    final @NotNull File folder = new File("plugins/SketchMap/SketchMaps");
    final File @Nullable [] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (final @NotNull File file : listOfFiles) {
        if (file.isFile()) {
          try {
            final @NotNull FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(
                file);
            final BufferedImage bufferedImage = Base64Utils.decodeBufferedImage(
                fileConfiguration.getString("IMAGE"));
            final @NotNull Map<Integer, RelativeLocation> mapping = new HashMap<>();
            for (final @NotNull String map : fileConfiguration.getStringList("MAPPING")) {
              final String @NotNull [] split = map.split(" ");
              final @Nullable RelativeLocation loc = RelativeLocation.fromString(split[0]);
              final int id = Integer.parseInt(split[1]);
              mapping.put(id, loc);
            }
            @Nullable String sketchMapID = fileConfiguration.getString("SKETCHMAPID");
            sketchMapID = sketchMapID.toLowerCase();
            sketchMapID = sketchMapID.replace("ä", "ae");
            sketchMapID = sketchMapID.replace("ö", "oe");
            sketchMapID = sketchMapID.replace("ü", "ue");
            sketchMapID = sketchMapID.replace("ß", "ss");
            sketchMapID = sketchMapID.replace("-", "_");
            if (Main.LOADED_SKETCH_MAPS.containsKey(sketchMapID)) {
              sketchMapID = sketchMapID + "_duplicate";
            }
            final int xPanes = fileConfiguration.getInt("XPANES");
            final int yPanes = fileConfiguration.getInt("YPANES");
            Main.LOADED_SKETCH_MAPS.put(sketchMapID,
                new SketchMap(UUID.randomUUID().toString(), sketchMapID, bufferedImage, xPanes,
                    yPanes, mapping, "CONSOLE", 0L));
            new SaveSketchMapTask(Main.LOADED_SKETCH_MAPS.get(sketchMapID), commandSender);
          } catch (final IOException ignored) {
          }
        }
      }
    }
    commandSender.sendMessage("Alle SketchMaps wurden importiert.");
  }
}