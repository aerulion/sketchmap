package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SketchMap {

  private final String uuid;
  private final int xPanes;
  private final int yPanes;
  private final Map<Integer, RelativeLocation> mapping;
  private final @NotNull Map<RelativeLocation, MapView> mapViews;
  private String namespaceID;
  private BufferedImage image;
  private String owner;
  private long creationTimestamp;

  public SketchMap(final String uuid, final String namespaceID, final @NotNull BufferedImage image,
      final int xPanes, final int yPanes, final Map<Integer, RelativeLocation> mapping,
      final String owner, final long creationTimestamp) {
    this.uuid = uuid;
    this.namespaceID = namespaceID;
    this.xPanes = xPanes;
    this.yPanes = yPanes;
    setImage(image);
    this.mapping = mapping;
    this.mapViews = new HashMap<>();
    this.owner = owner;
    this.creationTimestamp = creationTimestamp;
    loadSketchMap();
  }

  public void loadSketchMap() {
    this.mapViews.clear();
    for (final Entry<Integer, RelativeLocation> entry : this.mapping.entrySet()) {
      final RelativeLocation relativelocation = entry.getValue();
      final @Nullable MapView mapView = Utils.getMapView(entry.getKey());
      final BufferedImage subImage = this.image.getSubimage(relativelocation.getX() * 128,
          relativelocation.getY() * 128, 128, 128);
      for (final MapRenderer mapRenderer : mapView.getRenderers()) {
        mapView.removeRenderer(mapRenderer);
      }
      mapView.addRenderer(new ImageRenderer(subImage));
      this.mapViews.put(relativelocation, mapView);
    }
  }

  public void unloadSketchMap() {
    for (final @NotNull MapView mapview : this.mapViews.values()) {
      for (final MapRenderer maprenderer : mapview.getRenderers()) {
        mapview.removeRenderer(maprenderer);
      }
    }
  }

  public String getUuid() {
    return uuid;
  }

  public String getNamespaceID() {
    return namespaceID;
  }

  public void setNamespaceID(final String namespaceID) {
    this.namespaceID = namespaceID;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(final @NotNull BufferedImage image) {
    this.image = (image.getWidth() == xPanes * 128) && (image.getHeight() == yPanes * 128) ? image
        : Utils.resize(image, xPanes * 128, yPanes * 128);
  }

  public int getXPanes() {
    return xPanes;
  }

  public int getYPanes() {
    return yPanes;
  }

  public @NotNull Map<RelativeLocation, MapView> getMapViews() {
    return mapViews;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(final String owner) {
    this.owner = owner;
  }

  public long getCreationTimestamp() {
    return creationTimestamp;
  }

  public void updateCreationTimestamp() {
    this.creationTimestamp = System.currentTimeMillis();
  }
}