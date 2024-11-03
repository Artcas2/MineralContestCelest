package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.entity.Player;

public class MCPlayerLeavePluginEvent extends MCEvent {
    private Player joueur;
    private MCPlayer mcPlayer;

    public MCPlayerLeavePluginEvent(Player joueur, MCPlayer mcPlayerInstance) {
        this.joueur = joueur;
        this.mcPlayer = mcPlayerInstance;
    }

    public MCPlayer getMcPlayer() {
        return this.mcPlayer;
    }

    public Player getJoueur() {
        return this.joueur;
    }
}

