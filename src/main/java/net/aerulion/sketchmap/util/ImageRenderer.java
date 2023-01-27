package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

/**
 * A basic map renderer for rendering buffered images.
 */
public class ImageRenderer extends MapRenderer {

  private final BufferedImage bufferedImage;
  private Boolean imageRendered;

  /**
   * Instantiates a new image renderer.
   *
   * @param bufferedImage the buffered image to render
   */
  public ImageRenderer(final BufferedImage bufferedImage) {
    super();
    this.bufferedImage = bufferedImage;
    this.imageRendered = false;
  }

  @Override
  public void render(final @NotNull MapView map, final @NotNull MapCanvas canvas,
      final @NotNull Player player) {
    if (this.imageRendered) {
      return;
    }
    canvas.drawImage(0, 0, this.bufferedImage);
    this.imageRendered = true;
  }

}