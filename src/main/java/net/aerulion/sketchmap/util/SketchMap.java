package net.aerulion.sketchmap.util;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SketchMap {

    private final String UUID;
    private String namespaceID;
    private BufferedImage image;
    private final int X_PANES;
    private final int Y_PANES;
    private final Map<Integer, RelativeLocation> MAPPING;
    private final Map<RelativeLocation, MapView> MAPVIEWS;
    private String owner;
    private final long CREATION_TIMESTAMP;

    public SketchMap(String UUID, String namespaceID, BufferedImage image, int X_PANES, int Y_PANES, Map<Integer, RelativeLocation> MAPPING, String owner, long CREATION_TIMESTAMP) {
        this.UUID = UUID;
        this.namespaceID = namespaceID;
        this.X_PANES = X_PANES;
        this.Y_PANES = Y_PANES;
        setImage(image);
        this.MAPPING = MAPPING;
        this.MAPVIEWS = new HashMap<>();
        this.owner = owner;
        this.CREATION_TIMESTAMP = CREATION_TIMESTAMP;
        loadSketchMap();
    }

    public void loadSketchMap() {
        this.MAPVIEWS.clear();
        for (int mapID : this.MAPPING.keySet()) {
            RelativeLocation relativelocation = this.MAPPING.get(mapID);
            MapView mapView = Utils.getMapView(mapID);
            BufferedImage subImage = this.image.getSubimage(relativelocation.getX() * 128, relativelocation.getY() * 128, 128, 128);
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
        return namespaceID;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = (image.getWidth() == X_PANES * 128) && (image.getHeight() == Y_PANES * 128) ? image : Utils.resize(image, X_PANES * 128, Y_PANES * 128);
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getCreationTimestamp() {
        return CREATION_TIMESTAMP;
    }
}