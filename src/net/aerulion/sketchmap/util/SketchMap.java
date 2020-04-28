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
	private Map<RelativeLocation, MapView> mapviews;
	private Map<Short, RelativeLocation> mapping;

	public SketchMap(final BufferedImage image, final String mapID, final int xPanes, final int yPanes, final BaseFormat format, final Map<Short, RelativeLocation> mapping) {
		if (!((image.getWidth() == xPanes * 128) && (image.getHeight() == yPanes * 128)))
			this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		else
			this.image = image;
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.format = format;
		this.mapping = mapping;
		this.mapviews = new HashMap<RelativeLocation, MapView>();
		loadSketchMap();
	}

	public void loadSketchMap() {
		this.mapviews.clear();
		for (final Short mapID : this.mapping.keySet()) {
			RelativeLocation relativelocation = this.mapping.get(mapID);
			MapView mapview = SketchMapUtils.getMapView(mapID);
			BufferedImage subImage = this.image.getSubimage(relativelocation.getX() * 128, relativelocation.getY() * 128, 128, 128);
			for (MapRenderer rend : mapview.getRenderers()) {
				mapview.removeRenderer(rend);
			}
			mapview.addRenderer((MapRenderer) new ImageRenderer(subImage));
			this.mapviews.put(relativelocation, mapview);
		}
	}

	public void unloadSketchMap() {
		for (MapView mapview : this.mapviews.values()) {
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

	public Map<RelativeLocation, MapView> getMapViews() {
		return this.mapviews;
	}

	public BaseFormat getBaseFormat() {
		return this.format;
	}
}
