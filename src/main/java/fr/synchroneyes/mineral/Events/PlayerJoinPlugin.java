package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Utils.DisconnectedPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinPlugin implements Listener {
    @EventHandler
    public void onPlayerJoinPlugin(MCPlayerJoinEvent event) {
        Player joueur = event.getPlayer();
        DisconnectedPlayer disconnectedPlayer = mineralcontest.plugin.wasPlayerDisconnected(joueur);
        if (disconnectedPlayer != null) {
            mineralcontest.plugin.addNewPlayer(joueur);
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            if (mcPlayer == null) {
                return;
            }
            mcPlayer.reconnectPlayer(disconnectedPlayer);
            mcPlayer.setVisible();
            return;
        }
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        if (mcPlayer != null) {
            return;
        }
        mineralcontest.plugin.addNewPlayer(joueur);
        mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        if (!mineralcontest.communityVersion) {
            if (joueur.isOp()) {
                mineralcontest.plugin.getNonCommunityGroup().addAdmin(joueur);
            } else {
                mineralcontest.plugin.getNonCommunityGroup().addJoueur(joueur);
            }
            if (mcPlayer.getGroupe() != null) {
                if (!mcPlayer.getGroupe().getGame().isGameStarted() && mcPlayer.getGroupe().getGame().isGameInitialized) {
                    mcPlayer.getJoueur().getInventory().setItemInMainHand(Game.getTeamSelectionItem());
                }
                if (mcPlayer.getGroupe().getGame().isGameStarted()) {
                    joueur.setGameMode(GameMode.SPECTATOR);
                    Bukkit.broadcastMessage((String)"Le joueur a \u00e9t\u00e9 mis en spectateur !");
                }
            }
        }
    }
}

