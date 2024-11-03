package fr.synchroneyes.mineral.Core.Parachute.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Parachute.Parachute;
import fr.synchroneyes.mineral.Core.Parachute.ParachuteManager;
import fr.synchroneyes.mineral.Statistics.Class.MostParachuteHitStat;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ParachuteHitDetection implements Listener {
    @EventHandler
    public void onBlockHitByArrow(ProjectileHitEvent event) {
        Player tireur;
        Arrow fleche;
        if (event.getEntity() instanceof Arrow && (fleche = (Arrow)event.getEntity()).getShooter() instanceof Player && mineralcontest.isInAMineralContestWorld(tireur = (Player)fleche.getShooter())) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(tireur);
            if (playerGroup == null || playerGroup.getGame() == null || !playerGroup.getGame().isGameStarted()) {
                return;
            }
            ParachuteManager parachuteManager = playerGroup.getGame().getParachuteManager();
            if (parachuteManager == null) {
                return;
            }
            for (Parachute parachute : parachuteManager.getParachutes()) {
                if (!parachute.isParachuteHit((Projectile)fleche)) continue;
                parachute.receiveDamage(fleche.getDamage(), fleche.getLocation());
                playerGroup.getGame().getStatsManager().register(MostParachuteHitStat.class, tireur, null);
                fleche.remove();
            }
        }
    }
}

