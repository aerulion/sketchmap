package net.aerulion.sketchmap;

import org.bukkit.plugin.java.JavaPlugin;

import net.aerulion.sketchmap.CMDs.CMD_SKETCHMAP;
import net.aerulion.sketchmap.file.SketchMapLoader;

public class SketchMapPlugin extends JavaPlugin {
	public static SketchMapPlugin plugin;

	public void onEnable() {
		SketchMapPlugin.plugin = this;

		getCommand("sketchmap").setExecutor(new CMD_SKETCHMAP());
		getCommand("sketchmap").setTabCompleter(new CMD_SKETCHMAP());

		SketchMapLoader.loadMaps();
		this.sendEnabledMessage();
	}

	private void sendEnabledMessage() {
		SketchMapUtils.sendColoredConsoleMessage("§e[§aSketchMap§e] Das Plugin wurde aktiviert.");
	}

	public static SketchMapPlugin getPlugin() {
		return SketchMapPlugin.plugin;
	}
}
