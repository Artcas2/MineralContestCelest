package fr.synchroneyes.custom_events;

import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLocationSaverLoop implements Runnable {
    int secondsDelay = 2;
    private JavaPlugin plugin;

    public PlayerLocationSaverLoop(JavaPlugin plugin, int delayBetweenEachCheck) {
        this.plugin = plugin;
        this.secondsDelay = delayBetweenEachCheck;
    }

    @Override
    public synchronized void run() {
        new BukkitRunnable(){

            public synchronized void run() {
                for (Player online_player : PlayerLocationSaverLoop.this.plugin.getServer().getOnlinePlayers()) {
                    MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(online_player);
                    if (mcPlayer == null) continue;
                    mcPlayer.setPlayerWorldLocation(online_player.getWorld(), online_player.getLocation());
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, (long)(this.secondsDelay * 20));
    }
}

