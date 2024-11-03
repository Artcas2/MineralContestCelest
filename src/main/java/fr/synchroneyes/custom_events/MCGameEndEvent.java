package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;

public class MCGameEndEvent extends MCEvent {
    private Game partie;

    public MCGameEndEvent(Game partie) {
        this.partie = partie;
    }

    public Game getGame() {
        return this.partie;
    }
}

