package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.PlayerPermissionChangeEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPermissionChange implements Listener {
    @EventHandler
    public void onPlayerPermissionChange(PlayerPermissionChangeEvent event) {
        Player player = event.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(player)) {
            Groupe playerGroupe = mineralcontest.getPlayerGroupe(player);
            if (playerGroupe == null) {
                return;
            }
            if (player.isOp() && !playerGroupe.isAdmin(player)) {
                playerGroupe.addAdmin(player);
            }
        }
    }
}

