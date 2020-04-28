package net.aerulion.sketchmap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

public class SketchMapUtils {

	public static BufferedImage resize(Image img, final Integer width, final Integer height) {
		img = img.getScaledInstance(width, height, 4);
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
		final Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	@SuppressWarnings("deprecation")
	public static MapView getMapView(final short id) {
		final MapView map = Bukkit.getMap(id);
		if (map != null) {
			return map;
		}
		return Bukkit.createMap(getDefaultWorld());
	}

	public static Block getTargetBlock(final Player player, final int i) {
		return player.getTargetBlock((HashSet<Material>) null, i);
	}

	public static World getDefaultWorld() {
		return Bukkit.getWorlds().get(0);
	}

	public static ArrayList<String> filterForTabcomplete(ArrayList<String> Input, String Filter) {
		if (Filter != null) {
			for (Iterator<String> iterator = Input.iterator(); iterator.hasNext();) {
				String value = iterator.next();
				if (!value.toLowerCase().startsWith(Filter.toLowerCase())) {
					{
						iterator.remove();
					}
				}
			}
		}
		return Input;
	}
}
