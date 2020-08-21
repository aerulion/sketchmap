package net.aerulion.sketchmap.task;

import net.aerulion.nucleus.api.sound.SoundType;
import net.aerulion.nucleus.api.sound.SoundUtils;
import net.aerulion.sketchmap.Main;
import net.aerulion.sketchmap.util.Messages;
import net.aerulion.sketchmap.util.SketchMap;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ExchangeImageTask extends BukkitRunnable {

    private final CommandSender COMMANDSENDER;
    private final SketchMap SKETCHMAP;
    private final URL IMAGE_URL;

    public ExchangeImageTask(CommandSender COMMANDSENDER, SketchMap SKETCHMAP, URL IMAGE_URL) {
        this.COMMANDSENDER = COMMANDSENDER;
        this.SKETCHMAP = SKETCHMAP;
        this.IMAGE_URL = IMAGE_URL;
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
        SKETCHMAP.unloadSketchMap();
        SKETCHMAP.setImage(image);
        SKETCHMAP.updateCreationTimestamp();
        SKETCHMAP.loadSketchMap();
        new SaveSketchMapTask(SKETCHMAP, COMMANDSENDER);
    }
}