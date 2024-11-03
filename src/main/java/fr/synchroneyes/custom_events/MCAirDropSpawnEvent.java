package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import org.bukkit.Location;

public class MCAirDropSpawnEvent extends MCEvent {
    private Game game;
    private Location dropLocation;

    public MCAirDropSpawnEvent(Location drop, Game partie) {
        this.dropLocation = drop;
        this.game = partie;
    }

    public Game getGame() {
        return this.game;
    }

    public Location getParachuteLocation() {
        return this.dropLocation;
    }
}

