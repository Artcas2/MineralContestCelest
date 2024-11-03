package fr.synchroneyes.mineral.DeathAnimations.Animations;

import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HalloweenHurricaneAnimation extends DeathAnimation {
    @Override
    public String getAnimationName() {
        return "Temp\u00eate d'Halloween";
    }

    @Override
    public Material getIcone() {
        return Material.NETHER_STAR;
    }

    @Override
    public void playAnimation(LivingEntity entity) {
        final ArrayList<Bat> list_chauve_souris = new ArrayList<Bat>();
        int duree_animation = 3;
        int nb_chauve_souris = 25;
        Location chaube_souris_Location = entity.getLocation().clone();
        chaube_souris_Location.setY((double)(chaube_souris_Location.getBlockY() + 1));
        for (int i = 0; i < nb_chauve_souris; ++i) {
            Bat chauve_souris = (Bat)entity.getWorld().spawnEntity(chaube_souris_Location, EntityType.BAT);
            chauve_souris.setGlowing(true);
            chauve_souris.setCollidable(true);
            chauve_souris.setSilent(true);
            list_chauve_souris.add(chauve_souris);
        }
        int nb_bloc_plafond = 20;
        Location copiedPlayerLocation = entity.getLocation().clone();
        boolean isUnderRoof = false;
        for (int y = 0; y < nb_bloc_plafond; ++y) {
            int tmpY = copiedPlayerLocation.getBlockY();
            copiedPlayerLocation.setY((double)(tmpY + 1));
            if (copiedPlayerLocation.getBlock().getType() == Material.AIR) continue;
            isUnderRoof = true;
            break;
        }
        AreaEffectCloud e = (AreaEffectCloud)entity.getWorld().spawnEntity(entity.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        e.setColor(Color.ORANGE);
        e.setDuration(duree_animation * 20);
        AreaEffectCloud e1 = (AreaEffectCloud)entity.getWorld().spawnEntity(entity.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        e1.setColor(Color.BLACK);
        e1.setDuration(duree_animation * 20);
        final AtomicInteger temps_chauve_souris = new AtomicInteger(duree_animation + 1);
        new BukkitRunnable(){

            public void run() {
                if (temps_chauve_souris.get() == 0) {
                    for (Entity chauve_sours : list_chauve_souris) {
                        chauve_sours.remove();
                    }
                    this.cancel();
                    list_chauve_souris.clear();
                    return;
                }
                temps_chauve_souris.decrementAndGet();
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 20L);
    }
}

