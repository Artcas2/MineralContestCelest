package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;

public class MCAirDropTickEvent extends MCEvent {
    private Game game;
    private int timeLeft;

    public MCAirDropTickEvent(int timeLeft, Game partie) {
        this.timeLeft = timeLeft;
        this.game = partie;
    }

    public Game getGame() {
        return this.game;
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }
}

