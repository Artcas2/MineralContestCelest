package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import org.bukkit.entity.Player;

public class MCPlayerRespawnEvent extends MCEvent {
    private Player joueur;

    public MCPlayerRespawnEvent(Player joueur) {
        this.joueur = joueur;
    }

    public Player getJoueur() {
        return this.joueur;
    }
}

