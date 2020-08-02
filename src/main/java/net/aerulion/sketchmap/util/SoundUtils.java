package net.aerulion.sketchmap.util;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void playCommandSound(CommandSender sender, CommandSound commandsound) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            switch (commandsound) {
                case SUCCESS:
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2F);
                    break;
                case ERROR:
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5F, 2F);
                    break;
                default:
                    break;
            }
        }
    }
}