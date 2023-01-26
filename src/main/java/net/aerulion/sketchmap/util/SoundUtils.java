package net.aerulion.sketchmap.util;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundUtils {

  public static void playCommandSound(final CommandSender commandSender, final CommandSound commandSound) {
    if (commandSender instanceof Player) {
      final Player player = (Player) commandSender;
      if (commandSound.equals(CommandSound.SUCCESS)) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2F);
      }
      if (commandSound.equals(CommandSound.ERROR)) {
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.5F, 2F);
      }
    }
  }
}