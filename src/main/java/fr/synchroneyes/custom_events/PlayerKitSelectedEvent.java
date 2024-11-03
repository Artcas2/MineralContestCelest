package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import org.bukkit.entity.Player;

public class PlayerKitSelectedEvent extends MCEvent {
    private Player player;
    private KitAbstract selectedKit;

    public PlayerKitSelectedEvent(Player joueur, KitAbstract selectedKit) {
        this.player = joueur;
        this.selectedKit = selectedKit;
    }

    public Player getPlayer() {
        return this.player;
    }

    public KitAbstract getSelectedKit() {
        return this.selectedKit;
    }
}

