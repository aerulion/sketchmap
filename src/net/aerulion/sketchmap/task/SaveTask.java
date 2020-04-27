package net.aerulion.sketchmap.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Base64Utils;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMap;

public class SaveTask extends BukkitRunnable {

	private final String sketchmapid;

	public SaveTask(String sketchmapid) {
		this.sketchmapid = sketchmapid;
		this.runTaskAsynchronously(Main.plugin);
	}

	@Override
	public void run() {
		SketchMap sketchmap = Main.LoadedSketchMaps.get(this.sketchmapid);
		File sketchmapFile = new File("plugins/SketchMap/SketchMaps", this.sketchmapid + ".sketchmap");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(sketchmapFile);
		cfg.set("IMAGE", Base64Utils.imgToBase64String(sketchmap.getImage(), sketchmap.getBaseFormat().name()));
		cfg.set("SKETCHMAPID", sketchmap.getID());
		cfg.set("XPANES", sketchmap.getXPanes());
		cfg.set("YPANES", sketchmap.getYPanes());
		cfg.set("BASEFORMAT", sketchmap.getBaseFormat().name());
		final List<String> mapping = new ArrayList<String>();
		for (final RelativeLocation loc : sketchmap.getMapCollection().keySet()) {
			mapping.add(String.valueOf(String.valueOf(loc.toString())) + " " + sketchmap.getMapCollection().get(loc).getId());
		}
		cfg.set("MAPPING", mapping);
		try {
			cfg.save(sketchmapFile);
		} catch (IOException e) {
		}
	}
}
