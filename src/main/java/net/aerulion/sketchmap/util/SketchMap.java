package net.aerulion.sketchmap.util;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SketchMap {

    private final String UUID;
    private final String NAMESPACE_ID;
    private final BufferedImage IMAGE;
    private final int X_PANES;
    private final int Y_PANES;
    private final Map<Integer, RelativeLocation> MAPPING;
    private final Map<RelativeLocation, MapView> MAPVIEWS;

    public SketchMap(String UUID, String NAMESPACE_ID, BufferedImage IMAGE, int X_PANES, int Y_PANES, Map<Integer, RelativeLocation> MAPPING) {
        this.UUID = UUID;
        this.NAMESPACE_ID = NAMESPACE_ID;
        this.IMAGE = (IMAGE.getWidth() == X_PANES * 128) && (IMAGE.getHeight() == Y_PANES * 128) ? IMAGE : Utils.resize(IMAGE, X_PANES * 128, Y_PANES * 128);
        this.X_PANES = X_PANES;
        this.Y_PANES = Y_PANES;
        this.MAPPING = MAPPING;
        this.MAPVIEWS = new HashMap<>();
        loadSketchMap();
    }

    public void loadSketchMap() {
        this.MAPVIEWS.clear();
        for (int mapID : this.MAPPING.keySet()) {
            RelativeLocation relativelocation = this.MAPPING.get(mapID);
            MapView mapView = Utils.getMapView(mapID);
            BufferedImage subImage = this.IMAGE.getSubimage(relativelocation.getX() * 128, relativelocation.getY() * 128, 128, 128);
            for (MapRenderer mapRenderer : mapView.getRenderers()) {
                mapView.removeRenderer(mapRenderer);
            }
            mapView.addRenderer(new ImageRenderer(subImage));
            this.MAPVIEWS.put(relativelocation, mapView);
        }
    }

    public void unloadSketchMap() {
        for (MapView mapview : this.MAPVIEWS.values()) {
            for (MapRenderer maprenderer : mapview.getRenderers()) {
                mapview.removeRenderer(maprenderer);
            }
        }
    }

    public String getUUID() {
        return UUID;
    }

    public String getNamespaceID() {
        return NAMESPACE_ID;
    }

    public BufferedImage getImage() {
        return IMAGE;
    }

    public int getXPanes() {
        return X_PANES;
    }

    public int getYPanes() {
        return Y_PANES;
    }

    public Map<RelativeLocation, MapView> getMapViews() {
        return MAPVIEWS;
    }
}
