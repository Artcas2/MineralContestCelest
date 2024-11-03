package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.Board;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class UpdatableBoard extends Board {
    private List<Player> players = new ArrayList<Player>();

    @Override
    public void startDisplay(Player p) {
        this.players.add(p);
        this.update(p);
    }

    @Override
    public void stopDisplay(Player p) {
        this.players.remove(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void update() {
        for (Player p : this.players) {
            this.update(p);
        }
    }

    protected abstract void update(Player var1);
}

