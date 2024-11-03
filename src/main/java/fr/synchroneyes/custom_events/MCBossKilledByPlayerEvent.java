package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import org.bukkit.entity.Player;

public class MCBossKilledByPlayerEvent extends MCEvent {
    private Boss boss;
    private Player player;

    public MCBossKilledByPlayerEvent(Boss boss, Player player) {
        this.boss = boss;
        this.player = player;
    }

    public Boss getBoss() {
        return this.boss;
    }

    public Player getPlayer() {
        return this.player;
    }
}

