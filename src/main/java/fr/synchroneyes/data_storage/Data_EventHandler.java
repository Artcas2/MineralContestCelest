package fr.synchroneyes.data_storage;

import fr.synchroneyes.custom_events.MCGameEndEvent;
import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.custom_events.MCPlayerOpenChestEvent;
import fr.synchroneyes.custom_events.MCShopItemPurchaseEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreBoss;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreParachute;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Data_EventHandler implements Listener {
    @EventHandler
    public void onPlayerJoin(MCPlayerJoinEvent event) {
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        ResultSet donnee_joueur = null;
        try {
            PreparedStatement select_mineral_player = connection.prepareStatement("SELECT * FROM mineral_players WHERE uuid = ?");
            select_mineral_player.setString(1, event.getPlayer().getUniqueId() + "");
            select_mineral_player.execute();
            donnee_joueur = select_mineral_player.getResultSet();
            if (donnee_joueur.next()) {
                event.getMcPlayer().setDatabasePlayerId(donnee_joueur.getInt("id"));
                return;
            }
            Player joueur = event.getPlayer();
            PreparedStatement insert_mineral_players = connection.prepareStatement("INSERT INTO mineral_players SET uuid = ?, onlinemode = '1', name = ?");
            insert_mineral_players.setString(1, joueur.getUniqueId() + "");
            insert_mineral_players.setString(2, joueur.getDisplayName());
            insert_mineral_players.executeUpdate();
            PreparedStatement select_mineral_playeruid = connection.prepareStatement("SELECT * FROM mineral_players WHERE uuid = ?");
            select_mineral_playeruid.setString(1, joueur.getUniqueId() + "");
            select_mineral_playeruid.execute();
            donnee_joueur = select_mineral_playeruid.getResultSet();
            if (donnee_joueur.next()) {
                event.getMcPlayer().setDatabasePlayerId(donnee_joueur.getInt("id"));
                return;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        Game game = event.getGame();
        List<House> maisons = (List<House>) game.getHouses().clone();
        maisons.removeIf(maison -> maison.getTeam().getJoueurs().isEmpty());
        if (maisons.isEmpty()) {
            return;
        }
        try {
            PreparedStatement insert_mineral_game = connection.prepareStatement("INSERT INTO mineral_game SET gamestate = ?, map = ?, uid = ?");
            insert_mineral_game.setString(1, "started");
            insert_mineral_game.setString(2, event.getGame().groupe.getMapName());
            insert_mineral_game.setString(3, event.getGame().groupe.getIdentifiant());
            insert_mineral_game.executeUpdate();
            PreparedStatement select_mineral_game = connection.prepareStatement("SELECT * FROM mineral_game WHERE uid = ?");
            select_mineral_game.setString(1, event.getGame().groupe.getIdentifiant());
            select_mineral_game.execute();
            ResultSet resultatSet = select_mineral_game.getResultSet();
            if (resultatSet.next()) {
                int idGame = resultatSet.getInt("id");
                event.getGame().setDatabaseGameId(idGame);
                for (Player joueur : event.getGame().groupe.getPlayers()) {
                    MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
                    if (mcPlayer == null) continue;
                    PreparedStatement insert_game_players = connection.prepareStatement("INSERT INTO mineral_game_players SET gameid = ?, playerid = ?, join_event = ?, team = ?;");
                    insert_game_players.setInt(1, idGame);
                    insert_game_players.setInt(2, mcPlayer.getDatabasePlayerId());
                    if (event.getGame().isReferee(joueur)) {
                        insert_game_players.setString(3, "referee");
                    } else {
                        insert_game_players.setString(3, "at_game_start");
                    }
                    insert_game_players.setString(4, mcPlayer.getEquipe().getNomEquipe());
                    insert_game_players.executeUpdate();
                    if (event.getGame().groupe.getParametresPartie().getCVAR("enable_shop").getValeurNumerique() != 1) continue;
                    PreparedStatement insert_mineral_game_kit = connection.prepareStatement("INSERT INTO mineral_game_kits SET gameid = ?, playerid = ?, kit = ?;");
                    insert_mineral_game_kit.setInt(1, idGame);
                    insert_mineral_game_kit.setInt(2, mcPlayer.getDatabasePlayerId());
                    insert_mineral_game_kit.setString(3, event.getGame().groupe.getKitManager().getPlayerKit(joueur).getNom());
                    insert_mineral_game_kit.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerKilled(PlayerDeathByPlayerEvent event) {
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        Game partie = event.getPartie();
        MCPlayer deadPlayer = mineralcontest.plugin.getMCPlayer(event.getPlayerDead());
        MCPlayer killerPlayer = mineralcontest.plugin.getMCPlayer(event.getKiller());
        try {
            String deathCause = "HAND";
            event.getKiller().getInventory().getItemInMainHand();
            if (event.getKiller().getInventory().getItemInMainHand().getType() != Material.AIR) {
                deathCause = event.getKiller().getInventory().getItemInMainHand().getType().toString();
            }
            PreparedStatement insert_game_kill = connection.prepareStatement("INSERT INTO mineral_game_kills SET gameid = ?, dead_playerid = ?, killer_playerid = ?, cause = ?");
            insert_game_kill.setInt(1, partie.getDatabaseGameId());
            insert_game_kill.setInt(2, deadPlayer.getDatabasePlayerId());
            insert_game_kill.setInt(3, killerPlayer.getDatabasePlayerId());
            insert_game_kill.setString(4, deathCause);
            insert_game_kill.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onGameEnd(MCGameEndEvent endEvent) {
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        Game partie = endEvent.getGame();
        if (partie == null) {
            return;
        }
        try {
            PreparedStatement requete_update_game = connection.prepareStatement("UPDATE mineral_game SET gamestate = 'ended', date_end = CURRENT_TIMESTAMP WHERE id = ?");
            requete_update_game.setInt(1, partie.getDatabaseGameId());
            requete_update_game.executeUpdate();
            for (Player joueur : partie.groupe.getPlayers()) {
                MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
                if (mcPlayer == null) continue;
                PreparedStatement requete_verif_joueur_present_dbb = connection.prepareStatement("SELECT * FROM mineral_players WHERE id = ?");
                requete_verif_joueur_present_dbb.setInt(1, mcPlayer.getDatabasePlayerId());
                ResultSet resultat_requete_present_dbb = requete_verif_joueur_present_dbb.executeQuery();
                boolean present = resultat_requete_present_dbb.next();
                if (present) {
                    PreparedStatement requete_update_mineral_game_players = connection.prepareStatement("UPDATE mineral_game_players SET team = ? WHERE gameid = ? AND playerid = ?");
                    requete_update_mineral_game_players.setString(1, mcPlayer.getEquipe().getNomEquipe());
                    requete_update_mineral_game_players.setInt(2, partie.getDatabaseGameId());
                    requete_update_mineral_game_players.setInt(3, mcPlayer.getDatabasePlayerId());
                    requete_update_mineral_game_players.executeUpdate();
                    PreparedStatement requete_insert_mineral_game_end_players_info = connection.prepareStatement("INSERT INTO mineral_game_end_players_info SET gameid = ?, playerid = ?, score_brought = ?, score_lost = ?, team_score = ?");
                    requete_insert_mineral_game_end_players_info.setInt(1, partie.getDatabaseGameId());
                    requete_insert_mineral_game_end_players_info.setInt(2, mcPlayer.getDatabasePlayerId());
                    requete_insert_mineral_game_end_players_info.setInt(3, mcPlayer.getScore_brought());
                    requete_insert_mineral_game_end_players_info.setInt(4, mcPlayer.getScore_lost());
                    requete_insert_mineral_game_end_players_info.setInt(5, mcPlayer.getEquipe().getScore());
                    requete_insert_mineral_game_end_players_info.executeUpdate();
                }
                mcPlayer.resetPlayerScores();
            }
            for (House maison : partie.getHouses()) {
                if (maison.getTeam().getJoueurs().size() <= 0) continue;
                PreparedStatement requete_insert_team_infos = connection.prepareStatement("INSERT INTO mineral_game_end_teams_info SET gameid = ?, team = ?, score = ?");
                requete_insert_team_infos.setInt(1, partie.getDatabaseGameId());
                requete_insert_team_infos.setString(2, maison.getTeam().getNomEquipe());
                requete_insert_team_infos.setInt(3, maison.getTeam().getScore());
                requete_insert_team_infos.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onShopItemPurchase(MCShopItemPurchaseEvent shopItemPurchaseEvent) {
        Game partie = mineralcontest.getPlayerGame(shopItemPurchaseEvent.getJoueur());
        if (partie == null) {
            return;
        }
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(shopItemPurchaseEvent.getJoueur());
        if (mcPlayer == null) {
            return;
        }
        try {
            PreparedStatement requete_insert_shop_purchase = connection.prepareStatement("INSERT INTO mineral_game_shop_purchase SET gameid = ?, playerid = ?, shop_item = ?, item_price = ?, date = CURRENT_TIMESTAMP");
            requete_insert_shop_purchase.setInt(1, partie.getDatabaseGameId());
            requete_insert_shop_purchase.setInt(2, mcPlayer.getDatabasePlayerId());
            requete_insert_shop_purchase.setString(3, shopItemPurchaseEvent.getItem().getNomItem());
            requete_insert_shop_purchase.setInt(4, shopItemPurchaseEvent.getItem().getPrice());
            requete_insert_shop_purchase.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerChestOpen(MCPlayerOpenChestEvent event) {
        Connection connection = mineralcontest.plugin.getConnexion_database();
        if (connection == null) {
            return;
        }
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(event.getJoueur());
        if (mcPlayer == null) {
            return;
        }
        AutomatedChestAnimation coffre = event.getCoffre();
        if (coffre == null) {
            return;
        }
        try {
            PreparedStatement requete_insert_coffre_ouvert = connection.prepareStatement("INSERT INTO mineral_game_chests set gameid = ?, playerid = ?, chest_type = ?, date = CURRENT_TIMESTAMP;");
            requete_insert_coffre_ouvert.setInt(1, mcPlayer.getPartie().getDatabaseGameId());
            requete_insert_coffre_ouvert.setInt(2, mcPlayer.getDatabasePlayerId());
            if (coffre instanceof CoffreBoss) {
                requete_insert_coffre_ouvert.setString(3, "boss_chest");
            } else if (coffre instanceof CoffreParachute) {
                requete_insert_coffre_ouvert.setString(3, "aidrop");
            } else if (coffre instanceof CoffreArene) {
                requete_insert_coffre_ouvert.setString(3, "arena_chest");
            } else {
                requete_insert_coffre_ouvert.setString(3, "player_death_chest");
            }
            requete_insert_coffre_ouvert.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

