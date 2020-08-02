package net.aerulion.sketchmap.util;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SketchMap {
    private final BufferedImage image;
    private String mapID;
    private final int xPanes;
    private final int yPanes;
    private final BaseFormat format;
    private final Map<RelativeLocation, MapView> mapviews;
    private final Map<Short, RelativeLocation> mapping;

    public SketchMap(BufferedImage image, String mapID, int xPanes, int yPanes, BaseFormat format, Map<Short, RelativeLocation> mapping) {
        if (!((image.getWidth() == xPanes * 128) && (image.getHeight() == yPanes * 128)))
            this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
        else
            this.image = image;
        this.mapID = mapID;
        this.xPanes = xPanes;
        this.yPanes = yPanes;
        this.format = format;
        this.mapping = mapping;
        this.mapviews = new HashMap<>();
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
            mapview.addRenderer(new ImageRenderer(subImage));
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

    public void setID(String id) {
        this.mapID = id;
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
