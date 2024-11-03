package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.Game.Game;
import org.bukkit.entity.Player;

public class MCPlayerKilledByBossEvent extends MCEvent {
    private Player player;
    private Game game;
    private Boss boss;

    public MCPlayerKilledByBossEvent(Player player, Game game, Boss boss) {
        this.player = player;
        this.game = game;
        this.boss = boss;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Game getGame() {
        return this.game;
    }

    public Boss getBoss() {
        return this.boss;
    }
}

