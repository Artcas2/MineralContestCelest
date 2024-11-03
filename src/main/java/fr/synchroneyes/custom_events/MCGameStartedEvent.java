package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;

public class MCGameStartedEvent extends MCEvent {
    private Game game;

    public MCGameStartedEvent(Game partie) {
        this.game = partie;
    }

    public Game getGame() {
        return this.game;
    }
}

