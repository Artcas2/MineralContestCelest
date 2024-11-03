package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;

public class MCPlayerLeaveWorldPluginEvent extends MCEvent {
    private Player joueur;
    private MCPlayer mcPlayer;

    public MCPlayerLeaveWorldPluginEvent(Player joueur) {
        this.joueur = joueur;
        this.mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
    }

    public Player getJoueur() {
        return this.joueur;
    }

    public MCPlayer getMcPlayer() {
        return this.mcPlayer;
    }
}

