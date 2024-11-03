package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.World;
import org.bukkit.event.Cancellable;

public class MCPlayerChangedWorldEvent extends MCEvent implements Cancellable {
    private World from;
    private World to;
    private MCPlayer joueur;
    private boolean event_cancelled = false;

    public MCPlayerChangedWorldEvent(World from, World to, MCPlayer joueur) {
        this.from = from;
        this.to = to;
        this.joueur = joueur;
    }

    public boolean isCancelled() {
        return this.event_cancelled;
    }

    public void setCancelled(boolean b) {
        this.event_cancelled = b;
    }

    public World getFrom() {
        return this.from;
    }

    public World getTo() {
        return this.to;
    }

    public MCPlayer getJoueur() {
        return this.joueur;
    }
}

