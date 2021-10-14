package net.aerulion.sketchmap.util;

public enum Messages {

  CONSOLE_DISABLING("§eDeaktiviere Plugin..."),
  CONSOLE_ENABLING("§eAktiviere Plugin..."),
  CONSOLE_ERROR_LOADING_SKETCHMAP("§cFehler: Die folgende SketchMap konnte nicht geladen werden: "),
  CONSOLE_ERROR_LOADING_SKETCHMAPS("§cFehler: Die SketchMaps konnten nicht geladen werden."),
  CONSOLE_LOADING_SKETCHMAPS("§eSketchMaps werden geladen..."),
  CONSOLE_PLUGIN_DISABLED("§eDas Plugin wurde deaktiviert."),
  CONSOLE_PLUGIN_ENABLED("§eDas Plugin wurde aktiviert."),
  CONSOLE_SKETCHMAPS_LOADED("§e SketchMaps wurden geladen. Dauerte §a"),

  ERROR_DELETING_SKETCHMAP("§cFehler: Die SketchMap konnte nicht gelöscht werden."),
  ERROR_FETCHING_IMAGE(
      "§cFehler: Das Bild konnte an der angegebenen URL nicht gefunden werden. Wenn du denkst dies wäre ein Fehler, versuche das Bild auf imgur.com hochzuladen."),
  ERROR_MALFORMED_URL(
      "§cFehler: Das Bild konnte nicht geladen werden. Die URL erscheint ungültig."),
  ERROR_NAMESPACE_ID_ILLEGAL_CHARACTERS(
      "§cFehler: Die Namespace ID darf nur die Zahlen von 0-9, die Kleinbuchstaben von a-z und ein / oder _ enthalten. Außerdem darf die ID nicht mit einem / oder _ beginnen oder enden."),
  ERROR_NAMESPACE_ID_ALREADY_TAKEN("§cFehler: Diese Namespace ID ist bereits vergeben."),
  ERROR_NAMESPACE_ID_LENGTH("§cFehler: Die Namespace ID muss zwischen 3-128 Zeichen lang sein."),
  ERROR_NAMESPACE_ID_NOT_FOUND(
      "§cFehler: Es existiert keine SketchMap mit folgender Namespace ID: "),
  ERROR_NAMESPACE_ID_NO_UPPERCASE(
      "§cFehler: Die Namespace ID darf keine Großbuchstaben enthalten."),
  ERROR_NO_PERMISSION("§cFehler: Du hast keine Rechte diesen Befehl zu nutzen."),
  ERROR_NO_PLAYER("§cFehler: Dieser Befehl kann nur als Spieler ausgeführt werden."),
  ERROR_OFFLINE_PLAYER_NOT_FOUND(
      "§cFehler: Der angegebene Spieler war noch nie auf diesem Server."),
  ERROR_PLAYER_NOT_FOUND("§cFehler: Der angegebene Spieler konnte nicht gefunden werden."),
  ERROR_SAVING_SKETCHMAP(
      "§cFehler: Die SketchMap konnte nicht gespeichert werden. Erneuter Versuch in ein paar Sekunden..."),
  ERROR_SCALE_ARGUMENTS(
      "§cFehler: Das Bild konnte nicht skaliert werden. Ungültige Argumente angegeben."),
  ERROR_WRONG_ARGUMENTS(
      "§cFehler: Falsche Argumente. Nutze '/sketchmap help' um Hilfe zu erhalten."),
  ERROR_WRONG_IMAGE_FORMAT("§cFehler: Bilder werden nur im Format .JPG und .PNG unterstützt."),

  MESSAGE_SKETCHMAP_DELETED_1("§eDie SketchMap §a§l"),
  MESSAGE_SKETCHMAP_DELETED_2("§e wurde erfolgreich gelöscht."),
  MESSAGE_SKETCHMAP_SAVED_1("§eDie SketchMap §a§l"),
  MESSAGE_SKETCHMAP_SAVED_2("§e wurde erfolgreich gespeichert. §8["),

  PREFIX("§e[§a§lSketchMap§e]§7 ");

  private final String message;

  Messages(String message) {
    this.message = message;
  }

  public String get() {
    return "§e[§a§lSketchMap§e]§7 " + message;
  }

  public String getRaw() {
    return message;
  }
}