package fr.synchroneyes.mineral.DeathAnimations.Animations;

import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class SmokeAnimation extends DeathAnimation {
    @Override
    public String getAnimationName() {
        return "Partie en fum\u00e9e";
    }

    @Override
    public Material getIcone() {
        return Material.SMOKER;
    }

    @Override
    public void playAnimation(LivingEntity player) {
        Location location = player.getLocation();
        AreaEffectCloud effectCloud = (AreaEffectCloud)location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        effectCloud.setColor(Color.BLACK);
        effectCloud.setRadius(3.0f);
        effectCloud.setDuration(100);
    }
}

