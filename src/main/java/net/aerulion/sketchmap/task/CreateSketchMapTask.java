package net.aerulion.sketchmap.task;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.RelativeLocation;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class CreateSketchMapTask extends BukkitRunnable {

  private final CommandSender commandSender;
  private final String uuid;
  private final String namespaceId;
  private final URL imageUrl;
  private final int xPanes;
  private final int yPanes;

  public CreateSketchMapTask(final CommandSender commandSender, final String namespaceId,
      final URL imageUrl, final int xPanes, final int yPanes) {
    this.commandSender = commandSender;
    this.uuid = java.util.UUID.randomUUID().toString();
    this.namespaceId = namespaceId;
    this.imageUrl = imageUrl;
    this.xPanes = xPanes;
    this.yPanes = yPanes;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    final BufferedImage image;
    try {
      final @NotNull String ext = imageUrl.getFile().substring(imageUrl.getFile().length() - 3);
      if (!(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png"))) {
        commandSender.sendMessage(Messages.ERROR_WRONG_IMAGE_FORMAT.get());
        SoundUtils.playSound(commandSender, SoundType.ERROR);
        return;
      }
      image = ImageIO.read(imageUrl);
    } catch (final IOException | StringIndexOutOfBoundsException exception) {
      commandSender.sendMessage(Messages.ERROR_FETCHING_IMAGE.get());
      SoundUtils.playSound(commandSender, SoundType.ERROR);
      return;
    }
    final @NotNull Map<Integer, RelativeLocation> mapping = new HashMap<>();
    for (int x = 0; x < xPanes; ++x) {
      for (int y = 0; y < yPanes; ++y) {
        mapping.put(Bukkit.createMap(Utils.getDefaultWorld()).getId(),
            RelativeLocation.fromString(x + ":" + y));
      }
    }
    Main.LOADED_SKETCH_MAPS.put(namespaceId,
        new SketchMap(uuid, namespaceId, image, xPanes, yPanes, mapping,
            commandSender instanceof Player player ? player.getUniqueId().toString() : "CONSOLE",
            System.currentTimeMillis()));
    new SaveSketchMapTask(Main.LOADED_SKETCH_MAPS.get(namespaceId), commandSender);
  }
}