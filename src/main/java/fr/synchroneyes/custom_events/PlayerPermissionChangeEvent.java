package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import org.bukkit.entity.Player;

public class PlayerPermissionChangeEvent extends MCEvent {
    private Player player;
    private String oldPermission;
    private String newPermission;

    public PlayerPermissionChangeEvent(Player p, String old, String new_p) {
        this.player = p;
        this.oldPermission = old;
        this.newPermission = new_p;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getOldPermission() {
        return this.oldPermission;
    }

    public String getNewPermission() {
        return this.newPermission;
    }
}

