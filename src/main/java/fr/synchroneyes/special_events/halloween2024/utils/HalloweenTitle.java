package fr.synchroneyes.special_events.halloween2024.utils;

import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HalloweenTitle {
    public static void sendTitle(Player player, String title, String content, int fadeInDuration, int stayDuration, int fadeOutDuration) {
        player.sendTitle(HalloweenTitle.format(title), content, fadeInDuration * 20, stayDuration * 20, fadeOutDuration * 20);
    }

    public static void sendTitle(Player player, String title, String content, int fadeInDuration, int stayDuration, int fadeOutDuration, boolean sendTextMessage) {
        player.sendTitle(HalloweenTitle.format(title), content, fadeInDuration * 20, stayDuration * 20, fadeOutDuration * 20);
        if (sendTextMessage) {
            player.sendMessage(mineralcontest.prefixPrive + "[" + HalloweenTitle.format(title) + ChatColor.RESET + "] " + content);
        }
    }

    private static String format(String message) {
        int messageLength = message.length();
        StringBuilder newMessageBuilder = new StringBuilder();
        newMessageBuilder.append(ChatColor.GOLD + "");
        int middleString = messageLength / 2;
        for (int i = 0; i < messageLength; ++i) {
            newMessageBuilder.append(message.charAt(i));
            if (middleString != i) continue;
            newMessageBuilder.append(ChatColor.BLUE + "");
        }
        return newMessageBuilder.toString();
    }
}

