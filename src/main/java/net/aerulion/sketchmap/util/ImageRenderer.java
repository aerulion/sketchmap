package net.aerulion.sketchmap.util;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class ImageRenderer extends MapRenderer {
    private final BufferedImage bufferedImage;
    private Boolean imageRendered;

    public ImageRenderer(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.imageRendered = false;
    }

    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (this.imageRendered) {
            return;
        }
        mapCanvas.drawImage(0, 0, this.bufferedImage);
        this.imageRendered = true;
    }
}