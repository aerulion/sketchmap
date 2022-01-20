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

  private final CommandSender commandSender;
  private final SketchMap sketchMap;

  public DeleteSketchMapTask(final CommandSender commandSender, final SketchMap sketchMap) {
    this.commandSender = commandSender;
    this.sketchMap = sketchMap;
    this.runTaskAsynchronously(Main.plugin);
  }

  @Override
  public void run() {
    try (final Connection connection = MySQLUtils.getConnection()) {
      final PreparedStatement preparedStatement = connection.prepareStatement(
          "DELETE FROM `aerulion_sketchmap` WHERE `UUID` = ?");
      preparedStatement.setString(1, sketchMap.getUuid());
      if (preparedStatement.executeUpdate() < 1) {
        throw new SQLException();
      }
      sketchMap.unloadSketchMap();
      Main.LOADED_SKETCH_MAPS.remove(sketchMap.getNamespaceID());
      commandSender.sendMessage(
          Messages.MESSAGE_SKETCHMAP_DELETED_1.get() + sketchMap.getNamespaceID()
              + Messages.MESSAGE_SKETCHMAP_DELETED_2.getRaw());
      SoundUtils.playSound(commandSender, SoundType.SUCCESS);
    } catch (final SQLException exception) {
      commandSender.sendMessage(Messages.ERROR_DELETING_SKETCHMAP.get());
      SoundUtils.playSound(commandSender, SoundType.ERROR);
    }
  }
}