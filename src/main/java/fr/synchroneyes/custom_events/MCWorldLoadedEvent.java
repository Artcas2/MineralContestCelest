package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.groups.Core.Groupe;
import org.bukkit.World;

public class MCWorldLoadedEvent extends MCEvent {
    private World monde;
    private String world_name = "";
    private Groupe groupe;

    public MCWorldLoadedEvent(String world_name, World w, Groupe groupe) {
        this.monde = w;
        this.world_name = world_name;
        this.groupe = groupe;
    }

    public World getMonde() {
        return this.monde;
    }

    public String getWorld_name() {
        return this.world_name;
    }

    public Groupe getGroupe() {
        return this.groupe;
    }
}

