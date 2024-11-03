package fr.synchroneyes.mineral.DeathAnimations;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.lang.reflect.Constructor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class EffectAnimation extends DeathAnimation {
    private EffectManager manager;
    private Effect effect;

    protected abstract Class getEffectClass();

    public abstract int getDuration();

    public abstract int getHeighOffset();

    public abstract void applyCustomSettings(Effect var1);

    public EffectAnimation() {
        this.manager = mineralcontest.plugin.effectManager;
    }

    @Override
    public void playAnimation(LivingEntity player) {
        try {
            Class<?> clzz = Class.forName(this.getEffectClass().getName());
            Constructor<?> constructor = clzz.getConstructor(EffectManager.class);
            Effect instance = (Effect)constructor.newInstance(this.manager);
            instance.duration = this.getDuration() * 1000;
            this.applyCustomSettings(instance);
            Location location = player.getLocation();
            if (this.getHeighOffset() > 0) {
                location.setY(location.getY() + (double)this.getHeighOffset());
            }
            instance.setLocation(location);
            instance.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

