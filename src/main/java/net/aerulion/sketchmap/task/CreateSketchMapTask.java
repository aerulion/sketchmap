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

public class CreateSketchMapTask extends BukkitRunnable {

  private final CommandSender COMMANDSENDER;
  private final String UUID;
  private final String NAMESPACE_ID;
  private final URL IMAGE_URL;
  private final int X_PANES;
  private final int Y_PANES;

  public CreateSketchMapTask(CommandSender COMMANDSENDER, String NAMESPACE_ID, URL IMAGE_URL,
      int X_PANES, int Y_PANES) {
    this.COMMANDSENDER = COMMANDSENDER;
    this.UUID = java.util.UUID.randomUUID().toString();
    this.NAMESPACE_ID = NAMESPACE_ID;
    this.IMAGE_URL = IMAGE_URL;
    this.X_PANES = X_PANES;
    this.Y_PANES = Y_PANES;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    BufferedImage image;
    try {
      final String ext = IMAGE_URL.getFile().substring(IMAGE_URL.getFile().length() - 3);
      if (!(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png"))) {
        COMMANDSENDER.sendMessage(Messages.ERROR_WRONG_IMAGE_FORMAT.get());
        SoundUtils.playSound(COMMANDSENDER, SoundType.ERROR);
        return;
      }
      image = ImageIO.read(IMAGE_URL);
    } catch (IOException | StringIndexOutOfBoundsException exception) {
      COMMANDSENDER.sendMessage(Messages.ERROR_FETCHING_IMAGE.get());
      SoundUtils.playSound(COMMANDSENDER, SoundType.ERROR);
      return;
    }
    Map<Integer, RelativeLocation> MAPPING = new HashMap<>();
    for (int x = 0; x < X_PANES; ++x) {
      for (int y = 0; y < Y_PANES; ++y) {
        MAPPING.put(Bukkit.createMap(Utils.getDefaultWorld()).getId(),
            RelativeLocation.fromString(x + ":" + y));
      }
    }
    Main.LoadedSketchMaps.put(NAMESPACE_ID,
        new SketchMap(UUID, NAMESPACE_ID, image, X_PANES, Y_PANES, MAPPING,
            COMMANDSENDER instanceof Player ? ((Player) COMMANDSENDER).getUniqueId().toString()
                : "CONSOLE", System.currentTimeMillis()));
    new SaveSketchMapTask(Main.LoadedSketchMaps.get(NAMESPACE_ID), COMMANDSENDER);
  }
}