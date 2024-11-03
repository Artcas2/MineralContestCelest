package fr.synchroneyes.mineral.Core.Coffre;

import fr.synchroneyes.custom_events.MCAutomatedChestTimeOverEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreParachute;
import fr.synchroneyes.mineral.Core.Coffre.TimeChestAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class AutomatedChestManager implements Listener {
    private List<AutomatedChestAnimation> coffresAvecAnimation = new LinkedList<AutomatedChestAnimation>();
    private Queue<TimeChestAnimation> coffreAvecDureeDeVie = new LinkedBlockingQueue<TimeChestAnimation>();
    private Groupe groupe;
    private BukkitTask chestTimedLoop;

    public AutomatedChestManager(Groupe groupe) {
        this.groupe = groupe;
        this.registerCoffres();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    public Groupe getGroupe() {
        return this.groupe;
    }

    private void registerCoffres() {
        this.coffresAvecAnimation.add(new CoffreParachute(this));
        this.coffresAvecAnimation.add(new CoffreArene(this, null));
    }

    public void addChest(AutomatedChestAnimation chestAnimation) {
        if (!this.coffresAvecAnimation.contains(chestAnimation)) {
            this.coffresAvecAnimation.add(chestAnimation);
        }
    }

    public void addTimedChest(TimeChestAnimation chestAnimation) {
        this.coffreAvecDureeDeVie.add(chestAnimation);
        if (this.chestTimedLoop == null) {
            this.startChestTimerLoop();
        }
    }

    public AutomatedChestAnimation getFromInventory(Inventory v) {
        for (AutomatedChestAnimation automatedChestAnimation : this.coffresAvecAnimation) {
            if (!automatedChestAnimation.getInventory().equals((Object)v)) continue;
            return automatedChestAnimation;
        }
        return null;
    }

    public void replace(Class c, AutomatedChestAnimation automatedChestAnimation) {
        for (AutomatedChestAnimation automatedChestAnimation1 : this.coffresAvecAnimation) {
            if (!automatedChestAnimation1.getClass().equals(c)) continue;
            this.coffresAvecAnimation.remove(automatedChestAnimation1);
            break;
        }
        this.coffresAvecAnimation.add(automatedChestAnimation);
    }

    public boolean isThisAnAnimatedInventory(Inventory i) {
        for (AutomatedChestAnimation chestAnimation : this.coffresAvecAnimation) {
            if (!chestAnimation.getInventory().equals((Object)i)) continue;
            return true;
        }
        return false;
    }

    public AutomatedChestAnimation getChestAnomation(Block b) {
        for (AutomatedChestAnimation automatedChestAnimation : this.coffresAvecAnimation) {
            if (automatedChestAnimation.getLocation() == null || !automatedChestAnimation.getLocation().equals((Object)b.getLocation())) continue;
            return automatedChestAnimation;
        }
        for (AutomatedChestAnimation automatedChestAnimation : this.coffreAvecDureeDeVie) {
            if (automatedChestAnimation.getLocation() == null || !automatedChestAnimation.getLocation().equals((Object)b.getLocation())) continue;
            return automatedChestAnimation;
        }
        return null;
    }

    public boolean isThisBlockAChestAnimation(Block b) {
        for (AutomatedChestAnimation automatedChest : this.coffresAvecAnimation) {
            if (automatedChest.getLocation() == null || !automatedChest.getLocation().equals((Object)b.getLocation())) continue;
            return true;
        }
        for (TimeChestAnimation timedChest : this.coffreAvecDureeDeVie) {
            if (timedChest.getLocation() == null || !timedChest.getLocation().equals((Object)b.getLocation())) continue;
            return true;
        }
        return false;
    }

    @EventHandler
    public void onTimedChestEnd(MCAutomatedChestTimeOverEvent event) {
        TimeChestAnimation coffre = (TimeChestAnimation)event.getAutomatedChest();
        this.coffreAvecDureeDeVie.remove(coffre);
        if (this.coffreAvecDureeDeVie.isEmpty()) {
            this.endChestTimerLoop();
        }
    }

    private void endChestTimerLoop() {
        if (this.chestTimedLoop != null) {
            this.chestTimedLoop.cancel();
            this.chestTimedLoop = null;
        }
    }

    private void startChestTimerLoop() {
        if (this.chestTimedLoop != null) {
            this.endChestTimerLoop();
        }
        this.chestTimedLoop = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, this::doTimedChestTick, 0L, 20L);
    }

    private void doTimedChestTick() {
        if (this.coffresAvecAnimation.isEmpty()) {
            this.endChestTimerLoop();
            return;
        }
        for (TimeChestAnimation coffre : this.coffreAvecDureeDeVie) {
            if (!coffre.isCanTimeBeReduced()) continue;
            if (coffre.getTimeLeft() > 0) {
                coffre.reduceChestTime();
                continue;
            }
            coffre.deleteChest();
        }
    }
}

