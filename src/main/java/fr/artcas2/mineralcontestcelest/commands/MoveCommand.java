package fr.artcas2.mineralcontestcelest.commands;

import fr.synchroneyes.mineral.mineralcontest;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildVoiceState;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.VoiceChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveCommand implements TabCompleter, CommandExecutor {
    private final Map<Member, VoiceChannel> oldVoiceChannels = new HashMap<>();
    private final DiscordSRV discordSRV = DiscordSRV.getPlugin();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), completions);
        }

        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        YamlConfiguration discordConfig = mineralcontest.plugin.getDiscordConfig();

        if (!discordConfig.getBoolean("move.enabled")) {
            sender.sendMessage(ChatColor.RED + "This command is not enabled in the plugin's config!");
            return false;
        }

        VoiceChannel voiceChannel = DiscordUtil.getJda().getVoiceChannelById(discordConfig.getLong("move.general-voice-channel"));

        if (voiceChannel == null) {
            sender.sendMessage(ChatColor.RED + "The voice channel ID specified in the plugin's config is incorrect!");
            return false;
        }

        Guild guild = voiceChannel.getGuild();

        if (args.length == 0) {
            oldVoiceChannels.forEach((member, oldVoiceChannel) -> {
                try {
                    guild.moveVoiceMember(member, oldVoiceChannels.get(member)).queue();
                } catch (Exception ignored) {}
            });

            oldVoiceChannels.clear();
            sender.sendMessage(ChatColor.GREEN + "All members successfully removed from the voice channel.");
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayerExact(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "This player doesn't have a linked Discord account!");
                return false;
            }

            String discordId = discordSRV.getAccountLinkManager().getDiscordId(player.getUniqueId());

            if (discordId == null) {
                sender.sendMessage(ChatColor.RED + "This player doesn't have a linked Discord account!");
                return false;
            }

            Member member = guild.getMemberById(discordId);
            GuildVoiceState voiceState = member.getVoiceState();

            if (oldVoiceChannels.containsKey(member)) {
                try {
                    guild.moveVoiceMember(member, oldVoiceChannels.get(member)).queue();
                } catch (Exception ignored) {}

                oldVoiceChannels.remove(member);
                sender.sendMessage(ChatColor.GREEN + "Member successfully removed from the voice channel.");
                return true;
            }

            if (voiceState == null) {
                sender.sendMessage(ChatColor.RED + "Cannot move a Member with disabled CacheFlag.VOICE_STATE");
                return false;
            }

            VoiceChannel currentVoiceChannel = voiceState.getChannel();

            if (currentVoiceChannel == null) {
                sender.sendMessage(ChatColor.RED + "Cannot move a Member who's not currently in a voice channel.");
            } else {
                guild.moveVoiceMember(member, voiceChannel).queue();
                oldVoiceChannels.put(member, currentVoiceChannel);
                sender.sendMessage(ChatColor.GREEN + "Member successfully moved to the voice channel.");
            }
        } else {
            return false;
        }

        return true;
    }
}
