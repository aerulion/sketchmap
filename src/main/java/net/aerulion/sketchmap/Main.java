package net.aerulion.sketchmap;

import net.aerulion.sketchmap.CMDs.CMD_SKETCHMAP;
import net.aerulion.sketchmap.util.FileManager;
import net.aerulion.sketchmap.util.Lang;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.TextUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static HashMap<String, SketchMap> LoadedSketchMaps = new HashMap<String, SketchMap>();

    @Override
    public void onEnable() {
        TextUtils.sendColoredConsoleMessage(Lang.CONSOLE_ENABLING);
        Main.plugin = this;
        getCommand("sketchmap").setExecutor(new CMD_SKETCHMAP());
        getCommand("sketchmap").setTabCompleter(new CMD_SKETCHMAP());

        TextUtils.sendColoredConsoleMessage(Lang.CONSOLE_LOADING_SKETCHMAPS);
        FileManager.loadAllSketchMaps();
        TextUtils.sendColoredConsoleMessage(Lang.CONSOLE_PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() {
        TextUtils.sendColoredConsoleMessage(Lang.CONSOLE_DISABLING);
        TextUtils.sendColoredConsoleMessage(Lang.CONSOLE_PLUGIN_DISABLED);
    }
}