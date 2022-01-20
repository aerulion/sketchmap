package net.aerulion.sketchmap.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.aerulion.nucleus.api.base64.Base64Utils;
import net.aerulion.nucleus.api.mysql.MySQLUtils;
import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveSketchMapTask extends BukkitRunnable {

  private final SketchMap sketchMap;
  private final CommandSender commandSender;

  private final long startMillis;
  private boolean isSaving = false;
  private boolean saved = false;

  public SaveSketchMapTask(final SketchMap sketchMap, final CommandSender commandSender) {
    this.sketchMap = sketchMap;
    this.commandSender = commandSender;
    this.startMillis = System.currentTimeMillis();
    this.runTaskTimerAsynchronously(Main.plugin, 0L, 100L);
  }

  @Override
  public void run() {
    if (!isSaving) {
      isSaving = true;
      try (final Connection connection = MySQLUtils.getConnection()) {
        final PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO `aerulion_sketchmap` (`UUID`, `NAMESPACE_ID`, `IMAGE`, `X_PANES`, `Y_PANES`, `MAPPING`, `OWNER`, `CREATION_TIMESTAMP`) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `NAMESPACE_ID` = ?, `IMAGE` = ?, `X_PANES` = ?, `Y_PANES` = ?, `MAPPING` = ?, `OWNER` = ?, `CREATION_TIMESTAMP` = ?");
        preparedStatement.setString(1, sketchMap.getUuid());
        preparedStatement.setString(2, sketchMap.getNamespaceID());
        preparedStatement.setString(3,
            Base64Utils.encodeBufferedImage(sketchMap.getImage(), "PNG"));
        preparedStatement.setInt(4, sketchMap.getXPanes());
        preparedStatement.setInt(5, sketchMap.getYPanes());
        preparedStatement.setString(6, Utils.encodeMapping(sketchMap.getMapViews()));
        preparedStatement.setString(7, sketchMap.getOwner());
        preparedStatement.setLong(8, sketchMap.getCreationTimestamp());
        preparedStatement.setString(9, sketchMap.getNamespaceID());
        preparedStatement.setString(10,
            Base64Utils.encodeBufferedImage(sketchMap.getImage(), "PNG"));
        preparedStatement.setInt(11, sketchMap.getXPanes());
        preparedStatement.setInt(12, sketchMap.getYPanes());
        preparedStatement.setString(13, Utils.encodeMapping(sketchMap.getMapViews()));
        preparedStatement.setString(14, sketchMap.getOwner());
        preparedStatement.setLong(15, sketchMap.getCreationTimestamp());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        commandSender.sendMessage(
            Messages.MESSAGE_SKETCHMAP_SAVED_1.get() + sketchMap.getNamespaceID()
                + Messages.MESSAGE_SKETCHMAP_SAVED_2.getRaw() + (System.currentTimeMillis()
                - startMillis) + "ms]");
        SoundUtils.playSound(commandSender, SoundType.SUCCESS);
        saved = true;
      } catch (final SQLException | IOException exception) {
        commandSender.sendMessage(Messages.ERROR_SAVING_SKETCHMAP.get());
        SoundUtils.playSound(commandSender, SoundType.ERROR);
        isSaving = false;
      }
    }
    if (saved) {
      this.cancel();
    }
  }
}