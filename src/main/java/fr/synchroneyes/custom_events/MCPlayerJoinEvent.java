package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;

public class MCPlayerJoinEvent extends MCEvent {
    private MCPlayer mcPlayer;
    private Player player;

    public MCPlayerJoinEvent(Player p) {
        this.player = p;
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(p);
        this.mcPlayer = mcPlayer == null ? new MCPlayer(p) : mcPlayer;
    }

    public MCPlayer getMcPlayer() {
        return this.mcPlayer;
    }

    public Player getPlayer() {
        return this.player;
    }
}

