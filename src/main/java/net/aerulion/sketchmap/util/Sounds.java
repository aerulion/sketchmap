package net.aerulion.sketchmap.util;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A collection of all plugin sounds.
 */
public enum Sounds {
  COMMAND_ERROR(
      Sound.sound(org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, Source.MASTER, 0.5F, 2F)),
  COMMAND_SUCCESS(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Source.MASTER, 0.5F, 2F)),
  ;

  private final @NotNull Sound sound;

  @Contract(pure = true)
  Sounds(@NotNull final Sound sound) {
    this.sound = sound;
  }

  /**
   * Gets  the sound.
   *
   * @return the sound
   */
  @Contract(pure = true)
  public @NotNull Sound sound() {
    return sound;
  }

}