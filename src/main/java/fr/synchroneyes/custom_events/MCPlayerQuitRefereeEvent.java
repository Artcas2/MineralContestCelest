package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.event.Cancellable;

public class MCPlayerQuitRefereeEvent extends MCEvent implements Cancellable {
    private boolean isCancelled;
    private MCPlayer player;

    public MCPlayerQuitRefereeEvent(MCPlayer player) {
        this.player = player;
    }

    public MCPlayer getPlayer() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}

