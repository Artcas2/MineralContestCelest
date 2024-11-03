package fr.synchroneyes.mineral.Core.Coffre;

import fr.synchroneyes.custom_events.MCAutomatedChestTimeOverEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Coffre.TimeChestOpening;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;

public abstract class TimeChestAnimation extends AutomatedChestAnimation {
    private int timeLeft = this.getChestAliveTime();
    private boolean canTimeBeReduced = false;

    public TimeChestAnimation(int tailleInventaire, AutomatedChestManager manager) {
        super(tailleInventaire, manager);
    }

    public abstract int getChestAliveTime();

    public abstract TimeChestOpening getTimeTriggerAction();

    public void reduceChestTime() {
        --this.timeLeft;
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }

    public void deleteChest() {
        MCAutomatedChestTimeOverEvent event = new MCAutomatedChestTimeOverEvent(this, this.getOpeningPlayer());
        Bukkit.getPluginManager().callEvent((Event)event);
        this.getLocation().getBlock().setType(Material.AIR);
        this.isAnimationOver = true;
        if (this.openingPlayer != null) {
            this.openingPlayer.closeInventory();
        }
    }

    @Override
    public void spawn() {
        if (this.getTimeTriggerAction() == TimeChestOpening.ON_SPAWN) {
            this.canTimeBeReduced = true;
        }
        super.spawn();
    }

    @Override
    public void actionToPerformAfterAnimationOver() {
        if (this.getTimeTriggerAction() == TimeChestOpening.AFTER_OPENING_ANIMATION) {
            this.canTimeBeReduced = true;
        }
    }

    public boolean isCanTimeBeReduced() {
        return this.canTimeBeReduced;
    }
}

