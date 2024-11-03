package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.Referee.Referee;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerSpawn implements Listener {
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player joueur = e.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroupe == null) {
                this.teleportToLobby(joueur);
                return;
            }
            Game playerGame = playerGroupe.getGame();
            if (playerGame == null) {
                this.teleportToLobby(joueur);
                return;
            }
            if (playerGame.isGameStarted()) {
                Equipe playerTeam = playerGame.getPlayerTeam(joueur);
                if (playerTeam == null) {
                    if (playerGame.isReferee(joueur)) {
                        joueur.getInventory().addItem(new ItemStack[]{Referee.getRefereeItem()});
                    }
                    PlayerUtils.teleportPlayer(joueur, playerGroupe.getMonde(), playerGame.getArene().getCoffre().getLocation());
                    return;
                }
                playerGame.getArene().getDeathZone().add(joueur);
                return;
            }
            if (mineralcontest.isInMineralContestHub(joueur)) {
                PlayerUtils.teleportPlayer(joueur, mineralcontest.plugin.pluginWorld, mineralcontest.plugin.defaultSpawn);
                Bukkit.getLogger().severe("ddd");
                return;
            }
            if (playerGroupe.getMonde() == null) {
                Bukkit.getLogger().warning("[MC] A player is in a group, but should be in a loaded world, but in fact is in the hub. That's weird and should not happen. Please inform the staff");
                PlayerUtils.teleportPlayer(joueur, mineralcontest.plugin.pluginWorld, mineralcontest.plugin.defaultSpawn);
                return;
            }
            PlayerUtils.teleportPlayer(joueur, playerGroupe.getMonde(), playerGame.getArene().getCoffre().getLocation());
        }
    }

    private void teleportToLobby(Player joueur) {
        Location hubSpawnLocation = mineralcontest.plugin.defaultSpawn;
        PlayerUtils.getPluginWorld();
        hubSpawnLocation = mineralcontest.plugin.defaultSpawn;
        PlayerUtils.teleportPlayer(joueur, hubSpawnLocation.getWorld(), hubSpawnLocation);
    }
}

