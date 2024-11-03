package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.UpdatableBoard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class AutomaticBoard extends UpdatableBoard implements Runnable {
    private int taskId = -1;
    private int delay;

    public AutomaticBoard(int delay) {
        this.delay = delay;
    }

    public void start(Plugin plugin) {
        this.taskId = Bukkit.getScheduler().runTaskTimer(plugin, (Runnable)this, 0L, (long)this.delay).getTaskId();
    }

    public void stop() {
        if (this.taskId != -1) {
            Bukkit.getScheduler().cancelTask(this.taskId);
            this.taskId = -1;
        }
    }

    @Override
    public void run() {
        this.update();
    }
}

