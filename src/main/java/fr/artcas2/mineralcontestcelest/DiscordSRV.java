package fr.artcas2.mineralcontestcelest;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface DiscordSRV {
    public boolean move(CommandSender sender, Player player, long voiceChannelId);
}
