package fr.synchroneyes.mineral.Core.Boss;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerKilledByBossEvent;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BossManager implements Listener {
    private BukkitTask boucle;
    private List<Boss> bossList = new LinkedList<Boss>();
    private Game partie;

    public BossManager(Game partie) {
        this.partie = partie;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    public void spawnNewBoss(Location position, Boss boss) {
        this.bossList.add(boss);
        boss.setChestManager(this.partie.groupe.getAutomatedChestManager());
        boss.spawn(position);
    }

    public boolean wasPlayerKilledByBoss(Entity killer) {
        for (Boss boss : this.bossList) {
            if (!killer.equals((Object)boss.entity)) continue;
            return true;
        }
        return false;
    }

    public void fireBossMadeKill(Entity killer, Player deadPlayer) {
        if (!this.wasPlayerKilledByBoss(killer)) {
            return;
        }
        for (Boss boss : this.bossList) {
            if (!killer.equals((Object)boss.entity)) continue;
            Bukkit.getPluginManager().callEvent((Event)new MCPlayerKilledByBossEvent(deadPlayer, this.partie, boss));
            boss.onPlayerKilled(deadPlayer);
            return;
        }
    }

    private void doLoopTick() {
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        if (this.boucle == null) {
            this.boucle = new BukkitRunnable(){

                public void run() {
                    BossManager.this.doLoopTick();
                }
            }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 20L);
        }
    }

    public boolean isThisEntityABoss(LivingEntity e) {
        return !e.getMetadata("boss").isEmpty();
    }

    public boolean isThisEntitySpawnedByBoss(Entity e) {
        for (Boss b : this.bossList) {
            if (!b.isThisEntitySpawnedByBoss(e)) continue;
            return true;
        }
        return false;
    }

    public Boss toBoss(Entity e) {
        for (Boss b : this.bossList) {
            if (!b.isThisEntitySpawnedByBoss(e)) continue;
            return b;
        }
        return null;
    }
}

