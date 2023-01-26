package net.aerulion.sketchmap.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Base64Utils;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {

  private final String sketchMapID;

  public SaveTask(final String sketchMapID) {
    this.sketchMapID = sketchMapID;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    final SketchMap sketchMap = Main.LoadedSketchMaps.get(this.sketchMapID);
    final File file = new File("plugins/SketchMap/SketchMaps", this.sketchMapID + ".sketchmap");
    final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
    try {
      fileConfiguration.set("IMAGE",
          Base64Utils.encodeImage(sketchMap.getImage(), sketchMap.getBaseFormat().name()));
      fileConfiguration.set("SKETCHMAPID", sketchMap.getID());
      fileConfiguration.set("XPANES", sketchMap.getXPanes());
      fileConfiguration.set("YPANES", sketchMap.getYPanes());
      fileConfiguration.set("BASEFORMAT", sketchMap.getBaseFormat().name());
      final List<String> mapping = new ArrayList<>();
      for (final RelativeLocation loc : sketchMap.getMapViews().keySet()) {
        mapping.add(loc.toString() + " " + sketchMap.getMapViews().get(loc).getId());
      }
      fileConfiguration.set("MAPPING", mapping);
      fileConfiguration.save(file);
    } catch (final IOException ignored) {
    }
  }
}