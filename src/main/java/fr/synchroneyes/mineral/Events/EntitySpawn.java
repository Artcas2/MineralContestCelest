package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.Plugin;

public class EntitySpawn implements Listener {
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) throws Exception {
        World worldEvent = e.getEntity().getWorld();
        if (mineralcontest.isAMineralContestWorld(worldEvent)) {
            if (e.getEntity() instanceof Phantom) {
                e.setCancelled(true);
                Bukkit.getLogger().info("[MineralContest][INFO] Blocked a phantom spawn");
                return;
            }
            Game partie = mineralcontest.getWorldGame(worldEvent);
            if ((e.getEntity() instanceof Monster || e.getEntity() instanceof Mob && !(e.getEntity() instanceof Chicken) && !(e.getEntity() instanceof Villager) && !(e.getEntity() instanceof Horse) && !(e.getEntity() instanceof ArmorStand)) && partie != null && partie.isGameStarted() && Radius.isBlockInRadius(partie.getArene().getCoffre().getLocation(), e.getEntity().getLocation(), partie.groupe.getParametresPartie().getCVAR("protected_zone_area_radius").getValeurNumerique())) {
                Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                    LivingEntity livingEntity;
                    if (partie.getArene().chickenWaves.isFromChickenWave((LivingEntity)e.getEntity())) {
                        return;
                    }
                    if (e.getEntity() instanceof Bat) {
                        return;
                    }
                    if (partie.getBossManager().isThisEntityABoss((LivingEntity)e.getEntity()) && ((livingEntity = (LivingEntity)e.getEntity()).getCustomName() != null || livingEntity.getCustomName().length() > 1)) {
                        return;
                    }
                    if (partie.groupe.getParametresPartie().getCVAR("enable_monster_in_protected_zone").getValeurNumerique() != 1) {
                        e.setCancelled(true);
                    }
                }, 1L);
            }
        }
    }
}

