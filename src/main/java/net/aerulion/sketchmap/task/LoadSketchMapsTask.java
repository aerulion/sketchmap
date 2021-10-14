package net.aerulion.sketchmap.task;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.aerulion.nucleus.api.base64.Base64Utils;
import net.aerulion.nucleus.api.console.ConsoleUtils;
import net.aerulion.nucleus.api.mysql.MySQLUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import net.aerulion.sketchmap.util.Utils;
import org.bukkit.scheduler.BukkitRunnable;

public class LoadSketchMapsTask extends BukkitRunnable {

  public LoadSketchMapsTask() {
    this.runTask(Main.plugin);
  }

  @Override
  public void run() {
    long startMillis = System.currentTimeMillis();
    if (!Main.LoadedSketchMaps.isEmpty()) {
      for (SketchMap sketchMap : Main.LoadedSketchMaps.values()) {
        sketchMap.unloadSketchMap();
      }
      Main.LoadedSketchMaps.clear();
    }
    ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_LOADING_SKETCHMAPS.get());
    try (Connection connection = MySQLUtils.getConnection()) {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT * FROM `aerulion_sketchmap`");
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet != null) {
        while (resultSet.next()) {
          try {
            Main.LoadedSketchMaps.put(resultSet.getString("NAMESPACE_ID"),
                new SketchMap(resultSet.getString("UUID"), resultSet.getString("NAMESPACE_ID"),
                    Base64Utils.decodeBufferedImage(resultSet.getString("IMAGE")),
                    resultSet.getInt("X_PANES"), resultSet.getInt("Y_PANES"),
                    Utils.decodeMapping(resultSet.getString("MAPPING")),
                    resultSet.getString("OWNER"), resultSet.getLong("CREATION_TIMESTAMP")));
          } catch (IOException exception) {
            ConsoleUtils.sendColoredConsoleMessage(
                Messages.CONSOLE_ERROR_LOADING_SKETCHMAP.get() + resultSet.getString(
                    "NAMESPACE_ID"));
          }
        }
        resultSet.close();
      }
      preparedStatement.close();
      ConsoleUtils.sendColoredConsoleMessage(
          Messages.PREFIX.getRaw() + "§a" + Main.LoadedSketchMaps.size()
              + Messages.CONSOLE_SKETCHMAPS_LOADED.getRaw() + (System.currentTimeMillis()
              - startMillis) + "ms");
    } catch (SQLException exception) {
      ConsoleUtils.sendColoredConsoleMessage(Messages.CONSOLE_ERROR_LOADING_SKETCHMAPS.get());
    }
  }
}