package net.aerulion.sketchmap.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.aerulion.nucleus.api.mysql.MySQLUtils;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class DeleteSketchMapTask extends BukkitRunnable {

  private final CommandSender COMMANDSENDER;
  private final SketchMap SKETCHMAP;

  public DeleteSketchMapTask(CommandSender COMMANDSENDER, SketchMap SKETCHMAP) {
    this.COMMANDSENDER = COMMANDSENDER;
    this.SKETCHMAP = SKETCHMAP;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    try (Connection connection = MySQLUtils.getConnection()) {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "DELETE FROM `aerulion_sketchmap` WHERE `UUID` = ?");
      preparedStatement.setString(1, SKETCHMAP.getUuid());
        if (preparedStatement.executeUpdate() < 1) {
            throw new SQLException();
        }
      SKETCHMAP.unloadSketchMap();
      Main.LoadedSketchMaps.remove(SKETCHMAP.getNamespaceID());
      COMMANDSENDER.sendMessage(
          Messages.MESSAGE_SKETCHMAP_DELETED_1.get() + SKETCHMAP.getNamespaceID()
              + Messages.MESSAGE_SKETCHMAP_DELETED_2.getRaw());
      SoundUtils.playSound(COMMANDSENDER, SoundType.SUCCESS);
    } catch (SQLException exception) {
      COMMANDSENDER.sendMessage(Messages.ERROR_DELETING_SKETCHMAP.get());
      SoundUtils.playSound(COMMANDSENDER, SoundType.ERROR);
    }
  }
}