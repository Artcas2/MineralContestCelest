package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathByPlayerEvent extends MCEvent {
    private Player playerDead;
    private Player killer;
    private Game partie;

    public PlayerDeathByPlayerEvent(Player dead, Player killer, Game partie) {
        this.playerDead = dead;
        this.killer = killer;
        this.partie = partie;
    }

    @NotNull
    public Game getPartie() {
        return this.partie;
    }

    @NotNull
    public Player getPlayerDead() {
        return this.playerDead;
    }

    @NotNull
    public Player getKiller() {
        return this.killer;
    }
}

