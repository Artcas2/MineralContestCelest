package fr.artcas2.mineralcontestcelest;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildVoiceState;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DiscordSRVImpl implements DiscordSRV {
    private static final github.scarsz.discordsrv.DiscordSRV discordSRV = github.scarsz.discordsrv.DiscordSRV.getPlugin();
    private static final Map<String, VoiceChannel> oldVoiceChannels = new HashMap<>();

    public boolean move(CommandSender sender, Player player, long voiceChannelId) {
        String userId = discordSRV.getAccountLinkManager().getDiscordId(player.getUniqueId());
        VoiceChannel voiceChannel = DiscordUtil.getJda().getVoiceChannelById(voiceChannelId);

        if (voiceChannel == null) {
            sender.sendMessage(ChatColor.RED + "The voice channel ID specified in the plugin's config is incorrect!");
            return false;
        }

        if (userId == null) {
            sender.sendMessage(ChatColor.RED + "This player doesn't have a linked Discord account!");
            return false;
        }

        Guild guild = voiceChannel.getGuild();
        Member member = guild.getMemberById(userId);

        if (member == null) {
            sender.sendMessage(ChatColor.RED + "This player is not on the Discord!");
            return false;
        }

        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null) {
            sender.sendMessage(ChatColor.RED + "Cannot move a Member with disabled CacheFlag.VOICE_STATE");
            return false;
        }

        VoiceChannel currentVoiceChannel = voiceState.getChannel();

        if (currentVoiceChannel == null) {
            sender.sendMessage(ChatColor.RED + "Cannot move a Member who's not currently in a voice channel.");
        } else {
            guild.moveVoiceMember(member, voiceChannel).queue();
            oldVoiceChannels.put(userId, currentVoiceChannel);
            sender.sendMessage(ChatColor.GREEN + "Member successfully moved to the voice channel.");
        }

        return true;
    }

    public static boolean moveBack(CommandSender sender, Player player) {
        String userId = discordSRV.getAccountLinkManager().getDiscordId(player.getUniqueId());

        if (userId == null) {
            sender.sendMessage(ChatColor.RED + "This player doesn't have a linked Discord account!");
            return false;
        }

        VoiceChannel voiceChannel = oldVoiceChannels.get(userId);

        if (voiceChannel == null) {
            return false;
        }

        Guild guild = voiceChannel.getGuild();
        Member member = guild.getMemberById(userId);

        if (member == null) {
            sender.sendMessage(ChatColor.RED + "This player is not on the Discord!");
            return false;
        }

        try {
            guild.moveVoiceMember(member, voiceChannel).queue();
        } catch (Exception e) {
            return true;
        }

        oldVoiceChannels.remove(userId);
        sender.sendMessage(ChatColor.GREEN + "Member successfully removed from the voice channel.");
        return true;
    }
}
