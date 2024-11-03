package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;

public class MCPlayerReconnectEvent extends MCEvent {
    private MCPlayer player;

    public MCPlayerReconnectEvent(MCPlayer player) {
        this.player = player;
    }

    public MCPlayer getPlayer() {
        return this.player;
    }
}

