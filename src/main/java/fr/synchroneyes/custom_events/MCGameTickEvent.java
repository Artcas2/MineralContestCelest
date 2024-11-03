package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;

public class MCGameTickEvent extends MCEvent {
    private Game game;

    public MCGameTickEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }
}

