package fr.synchroneyes.mineral.Scoreboard.newapi;

import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardFields;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardAPI {
    private static ArrayList<Player> playersWithScoreboard;
    public static String prefix;

    public static void createScoreboard(Player player, boolean force) {
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
        if (playersWithScoreboard == null) {
            playersWithScoreboard = new ArrayList();
        }
        if (ScoreboardAPI.doesPlayerHaveHUD(player) && !force) {
            return;
        }
        ScoreboardAPI.clearScoreboard(player);
        Scoreboard scoreboard = player.getScoreboard();
        int position = 16;
        ScoreboardAPI.addScoreboardText(player, ChatColor.GOLD + "Groupe", position--);
        ScoreboardAPI.registerNewObjective(player, ScoreboardFields.SCOREBOARD_GROUP_STATE, prefix + mcPlayer.getGroupe().getEtatPartie().getNom(), position--);
        ScoreboardAPI.addEmptyLine(player, position--);
        ScoreboardAPI.addScoreboardText(player, ChatColor.GOLD + "Joueurs", position--);
        ScoreboardAPI.registerNewObjective(player, ScoreboardFields.SCOREBOARD_PLAYER_COUNT, prefix + mcPlayer.getGroupe().getPlayerCount(), position--);
        ScoreboardAPI.addEmptyLine(player, position--);
        ScoreboardAPI.addScoreboardText(player, ChatColor.GOLD + "Etat", position--);
        ScoreboardAPI.registerNewObjective(player, ScoreboardFields.SCOREBOARD_PLAYER_READY, prefix, position--);
        if (mcPlayer.getGroupe().getAdmins().size() > 0) {
            Player admin = mcPlayer.getGroupe().getAdmins().getFirst();
            ScoreboardAPI.addEmptyLine(player, position--);
            ScoreboardAPI.addScoreboardText(player, ChatColor.RED + "Admin", position--);
            ScoreboardAPI.registerNewObjective(player, ScoreboardFields.SCOREBOARD_ADMINS, prefix + admin.getDisplayName(), position--);
        }
        player.setScoreboard(scoreboard);
        playersWithScoreboard.add(player);
        if (mcPlayer.getGroupe().getGame().isPlayerReady(player)) {
            ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_PLAYER_READY, prefix + ChatColor.GREEN + Lang.ready_tag.toString());
        } else {
            ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_PLAYER_READY, prefix + ChatColor.RED + Lang.not_ready_tag.toString());
        }
    }

    public static void updateField(Player player, ScoreboardFields cle, String valeur) {
        if (playersWithScoreboard == null) {
            playersWithScoreboard = new ArrayList();
        }
        if (!ScoreboardAPI.doesPlayerHaveHUD(player)) {
            return;
        }
        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.getObjective(ScoreboardFields.SCOREBOARD_TITLE.toString());
        Team team = scoreboard.getTeam(cle.toString());
        if (team == null) {
            return;
        }
        team.setPrefix(valeur);
    }

    public static void clearScoreboard(Player joueur) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        String scoreboardTitle = Lang.title.toString();
        Objective objective = scoreboard.registerNewObjective(ScoreboardFields.SCOREBOARD_TITLE.toString(), "dummy", scoreboardTitle);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        joueur.setScoreboard(scoreboard);
    }

    public static void registerNewObjective(Player player, ScoreboardFields fields, String value, int position) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(ScoreboardFields.SCOREBOARD_TITLE.toString());
        Team equipe = objective.getScoreboard().registerNewTeam(fields.toString());
        equipe.addEntry(fields.getUniqueColor());
        equipe.setPrefix(value);
        objective.getScore(fields.getUniqueColor()).setScore(position);
        player.setScoreboard(scoreboard);
    }

    public static void addScoreboardText(Player player, String text, int position) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(ScoreboardFields.SCOREBOARD_TITLE.toString());
        Score score = objective.getScore(text);
        score.setScore(position);
        player.setScoreboard(scoreboard);
    }

    public static void addEmptyLine(Player player, int position) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(ScoreboardFields.SCOREBOARD_TITLE.toString());
        String space = "";
        for (int i = 0; i < position; ++i) {
            space = space + " ";
        }
        Score score = objective.getScore(space);
        score.setScore(position);
        player.setScoreboard(scoreboard);
    }

    private static boolean doesPlayerHaveHUD(Player player) {
        for (Player player1 : playersWithScoreboard) {
            if (!player1.getUniqueId().equals(player.getUniqueId())) continue;
            return true;
        }
        return false;
    }

    static {
        prefix = "\u00bb ";
    }
}

