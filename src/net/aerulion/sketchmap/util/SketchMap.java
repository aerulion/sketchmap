package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aerulion.sketchmap.SketchMapUtils;

public class SketchMap {
	private BufferedImage image;
	private String mapID;
	private Integer xPanes;
	private Integer yPanes;
	private BaseFormat format;
	private Map<RelativeLocation, MapView> mapCollection;

	public SketchMap(final BufferedImage image, final String mapID, final int xPanes, final int yPanes, final BaseFormat format, final Map<Short, RelativeLocation> mapCollection) {
		this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.format = format;
		this.mapCollection = new HashMap<RelativeLocation, MapView>();
		this.loadSketchMap(mapCollection);
	}

	private void loadSketchMap(final Map<Short, RelativeLocation> mapCollection) {
		for (final Short mapID : mapCollection.keySet()) {
			final RelativeLocation loc = mapCollection.get(mapID);
			this.initMap(loc.getX(), loc.getY(), SketchMapUtils.getMapView(mapID));
		}
	}

	private void initMap(final int x, final int y, final MapView mapView) {
		final BufferedImage subImage = this.image.getSubimage(x * 128, y * 128, 128, 128);
		for (final MapRenderer rend : mapView.getRenderers()) {
			mapView.removeRenderer(rend);
		}
		mapView.addRenderer((MapRenderer) new ImageRenderer(subImage));
		this.mapCollection.put(new RelativeLocation(x, y), mapView);
	}

	public void unloadMap() {
		for (MapView mapview : mapCollection.values()) {
			for (MapRenderer maprenderer : mapview.getRenderers()) {
				mapview.removeRenderer(maprenderer);
			}
		}
	}

	public String getID() {
		return this.mapID;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public int getXPanes() {
		return this.xPanes;
	}

	public int getYPanes() {
		return this.yPanes;
	}

	public Map<RelativeLocation, MapView> getMapCollection() {
		return this.mapCollection;
	}

	public BaseFormat getBaseFormat() {
		return this.format;
	}

	// public enum BaseFormat {
	// PNG("PNG", 0), JPEG("JPEG", 1);
	//
	// private BaseFormat(final String s, final int n) {
	// }
	//
	// public String getExtension() {
	// if (this == BaseFormat.PNG) {
	// return "png";
	// }
	// if (this == BaseFormat.JPEG) {
	// return "jpg";
	// }
	// return null;
	// }
	//
	// public static BaseFormat fromExtension(final String ext) {
	// if (ext.equalsIgnoreCase("png")) {
	// return BaseFormat.PNG;
	// }
	// if (ext.equalsIgnoreCase("jpg")) {
	// return BaseFormat.JPEG;
	// }
	// return null;
	// }
	// }
}
