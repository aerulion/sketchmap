package net.aerulion.sketchmap;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.aerulion.sketchmap.util.BaseFormat;
import net.aerulion.sketchmap.util.ImageRenderer;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMapUtils;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a single sketchmap.
 */
public class SketchMap {

  private final @NotNull BufferedImage image;
  private final int xPanes;
  private final int yPanes;
  private final BaseFormat format;
  private final @NotNull Map<RelativeLocation, MapView> mapviews;
  private final Map<Short, RelativeLocation> mapping;
  private String mapID;

  /**
   * Instantiates a new sketchmap.
   *
   * @param image   the image to display
   * @param mapID   the sketchmap id
   * @param xPanes  the number of x tiles
   * @param yPanes  the number of y tiles
   * @param format  the image format
   * @param mapping the tile mapping
   */
  public SketchMap(final @NotNull BufferedImage image, final String mapID, final int xPanes,
      final int yPanes, final BaseFormat format, final Map<Short, RelativeLocation> mapping) {
    super();
    if (!((image.getWidth() == xPanes * 128) && (image.getHeight() == yPanes * 128))) {
      this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
    } else {
      this.image = image;
    }
    this.mapID = mapID;
    this.xPanes = xPanes;
    this.yPanes = yPanes;
    this.format = format;
    this.mapping = mapping;
    this.mapviews = new HashMap<>();
    loadSketchMap();
  }

  /**
   * Loads the sketchmap.
   */
  public void loadSketchMap() {
    this.mapviews.clear();
    for (final @NotNull Entry<Short, RelativeLocation> entry : this.mapping.entrySet()) {
      final RelativeLocation relativelocation = entry.getValue();
      final @NotNull MapView mapview = SketchMapUtils.getMapView(entry.getKey());
      final BufferedImage subImage = this.image.getSubimage(relativelocation.x() * 128,
          relativelocation.y() * 128, 128, 128);
      for (final MapRenderer rend : mapview.getRenderers()) {
        mapview.removeRenderer(rend);
      }
      mapview.addRenderer(new ImageRenderer(subImage));
      this.mapviews.put(relativelocation, mapview);
    }
  }

  /**
   * Unloads the sketchmap.
   */
  public void unloadSketchMap() {
    for (final @NotNull MapView mapview : this.mapviews.values()) {
      for (final MapRenderer maprenderer : mapview.getRenderers()) {
        mapview.removeRenderer(maprenderer);
      }
    }
  }

  /**
   * Gets the namespaced id of this sketchmap.
   *
   * @return the id
   */
  public String getID() {
    return this.mapID;
  }

  /**
   * Sets the namespaced id of this sketchmap.
   *
   * @param id the new id
   */
  public void setID(final String id) {
    this.mapID = id;
  }

  /**
   * Gets the image displayed on this sketchmap.
   *
   * @return the image
   */
  public @NotNull BufferedImage getImage() {
    return this.image;
  }

  /**
   * Gets the number of x tiles.
   *
   * @return the number of x tiles
   */
  public int getXPanes() {
    return this.xPanes;
  }

  /**
   * Gets the number of y tiles.
   *
   * @return the number of y tiles
   */
  public int getYPanes() {
    return this.yPanes;
  }

  /**
   * Gets the map views mapped by their relative location.
   *
   * @return the map views
   */
  public @NotNull Map<RelativeLocation, MapView> getMapViews() {
    return this.mapviews;
  }

  /**
   * Gets the image base format.
   *
   * @return the base format
   */
  public BaseFormat getBaseFormat() {
    return this.format;
  }

}