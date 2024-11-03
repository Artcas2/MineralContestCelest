package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.event.Cancellable;

public class MCPlayerLocationHUDUpdatedEvent extends MCEvent implements Cancellable {
    private MCPlayer player;
    private boolean cancelled;

    public MCPlayerLocationHUDUpdatedEvent(MCPlayer player) {
        this.player = player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public MCPlayer getPlayer() {
        return this.player;
    }
}

