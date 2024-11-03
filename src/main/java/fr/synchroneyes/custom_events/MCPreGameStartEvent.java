package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import org.bukkit.event.Cancellable;

public class MCPreGameStartEvent extends MCEvent implements Cancellable {
    private boolean cancelled;
    private Game partie;

    public MCPreGameStartEvent(Game partie) {
        this.partie = partie;
    }

    public Game getPartie() {
        return this.partie;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

