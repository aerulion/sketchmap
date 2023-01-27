package net.aerulion.sketchmap.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.aerulion.sketchmap.SketchMap;
import net.aerulion.sketchmap.SketchMapPlugin;
import net.aerulion.sketchmap.util.Base64Utils;
import net.aerulion.sketchmap.util.BaseFormat;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMapUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The file manager for handling file i/o.
 */
public class FileManager {

  private final @NotNull SketchMapPlugin sketchMapPlugin;

  /**
   * Instantiates a new file manager.
   *
   * @param sketchMapPlugin the plugin instance
   */
  @Contract(pure = true)
  public FileManager(final @NotNull SketchMapPlugin sketchMapPlugin) {
    super();
    this.sketchMapPlugin = sketchMapPlugin;
  }

  /**
   * Load the sketchmap from the given file.
   *
   * @param file the file to load the sketchmap from
   * @throws IOException when an unexpected error occurs
   */
  public void loadSketchMapFromFile(final @NotNull File file) throws IOException {
    final @NotNull FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
    final @NotNull BufferedImage bufferedImage = Base64Utils.decodeImage(
        fileConfiguration.getString("IMAGE"));
    final @NotNull Map<Short, RelativeLocation> mapping = new HashMap<>();
    for (final @NotNull String map : fileConfiguration.getStringList("MAPPING")) {
      final String @NotNull [] split = map.split(" ");
      final @Nullable RelativeLocation loc = RelativeLocation.fromString(split[0]);
      final @NotNull Short id = Short.parseShort(split[1]);
      mapping.put(id, loc);
    }
    final @Nullable String sketchMapID = fileConfiguration.getString("SKETCHMAPID");
    final int xPanes = fileConfiguration.getInt("XPANES");
    final int yPanes = fileConfiguration.getInt("YPANES");
    final @NotNull BaseFormat baseFormat = BaseFormat.valueOf(fileConfiguration.getString("BASEFORMAT"));
    sketchMapPlugin.getLoadedSketchMaps().put(sketchMapID,
        new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, baseFormat, mapping));
  }

  /**
   * Loads all sketchmaps from the files.
   */
  public void loadAllSketchMaps() {
    final long start = System.currentTimeMillis();
    int count = 0;
    final @NotNull File folder = new File("plugins/SketchMap/SketchMaps");
    final File @Nullable [] listOfFiles = folder.listFiles();
    if (listOfFiles != null) {
      for (final @NotNull File file : listOfFiles) {
        if (file.isFile()) {
          try {
            loadSketchMapFromFile(file);
            count++;
          } catch (final IOException exception) {
            sketchMapPlugin.logError(Messages.CONSOLE_ERROR_LOADING_FILE.asComponent()
                .replaceText(builder -> builder.match("%name%").replacement(file.getName())));
          }
        }
      }
    }
    final int finalCount = count;
    sketchMapPlugin.logInfo(Messages.CONSOLE_SKETCHMAPS_LOADED.asComponent()
        .replaceText(builder -> builder.match("%amount%").replacement(String.valueOf(finalCount)))
        .replaceText(builder -> builder.match("%time%")
            .replacement(String.valueOf(System.currentTimeMillis() - start))));
  }

  /**
   * Saves a currently loaded sketchmap to a file.
   *
   * @param sketchMapID the sketchmap to save
   */
  public void saveSketchMapToFile(final String sketchMapID) {
    Bukkit.getScheduler().runTaskAsynchronously(sketchMapPlugin, () -> {
      final SketchMap sketchMap = sketchMapPlugin.getLoadedSketchMaps().get(sketchMapID);
      final @NotNull File file = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
      final @NotNull FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
      try {
        fileConfiguration.set("IMAGE",
            Base64Utils.encodeImage(sketchMap.getImage(), sketchMap.getBaseFormat().name()));
        fileConfiguration.set("SKETCHMAPID", sketchMap.getID());
        fileConfiguration.set("XPANES", sketchMap.getXPanes());
        fileConfiguration.set("YPANES", sketchMap.getYPanes());
        fileConfiguration.set("BASEFORMAT", sketchMap.getBaseFormat().name());
        final @NotNull List<String> mapping = new ArrayList<>();
        for (final @NotNull RelativeLocation loc : sketchMap.getMapViews().keySet()) {
          mapping.add(loc.toString() + " " + sketchMap.getMapViews().get(loc).getId());
        }
        fileConfiguration.set("MAPPING", mapping);
        fileConfiguration.save(file);
      } catch (final IOException ignored) {
      }
    });
  }

  /**
   * Unloads and deletes the sketchmap with the given id.
   *
   * @param sketchMapID the sketchmap id
   */
  public void deleteSketchMap(final String sketchMapID) {
    final SketchMap sketchMap = sketchMapPlugin.getLoadedSketchMaps().get(sketchMapID);
    sketchMap.unloadSketchMap();
    sketchMapPlugin.getLoadedSketchMaps().remove(sketchMapID);
    final @NotNull File file = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
    file.delete();
  }

  /**
   * Renames the sketchmap.
   *
   * @param sketchMapID the old sketchmap id
   * @param newname     the new sketchmap id
   */
  public void renameSketchMap(final String sketchMapID, final String newname) {
    final @NotNull File oldFile = new File("plugins/SketchMap/SketchMaps", sketchMapID + ".sketchmap");
    oldFile.delete();
    sketchMapPlugin.getLoadedSketchMaps()
        .put(newname, sketchMapPlugin.getLoadedSketchMaps().remove(sketchMapID));
    final SketchMap sketchmap = sketchMapPlugin.getLoadedSketchMaps().get(newname);
    sketchmap.setID(newname);
    saveSketchMapToFile(newname);
  }

  /**
   * Creates and loads a new sketchmap.
   *
   * @param bufferedImage the buffered image to display
   * @param sketchMapID   the sketchmap id
   * @param xPanes        the x panes
   * @param yPanes        the y panes
   */
  public void createNewSketchMap(final @NotNull BufferedImage bufferedImage, final String sketchMapID,
      final int xPanes, final int yPanes) {
    final @NotNull Map<Short, RelativeLocation> mapping = new HashMap<>();
    for (int x = 0; x < xPanes; ++x) {
      for (int y = 0; y < yPanes; ++y) {
        mapping.put((short) Bukkit.createMap(SketchMapUtils.getDefaultWorld()).getId(),
            RelativeLocation.fromString(x + ":" + y));
      }
    }
    sketchMapPlugin.getLoadedSketchMaps().put(sketchMapID,
        new SketchMap(bufferedImage, sketchMapID, xPanes, yPanes, BaseFormat.PNG, mapping));
    saveSketchMapToFile(sketchMapID);
  }

}