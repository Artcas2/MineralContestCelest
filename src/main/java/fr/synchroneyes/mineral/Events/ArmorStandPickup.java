package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ArmorStandPickup implements Listener {
    @EventHandler
    public void onPlayerItemPicked(PlayerInteractAtEntityEvent event) {
        Player joueur = event.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur) && event.getRightClicked() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }
}

