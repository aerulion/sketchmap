package net.aerulion.sketchmap;

import java.util.HashMap;
import net.aerulion.sketchmap.command.SketchMapCommand;
import net.aerulion.sketchmap.file.FileManager;
import net.aerulion.sketchmap.util.Messages;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main class of the plugin.
 */
public class SketchMapPlugin extends JavaPlugin {

  private final @NotNull FileManager fileManager = new FileManager(this);
  private final HashMap<String, SketchMap> loadedSketchMaps = new HashMap<>();

  @Override
  public void onDisable() {
    logInfo(Messages.CONSOLE_DISABLING);
    logInfo(Messages.CONSOLE_PLUGIN_DISABLED);
  }

  @Override
  public void onEnable() {
    logInfo(Messages.CONSOLE_ENABLING);
    final @Nullable PluginCommand pluginCommand = getCommand("sketchmap");
    if (pluginCommand != null) {
      pluginCommand.setExecutor(new SketchMapCommand(this));
    }
    logInfo(Messages.CONSOLE_LOADING_SKETCHMAPS);
    fileManager.loadAllSketchMaps();
    logInfo(Messages.CONSOLE_PLUGIN_ENABLED);
  }

  /**
   * Logs the provided message as an info.
   *
   * @param componentLike the component to log
   */
  public void logInfo(final @NotNull ComponentLike componentLike) {
    getComponentLogger().info(componentLike.asComponent());
  }

  /**
   * Logs the provided message as an error.
   *
   * @param componentLike the component to log
   */
  public void logError(final @NotNull ComponentLike componentLike) {
    getComponentLogger().error(componentLike.asComponent());
  }

  /**
   * Gets the currently loaded sketch maps.
   *
   * @return the sketchmaps
   */
  public HashMap<String, SketchMap> getLoadedSketchMaps() {
    return loadedSketchMaps;
  }

  /**
   * Gets the file manager.
   *
   * @return the file manager
   */
  public @NotNull FileManager getFileManager() {
    return fileManager;
  }

}