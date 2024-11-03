package fr.synchroneyes.mineral.Scoreboard;

import java.util.Collection;
import org.bukkit.entity.Player;

public abstract class Board {
    public abstract void startDisplay(Player var1);

    public abstract void stopDisplay(Player var1);

    public void startDisplay(Collection<Player> players) {
        for (Player p : players) {
            this.startDisplay(p);
        }
    }
}

