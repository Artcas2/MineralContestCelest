package fr.synchroneyes.custom_events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Evenement appelé lors de la mort par un autre joueur
 */
public class PlayerDeathByPlayerEvent extends Event {

    private Player playerDead;
    private Player killer;


    private static final HandlerList handlers = new HandlerList();

    public PlayerDeathByPlayerEvent(Player dead, Player killer) {
        this.playerDead = dead;
        this.killer = killer;
    }


    public Player getPlayerDead() {
        return playerDead;
    }

    public Player getKiller() {
        return killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
