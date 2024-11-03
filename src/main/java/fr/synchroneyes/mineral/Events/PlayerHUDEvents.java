package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCGameTickEvent;
import fr.synchroneyes.custom_events.MCPlayerBecomeRefereeEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinTeamEvent;
import fr.synchroneyes.custom_events.MCPlayerQuitRefereeEvent;
import fr.synchroneyes.custom_events.MCPlayerReconnectEvent;
import fr.synchroneyes.custom_events.MCPlayerWorldChangeEvent;
import fr.synchroneyes.custom_events.MCTeamScoreUpdated;
import fr.synchroneyes.custom_events.MCWorldLoadedEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardAPI;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardFields;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PlayerHUDEvents implements Listener {
    @EventHandler
    public void onPlayerJoinPlugin(MCPlayerJoinEvent event) {
        Game partie = event.getMcPlayer().getPartie();
        if (partie != null && partie.isGameStarted()) {
            if (event.getMcPlayer().getEquipe() != null) {
                PlayerHUDEvents.setPlayerInGameHUD(event.getMcPlayer());
                return;
            }
            Player joueur = event.getPlayer();
            int position = 16;
            ScoreboardAPI.createScoreboard(event.getPlayer(), false);
            ScoreboardAPI.clearScoreboard(event.getPlayer());
            ScoreboardAPI.addScoreboardText(joueur, ChatColor.GREEN + "v" + mineralcontest.plugin.getDescription().getVersion(), position--);
            ScoreboardAPI.addEmptyLine(joueur, position--);
            ScoreboardAPI.addScoreboardText(joueur, "Temps restant", position--);
            ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_VALUE, "", position--);
            ScoreboardAPI.addEmptyLine(joueur, position--);
            ScoreboardAPI.addScoreboardText(joueur, "Spectateur", position--);
            return;
        }
        ScoreboardAPI.createScoreboard(event.getPlayer(), false);
    }

    @EventHandler
    public void onPlayerReconnect(MCPlayerReconnectEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> PlayerHUDEvents.setPlayerInGameHUD(event.getPlayer()), 2L);
    }

    @EventHandler
    public void onGroupWorldLoaded(MCWorldLoadedEvent event) {
        for (Player membre_groupe : event.getGroupe().getPlayers()) {
            ScoreboardAPI.clearScoreboard(membre_groupe);
            int position = 16;
            ScoreboardAPI.addScoreboardText(membre_groupe, ChatColor.GOLD + Lang.hud_current_game_state.toString(), position--);
            ScoreboardAPI.registerNewObjective(membre_groupe, ScoreboardFields.SCOREBOARD_PLAYER_READY, ScoreboardAPI.prefix + ChatColor.RED + (Object)((Object)Lang.not_ready_tag), position--);
            ScoreboardAPI.addEmptyLine(membre_groupe, position--);
            ScoreboardAPI.registerNewObjective(membre_groupe, ScoreboardFields.SCOREBOARD_TEAMNAME_TEXT, ChatColor.GOLD + Lang.hud_team_text.toString(), position--);
            if (event.getGroupe().getGame().isReferee(membre_groupe)) {
                ScoreboardAPI.registerNewObjective(membre_groupe, ScoreboardFields.SCOREBOARD_TEAMNAME_VALUE, ScoreboardAPI.prefix + Lang.hud_referee_text.toString(), position--);
                continue;
            }
            ScoreboardAPI.registerNewObjective(membre_groupe, ScoreboardFields.SCOREBOARD_TEAMNAME_VALUE, ScoreboardAPI.prefix + Lang.hud_you_are_not_in_team.toString(), position--);
        }
    }

    @EventHandler
    public void onPlayerJoinTeam(MCPlayerJoinTeamEvent event) {
        Player player = event.getMcPlayer().getJoueur();
        Equipe equipe = event.getJoinedTeam();
        ChatColor teamColor = event.getJoinedTeam().getCouleur();
        ChatColor resetColor = ChatColor.RESET;
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TEAMNAME_TEXT, teamColor + Lang.hud_team_text.toString());
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TEAMNAME_VALUE, equipe.getNomEquipe());
        String score = equipe.getScore() >= 0 ? ChatColor.GREEN + "" + equipe.getScore() : ChatColor.RED + "" + equipe.getScore();
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TEAMSCORE_TEXT, teamColor + Lang.hud_score_text.toString());
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TEAMSCORE_VALUE, score);
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TIMELEFT_TEXT, teamColor + Lang.hud_timeleft_text.toString());
        String position = teamColor + "X: " + resetColor + player.getLocation().getBlockX() + " " + teamColor + "Y:" + resetColor + player.getLocation().getBlockY() + teamColor + " Z:" + resetColor + player.getLocation().getBlockZ();
        ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_PLAYERLOCATION_VALUE, position);
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            for (Player joueur : event.getGame().groupe.getPlayers()) {
                if (!event.getGame().isReferee(joueur)) {
                    PlayerHUDEvents.setPlayerInGameHUD(mineralcontest.plugin.getMCPlayer(joueur));
                    continue;
                }
                PlayerHUDEvents.setPlayerRefereeHUD(mineralcontest.plugin.getMCPlayer(joueur));
            }
        }, 1L);
    }

    @EventHandler
    public void onScoreUpdated(MCTeamScoreUpdated mcTeamScoreUpdated) {
        for (Player player : mcTeamScoreUpdated.getEquipe().getJoueurs()) {
            ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_TEAMSCORE_VALUE, mcTeamScoreUpdated.getEquipe().getFormattedScore(mcTeamScoreUpdated.getNewScore()));
        }
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            for (Player joueur : mcTeamScoreUpdated.getEquipe().getPartie().getReferees()) {
                PlayerHUDEvents.setPlayerRefereeHUD(mineralcontest.plugin.getMCPlayer(joueur));
                joueur.sendMessage("HUD arbitre updated");
            }
        }, 1L);
    }

    @EventHandler
    public void onGameTick(MCGameTickEvent event) {
        for (Player joueur : event.getGame().groupe.getPlayers()) {
            ScoreboardAPI.updateField(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_VALUE, event.getGame().getTempsRestant());
        }
    }

    @EventHandler
    public void onWorldChangeEvent(MCPlayerWorldChangeEvent event) {
        if (!mineralcontest.isAMineralContestWorld(event.getToWorld())) {
            event.getPlayer().setScoreboard(null);
            return;
        }
        if (mineralcontest.plugin.pluginWorld.equals((Object)event.getToWorld())) {
            ScoreboardAPI.createScoreboard(event.getPlayer(), true);
            return;
        }
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(event.getPlayer());
        if (mcPlayer.getPartie().isPlayerReady(event.getPlayer())) {
            PlayerHUDEvents.setPlayerRefereeHUD(mcPlayer);
        } else {
            PlayerHUDEvents.setPlayerInGameHUD(mcPlayer);
        }
    }

    @EventHandler
    public void onPlayerBecomesReferee(MCPlayerBecomeRefereeEvent event) {
        if (event.getPlayer().getPartie().isGameStarted()) {
            PlayerHUDEvents.setPlayerRefereeHUD(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuitReferee(MCPlayerQuitRefereeEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> PlayerHUDEvents.setPlayerInGameHUD(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onPlayerKitSelected(PlayerKitSelectedEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            Player joueur = event.getPlayer();
            ScoreboardAPI.updateField(joueur, ScoreboardFields.SCOREBOARD_KIT_VALUE, event.getSelectedKit().getNom());
        }, 1L);
    }

    public static void setPlayerInGameHUD(MCPlayer player) {
        KitAbstract playerKit;
        Game game = player.getPartie();
        Player joueur = player.getJoueur();
        ChatColor teamColor = ChatColor.GOLD;
        ChatColor resetColor = ChatColor.RESET;
        int position = 16;
        String playerTeamName = "Arbitre";
        boolean isPlayerReferee = game.isReferee(player.getJoueur());
        Equipe playerTeam = game.getPlayerTeam(player.getJoueur());
        if (!game.isGameStarted()) {
            ScoreboardAPI.createScoreboard(player.getJoueur(), true);
            return;
        }
        if (isPlayerReferee) {
            teamColor = ChatColor.GOLD;
        } else if (playerTeam != null) {
            teamColor = playerTeam.getCouleur();
            playerTeamName = playerTeam.getNomEquipe();
        }
        ScoreboardAPI.clearScoreboard(joueur);
        ScoreboardAPI.addScoreboardText(joueur, ChatColor.GREEN + "v" + mineralcontest.plugin.getDescription().getVersion(), position--);
        ScoreboardAPI.addEmptyLine(joueur, position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_TEXT, teamColor + Lang.hud_timeleft_text.toString(), position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_VALUE, game.getTempsRestant(), position--);
        ScoreboardAPI.addEmptyLine(joueur, position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TEAMNAME_TEXT, teamColor + Lang.hud_team_text.toString(), position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TEAMNAME_VALUE, playerTeamName, position--);
        ScoreboardAPI.addEmptyLine(joueur, position--);
        if (!isPlayerReferee) {
            String score = playerTeam.getFormattedScore();
            ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TEAMSCORE_TEXT, teamColor + Lang.hud_score_text.toString(), position--);
            ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TEAMSCORE_VALUE, score, position--);
            ScoreboardAPI.addEmptyLine(joueur, position--);
        }
        if (game.groupe.getKitManager().isKitsEnabled() && (playerKit = game.groupe.getKitManager().getPlayerKit(joueur)) != null) {
            ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_KIT_NAME, teamColor + "Kit", position--);
            ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_KIT_VALUE, playerKit.getNom(), position--);
            ScoreboardAPI.addEmptyLine(joueur, position--);
        }
        String position_joueur = teamColor + "X: " + resetColor + joueur.getLocation().getBlockX() + " " + teamColor + "Y: " + resetColor + joueur.getLocation().getBlockY() + teamColor + " Z: " + resetColor + joueur.getLocation().getBlockZ();
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_PLAYERLOCATION_VALUE, position_joueur, position--);
    }

    public static void setPlayerRefereeHUD(MCPlayer player) {
        Player joueur = player.getJoueur();
        int position = 16;
        ScoreboardAPI.clearScoreboard(player.getJoueur());
        ScoreboardAPI.addScoreboardText(joueur, ChatColor.GREEN + "v" + mineralcontest.plugin.getDescription().getVersion(), position--);
        ScoreboardAPI.addEmptyLine(joueur, position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_TEXT, ChatColor.GOLD + Lang.hud_timeleft_text.toString(), position--);
        ScoreboardAPI.registerNewObjective(joueur, ScoreboardFields.SCOREBOARD_TIMELEFT_VALUE, player.getPartie().getTempsRestant(), position--);
        for (House maisons : player.getPartie().getHouses()) {
            if (maisons.getTeam().getJoueurs().isEmpty()) continue;
            ScoreboardAPI.addEmptyLine(joueur, position--);
            String teamScore = maisons.getTeam().getFormattedScore();
            ScoreboardAPI.addScoreboardText(joueur, maisons.getTeam().getCouleur() + maisons.getTeam().getNomEquipe(), position--);
            ScoreboardAPI.addScoreboardText(joueur, teamScore, position--);
        }
    }
}

