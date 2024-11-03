package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import org.bukkit.entity.Player;

public class MCPlayerOpenChestEvent extends MCEvent {
    private Player joueur;
    private AutomatedChestAnimation coffre;

    public MCPlayerOpenChestEvent(AutomatedChestAnimation coffre, Player joueur) {
        this.joueur = joueur;
        this.coffre = coffre;
    }

    public Player getJoueur() {
        return this.joueur;
    }

    public AutomatedChestAnimation getCoffre() {
        return this.coffre;
    }
}

