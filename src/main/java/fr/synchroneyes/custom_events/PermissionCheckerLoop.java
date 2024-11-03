package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.PlayerPermissionChangeEvent;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PermissionCheckerLoop implements Runnable {
    int secondsDelay = 2;
    private JavaPlugin plugin;

    public PermissionCheckerLoop(JavaPlugin plugin, int delayBetweenEachCheck) {
        this.plugin = plugin;
        this.secondsDelay = delayBetweenEachCheck;
    }

    @Override
    public synchronized void run() {
        final HashMap<Player, Boolean> playerCurrentPerm = new HashMap<Player, Boolean>();
        for (Player online_player : this.plugin.getServer().getOnlinePlayers()) {
            playerCurrentPerm.put(online_player, online_player.isOp());
        }
        new BukkitRunnable(){

            public synchronized void run() {
                for (Player online_player : PermissionCheckerLoop.this.plugin.getServer().getOnlinePlayers()) {
                    if (playerCurrentPerm.containsKey(online_player)) {
                        boolean currentSavedOp = (Boolean)playerCurrentPerm.get(online_player);
                        if (((Boolean)playerCurrentPerm.get(online_player)).equals(online_player.isOp())) continue;
                        PlayerPermissionChangeEvent event = new PlayerPermissionChangeEvent(online_player, PermissionCheckerLoop.this.opToString(currentSavedOp), PermissionCheckerLoop.this.opToString(online_player.isOp()));
                        Bukkit.getPluginManager().callEvent((Event)event);
                        playerCurrentPerm.replace(online_player, online_player.isOp());
                        continue;
                    }
                    playerCurrentPerm.put(online_player, online_player.isOp());
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, (long)(this.secondsDelay * 20));
    }

    public String opToString(boolean op) {
        return op ? "op" : "non_op";
    }
}

