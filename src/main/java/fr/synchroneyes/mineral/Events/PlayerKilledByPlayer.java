package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreMortJoueur;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerKilledByPlayer implements Listener {
    @EventHandler
    public void onPlayerKilled(PlayerDeathByPlayerEvent event) {
        int distanceProtectedArenaRadius;
        Player deadPlayer = event.getPlayerDead();
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(deadPlayer);
        if (mcPlayer == null) {
            return;
        }
        if (mcPlayer.getGroupe().getParametresPartie().getCVAR("drop_chest_on_death").getValeurNumerique() != 1) {
            return;
        }
        Location deadLocation = new Location(deadPlayer.getWorld(), (double)deadPlayer.getLocation().getBlockX(), (double)deadPlayer.getLocation().getBlockY(), (double)deadPlayer.getLocation().getBlockZ());
        while (deadLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            int y = deadLocation.getBlockY();
            deadLocation.setY((double)(--y));
        }
        Location arenaCenterLocation = event.getPartie().getArene().getCoffre().getLocation();
        if (Radius.isBlockInRadius(deadLocation, arenaCenterLocation, distanceProtectedArenaRadius = event.getPartie().groupe.getParametresPartie().getCVAR("protected_zone_area_radius").getValeurNumerique())) {
            return;
        }
        CoffreMortJoueur coffreMortJoueur = new CoffreMortJoueur(45, mcPlayer.getGroupe().getAutomatedChestManager(), deadPlayer);
        coffreMortJoueur.setChestLocation(deadLocation);
        mcPlayer.getGroupe().getAutomatedChestManager().addTimedChest(coffreMortJoueur);
        coffreMortJoueur.spawn();
        deadPlayer.getInventory().clear();
    }
}

