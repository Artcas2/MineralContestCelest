package fr.synchroneyes.mineral.Events;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

public class BowEvent implements Listener {
    @EventHandler
    public void onBowHit(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            Arrow fleche = (Arrow)event.getProjectile();
            event.getEntity().sendMessage("d\u00e9gats: " + fleche.getDamage() + " - hit: " + fleche.getFallDistance());
        }
    }
}

