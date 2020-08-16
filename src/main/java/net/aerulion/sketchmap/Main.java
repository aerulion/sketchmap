package net.aerulion.sketchmap;

import net.aerulion.nucleus.api.console.ConsoleUtils;
import net.aerulion.sketchmap.cmd.CMD_sketchmap;
import net.aerulion.sketchmap.task.LoadSketchMapsTask;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static final HashMap<String, SketchMap> LoadedSketchMaps = new HashMap<>();

    @Override
    public void onEnable() {
        ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_ENABLING.get());
        Main.plugin = this;
        getCommand("sketchmap").setExecutor(new CMD_sketchmap());
        new LoadSketchMapsTask();
        ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_PLUGIN_ENABLED.get());
    }

    @Override
    public void onDisable() {
        ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_DISABLING.get());
        ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_PLUGIN_DISABLED.get());
    }
}