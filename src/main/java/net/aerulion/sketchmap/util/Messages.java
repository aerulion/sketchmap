package net.aerulion.sketchmap.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A collection of all plugin messages.
 */
public enum Messages implements ComponentLike {
  CONSOLE_DISABLING("Deaktiviere Plugin..."),
  CONSOLE_ENABLING("Aktiviere Plugin..."),
  CONSOLE_ERROR_LOADING_FILE("<red>Die Datei %name% konnte nicht geladen werden!"),
  CONSOLE_LOADING_SKETCHMAPS("SketchMaps werden geladen..."),
  CONSOLE_PLUGIN_DISABLED("Das Plugin wurde deaktiviert."),
  CONSOLE_PLUGIN_ENABLED("Das Plugin wurde aktiviert."),
  CONSOLE_SKETCHMAPS_LOADED("%amount% SketchMaps wurden geladen. Dauerte %time%ms"),
  DOWNLOADED_IMAGE("<gray>Bild wurde heruntergeladen. <dark_gray>[%time%ms]"),
  DOWNLOADING_IMAGE("<gray>Bild wird heruntergeladen..."),
  ERROR_ID_ALPHA_NUMERIC("<red>Fehler: Map ID muss alphanumerisch sein."),
  ERROR_ID_DUPLICATE("<red>Fehler: Diese SketchMap ID existiert bereits."),
  ERROR_ID_LENGTH("<red>Fehler: Map ID darf zwischen 3-32 Zeichen lang sein."),
  ERROR_MALFORMED_URL("<red>Fehler: Bild konnte nicht geladen werden. URL erscheint ungültig."),
  ERROR_MISSING_ARGS("<red>Zu wenige Argumente, für mehr Infos nutze /sketchmap help."),
  ERROR_NEGATIVE_SCALING_ARGS(
      "<red>Fehler: Bild konnte nicht skaliert werden. Negative Argumente angegeben."),
  ERROR_NO_IMAGE_URL(
      "<red>Fehler: Das Bild konnte an der angegebenen URL nicht gefunden werden, wenn du denkst das wäre ein Fehler, versuche das Bild auf imgur.com hochzuladen."),
  ERROR_NO_PERMISSIONS("<red>Du hast nicht die Rechte, diesen Befehl zu nutzen."),
  ERROR_ONLY_PLAYER("<red>Fehler: Dieser Befehl kann nur als Spieler:in ausgeführt werden."),
  ERROR_PLAYER_NOT_ONLINE("<red>Fehler: Die Spieler:in '%name%' ist nicht online!"),
  ERROR_SKETCHMAP_ALREADY_EXISTS("<red>Fehler: Die SketchMap '%name%' existiert bereits."),
  ERROR_SKETCHMAP_DOES_NOT_EXIST("<red>Fehler: Die SketchMap '%name%' existiert nicht."),
  ERROR_WRONG_ARGS("<red>Fehler: Falsche Argumente."),
  ERROR_WRONG_FORMAT("<red>Fehler: Bilder werden nur im Format .JPG und .PNG unterstützt."),
  ERROR_WRONG_SCALING_ARGS(
      "<red>Fehler: Bild konnte nicht skaliert werden. Ungültige Argumente angegeben."),
  LOADED_SKETCHMAPS("<gray>Folgende SketchMaps sind geladen:"),
  LOADED_SKETCHMAPS_COUNT("<gray>Insgesamt sind %count% SketchMaps geladen."),
  NO_LOADED_SKETCHMAPS("<gray>Es sind keine SketchMaps geladen."),
  PROCESSED_IMAGE("<gray>Bild wurde verarbeitet. <dark_gray>[%time%ms]"),
  PROCESSING_IMAGE("<gray>Bild wird verarbeitet..."),
  SKETCHMAP_CREATED("<gray>Die SketchMap '%name%' wurde erfolgreich erstellt."),
  SKETCHMAP_DELETED("<gray>Die SketchMap '%name%' wurde erfolgreich gelöscht."),
  SKETCHMAP_RENAMED("<gray>Die SketchMap '%name%' wurde in '%newName%' umbenannt."),
  ;

  private final @NotNull Component message;

  Messages(final @NotNull String message) {
    this.message = MiniMessage.miniMessage().deserialize("<yellow>[<green>✎<yellow>] " + message);
  }

  @Override
  @Contract(pure = true)
  public @NotNull Component asComponent() {
    return message;
  }

}