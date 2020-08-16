package net.aerulion.sketchmap.task;

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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveSketchMapTask extends BukkitRunnable {

    private final SketchMap SKETCHMAP;
    private final CommandSender COMMANDSENDER;

    private final long START_MILLIS;
    private boolean isSaving = false;
    private boolean saved = false;

    public SaveSketchMapTask(SketchMap SKETCHMAP, CommandSender COMMANDSENDER) {
        this.SKETCHMAP = SKETCHMAP;
        this.COMMANDSENDER = COMMANDSENDER;
        this.START_MILLIS = System.currentTimeMillis();
        this.runTaskTimerAsynchronously(Main.plugin, 0L, 100L);
    }

    @Override
    public void run() {
        if (!isSaving) {
            isSaving = true;
            try (Connection connection = MySQLUtils.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `aerulion_sketchmap` (`UUID`, `NAMESPACE_ID`, `IMAGE`, `X_PANES`, `Y_PANES`, `MAPPING`, `OWNER`) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `NAMESPACE_ID` = ?, `IMAGE` = ?, `X_PANES` = ?, `Y_PANES` = ?, `MAPPING` = ?, `OWNER` = ?");
                preparedStatement.setString(1, SKETCHMAP.getUUID());
                preparedStatement.setString(2, SKETCHMAP.getNamespaceID());
                preparedStatement.setString(3, Base64Utils.encodeBufferedImage(SKETCHMAP.getImage(), "PNG"));
                preparedStatement.setInt(4, SKETCHMAP.getXPanes());
                preparedStatement.setInt(5, SKETCHMAP.getYPanes());
                preparedStatement.setString(6, Utils.encodeMapping(SKETCHMAP.getMapViews()));
                preparedStatement.setString(7, SKETCHMAP.getOwner());
                preparedStatement.setString(8, SKETCHMAP.getNamespaceID());
                preparedStatement.setString(9, Base64Utils.encodeBufferedImage(SKETCHMAP.getImage(), "PNG"));
                preparedStatement.setInt(10, SKETCHMAP.getXPanes());
                preparedStatement.setInt(11, SKETCHMAP.getYPanes());
                preparedStatement.setString(12, Utils.encodeMapping(SKETCHMAP.getMapViews()));
                preparedStatement.setString(13, SKETCHMAP.getOwner());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                COMMANDSENDER.sendMessage(Messages.MESSAGE_SKETCHMAP_SAVED_1.get() + SKETCHMAP.getNamespaceID() + Messages.MESSAGE_SKETCHMAP_SAVED_2.getRaw() + (System.currentTimeMillis() - START_MILLIS) + "ms]");
                SoundUtils.playSound(COMMANDSENDER, SoundType.SUCCESS);
                saved = true;
            } catch (SQLException | IOException exception) {
                COMMANDSENDER.sendMessage(Messages.ERROR_SAVING_SKETCHMAP.get());
                SoundUtils.playSound(COMMANDSENDER, SoundType.ERROR);
                isSaving = false;
            }
        }
        if (saved)
            this.cancel();
    }
}