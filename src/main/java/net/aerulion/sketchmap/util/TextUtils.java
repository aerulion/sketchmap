package net.aerulion.sketchmap.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class TextUtils {

    public static void sendColoredConsoleMessage(final String msg) {
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(msg);
    }
}