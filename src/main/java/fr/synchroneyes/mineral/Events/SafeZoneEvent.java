package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SafeZoneEvent implements Listener {
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) throws Exception {
        World worldEvent = event.getEntity().getWorld();
        if (mineralcontest.isAMineralContestWorld(worldEvent)) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }
            Player joueur = (Player)event.getEntity();
            Game partie = mineralcontest.getPlayerGame(joueur);
            if (partie != null && partie.isGameStarted() && event.getEntity() instanceof Player) {
                Arrow arrow;
                Player victime = (Player)event.getEntity();
                Player attaquant = null;
                if (event.getDamager() instanceof Arrow && (arrow = (Arrow)event.getDamager()).getShooter() instanceof Player) {
                    attaquant = (Player)arrow.getShooter();
                }
                if (event.getDamager() instanceof Player) {
                    attaquant = (Player)event.getDamager();
                }
                if (attaquant != null && (PlayerUtils.isPlayerInHisBase(victime) || PlayerUtils.isPlayerInHisBase(attaquant))) {
                    event.setCancelled(true);
                }
                if (PlayerUtils.isPlayerInDeathZone(victime)) {
                    event.setCancelled(true);
                }
                if (attaquant != null && PlayerUtils.isPlayerInHisBase(attaquant) || PlayerUtils.isPlayerInHisBase(victime)) {
                    event.setCancelled(true);
                }
                if (attaquant != null && Radius.isBlockInRadius(partie.getArene().getTeleportSpawn(), attaquant.getLocation(), partie.groupe.getParametresPartie().getCVAR("arena_safezone_radius").getValeurNumerique())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

