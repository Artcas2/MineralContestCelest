package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerFoodLevelEvent implements Listener {
    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        Player joueur;
        if (event.getEntity() instanceof Player && mineralcontest.isInAMineralContestWorld(joueur = (Player)event.getEntity())) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            if (mcPlayer.getGroupe() == null) {
                return;
            }
            if (mcPlayer.getGroupe().getParametresPartie().getCVAR("enable_hunger").getValeurNumerique() != 1) {
                event.setCancelled(true);
                return;
            }
        }
    }
}

