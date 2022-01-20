package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class ImageRenderer extends MapRenderer {

  private final BufferedImage bufferedImage;
  private boolean imageRendered;

  public ImageRenderer(final BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    this.imageRendered = false;
  }

  public void render(final @NotNull MapView map, final @NotNull MapCanvas canvas,
      final @NotNull Player player) {
    if (this.imageRendered) {
      return;
    }
    canvas.drawImage(0, 0, this.bufferedImage);
    this.imageRendered = true;
  }
}