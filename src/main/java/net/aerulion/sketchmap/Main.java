package net.aerulion.sketchmap;

import java.util.HashMap;
import java.util.Map;
import net.aerulion.nucleus.api.console.ConsoleUtils;
import net.aerulion.sketchmap.cmd.CMD_sketchmap;
import net.aerulion.sketchmap.task.LoadSketchMapsTask;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  public static final Map<String, SketchMap> LOADED_SKETCH_MAPS = new HashMap<>();
  public static Main plugin;

  @Override
  public void onDisable() {
    ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_DISABLING.get());
    ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_PLUGIN_DISABLED.get());
  }

  @Override
  public void onEnable() {
    ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_ENABLING.get());
    plugin = this;
    getCommand("sketchmap").setExecutor(new CMD_sketchmap());
    new LoadSketchMapsTask();
    ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_PLUGIN_ENABLED.get());
  }
}