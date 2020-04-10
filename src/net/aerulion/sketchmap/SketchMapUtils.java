package net.aerulion.sketchmap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import net.aerulion.sketchmap.map.SketchMap;

public class SketchMapUtils {

	public final static String chatPrefix = "§e[§a§lSketchMap§e]§7 ";

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

	public static BufferedImage base64StringToImg(final String imageString) {
		// Read the image from a byte array
		BufferedImage bImage;
		try {
			bImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(imageString)));

			// Get the height and width of the image
			int width = bImage.getWidth();
			int height = bImage.getHeight();

			// Get the pixels of the image to an int array
			int[] pixels = bImage.getRGB(0, 0, width, height, null, 0, width);

			// Create a new buffered image without an alpha channel.
			// (TYPE_INT_RGB)
			BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// Set the pixels of the original image to the new image
			copy.setRGB(0, 0, width, height, pixels, 0, width);
			File outputfile = new File("image.jpg");
			ImageIO.write(copy, "jpg", outputfile);
			return copy;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// public static BufferedImage base64StringToImg(final String imageString) {
	// try {
	// return ImageIO.read(new
	// ByteArrayInputStream(Base64.getDecoder().decode(imageString)));
	// } catch (final IOException e) {
	// throw new UncheckedIOException(e);
	// }
	// }

	// public static BufferedImage base64StringToImg(final String imageString) {
	// BufferedImage image = null;
	// try {
	// final BASE64Decoder decoder = new BASE64Decoder();
	// final byte[] imageByte = decoder.decodeBuffer(imageString);
	// final ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
	// image = ImageIO.read(bis);
	// bis.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return image;
	// }

	public static String imgToBase64String(final BufferedImage image, final String type) {
		String imageString = null;
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, Base64.getEncoder().wrap(bos));
			imageString = bos.toString(StandardCharsets.ISO_8859_1.name());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	public static void sendColoredConsoleMessage(final String msg) {
		final ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(msg);
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

	public static ArrayList<String> getLoadedMapIDs() {
		ArrayList<String> loadedIDs = new ArrayList<>();
		for (SketchMap map : SketchMap.getLoadedMaps()) {
			loadedIDs.add(map.getID());
		}
		return loadedIDs;
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
