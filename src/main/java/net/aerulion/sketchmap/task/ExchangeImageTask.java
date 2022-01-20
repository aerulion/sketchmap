package net.aerulion.sketchmap.task;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ExchangeImageTask extends BukkitRunnable {

  private final CommandSender commandSender;
  private final SketchMap sketchMap;
  private final URL imageUrl;

  public ExchangeImageTask(final CommandSender commandSender, final SketchMap sketchMap,
      final URL imageUrl) {
    this.commandSender = commandSender;
    this.sketchMap = sketchMap;
    this.imageUrl = imageUrl;
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
    sketchMap.unloadSketchMap();
    sketchMap.setImage(image);
    sketchMap.updateCreationTimestamp();
    sketchMap.loadSketchMap();
    new SaveSketchMapTask(sketchMap, commandSender);
  }
}