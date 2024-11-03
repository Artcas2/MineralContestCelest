package fr.synchroneyes.special_events.halloween2024.game_events;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.special_events.halloween2024.utils.HalloweenTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class HalloweenEvent {
    private Game partie;

    public HalloweenEvent(Game partie) {
        this.partie = partie;
    }

    public abstract String getEventName();

    public abstract void executionContent();

    public abstract void beforeExecute();

    public abstract void afterExecute();

    public abstract String getEventTitle();

    public abstract String getEventDescription();

    public abstract boolean isTextMessageNotificationEnabled();

    public abstract boolean isNotificationDelayed();

    public void execute() {
        Bukkit.getLogger().info("[MineralContest][Halloween2024] Executing event: " + this.getEventName());
        this.beforeExecute();
        if (!this.isNotificationDelayed()) {
            this.sendEventNotification();
        }
        this.executionContent();
        this.afterExecute();
    }

    public void sendEventNotification() {
        for (Player player : this.partie.groupe.getPlayers()) {
            HalloweenTitle.sendTitle(player, this.getEventTitle(), this.getEventDescription(), 1, 5, 1, this.isTextMessageNotificationEnabled());
        }
    }

    public Game getPartie() {
        return this.partie;
    }
}

