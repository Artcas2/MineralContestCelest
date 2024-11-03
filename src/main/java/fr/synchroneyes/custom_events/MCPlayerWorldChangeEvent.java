package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MCPlayerWorldChangeEvent extends MCEvent {
    private Location fromLocation;
    private World fromWorld;
    private Location toLocation;
    private World toWorld;
    private Player player;

    public MCPlayerWorldChangeEvent(Location from, Location to, Player player) {
        this.fromLocation = from;
        this.fromWorld = from.getWorld();
        this.toLocation = to;
        this.toWorld = to.getWorld();
        this.player = player;
    }

    public Location getFromLocation() {
        return this.fromLocation;
    }

    public World getFromWorld() {
        return this.fromWorld;
    }

    public Location getToLocation() {
        return this.toLocation;
    }

    public World getToWorld() {
        return this.toWorld;
    }

    public Player getPlayer() {
        return this.player;
    }
}

