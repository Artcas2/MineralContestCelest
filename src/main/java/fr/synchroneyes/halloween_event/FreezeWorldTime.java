package fr.synchroneyes.halloween_event;

import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class FreezeWorldTime {
    private static int midnight;
    private static int currentWorldTime;
    private static boolean isWorldFrozen;
    private static World frozenWorld;
    private static BukkitTask boucle;

    public static void setFrozenWorld(World w) {
        frozenWorld = w;
    }

    public static void freezeWorld() {
        if (boucle != null) {
            boucle.cancel();
        }
        frozenWorld.setTime((long)midnight);
        boucle = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> frozenWorld.setTime((long)midnight), 0L, 200L);
    }

    public static void unfreezeWorld() {
        boucle.cancel();
    }

    static {
        currentWorldTime = midnight = 18000;
        isWorldFrozen = false;
        frozenWorld = null;
        boucle = null;
    }
}

