package net.aerulion.sketchmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import net.aerulion.sketchmap.file.SketchMapFileException;
import net.aerulion.sketchmap.map.RelativeLocation;
import net.aerulion.sketchmap.map.SketchMap;

public class SketchMapAPI {
	public static SketchMap getMapByID(final String id) {
		for (final SketchMap map : SketchMap.getLoadedMaps()) {
			if (map.getID().equalsIgnoreCase(id)) {
				return map;
			}
		}
		return null;
	}

	public static List<ItemStack> getOrderedItemSet(final SketchMap map) {
		final List<ItemStack> items = new ArrayList<ItemStack>();
		for (int y = 0; y < map.getLengthY(); ++y) {
			for (int x = 0; x < map.getLengthX(); ++x) {
				for (final RelativeLocation loc : map.getMapCollection().keySet()) {
					if (loc.getX() == x) {
						if (loc.getY() != y) {
							continue;
						}
						final ItemStack iStack = new ItemStack(Material.FILLED_MAP, 1);
						final MapMeta iMeta = (MapMeta) iStack.getItemMeta();
						iMeta.setMapView(map.getMapCollection().get(loc));
						iMeta.setLore(Arrays.asList("§e§m                           ", "§7SketchMap ID: §a" + map.getID(), "§7Pos-X: §a" + (x + 1), "§7Pos-Y: §a" + (y + 1), "§e§m                           "));
						iStack.setItemMeta(iMeta);
						items.add(iStack);
					}
				}
			}
		}
		return items;
	}

	public static SketchMap loadSketchMapFromFile(final File file) throws SketchMapFileException {
		YamlConfiguration config = null;
		try {
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception ex) {
			throw new SketchMapFileException("Invalid SketchMap File \"" + file.getName() + "\"");
		}
		final String[] fieldSet = { "x-panes", "y-panes", "map-collection", "base-format", "map-image" };
		String[] array;
		for (int length = (array = fieldSet).length, i = 0; i < length; ++i) {
			final String field = array[i];
			if (!config.isSet(field)) {
				throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" missing field \"" + field + "\"");
			}
		}
		final Integer xPanes = config.getInt("x-panes");
		if (xPanes == null || xPanes < 1) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" invalid field \"x-panes\"");
		}
		final Integer yPanes = config.getInt("y-panes");
		if (yPanes == null || yPanes < 1) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" invalid field \"y-panes\"");
		}
		final List<String> mapList = (List<String>) config.getStringList("map-collection");
		if (mapList == null) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" invalid field \"map-collection\"");
		}
		final Map<Short, RelativeLocation> mapCollection = new HashMap<Short, RelativeLocation>();
		for (final String map : mapList) {
			final String[] split = map.split(" ");
			if (split.length != 2) {
				throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" cannot parse field in \"map-colection\"");
			}
			final RelativeLocation loc = RelativeLocation.fromString(split[0]);
			if (loc == null) {
				throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" cannot parse field in \"map-colection\"");
			}
			Short id = null;
			try {
				id = Short.parseShort(split[1]);
			} catch (Exception ex2) {
				throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" cannot parse field in \"map-colection\"");
			}
			mapCollection.put(id, loc);
		}
		SketchMap.BaseFormat format = null;
		try {
			format = SketchMap.BaseFormat.valueOf(config.getString("base-format"));
		} catch (Exception ex3) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" cannot parse BaseFormat from field \"base-format\"");
		}
		final String b64Img = config.getString("map-image");
		if (b64Img == null) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" invalid field \"map-image\"");
		}
		BufferedImage image = null;
		try {
			image = SketchMapUtils.base64StringToImg(b64Img);
		} catch (Exception ex4) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" parse image from field \"map-image\"");
		}
		final String imageID = file.getName().substring(0, file.getName().lastIndexOf("."));
		if (getMapByID(imageID) != null) {
			throw new SketchMapFileException("Unable to load SketchMap file \"" + file.getName() + "\" A SketchMap by that ID already exists.");
		}
		return new SketchMap(image, imageID, xPanes, yPanes, format, mapCollection);
	}
}
