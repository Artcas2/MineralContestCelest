package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.BoardPage;
import fr.synchroneyes.mineral.Scoreboard.ScoreboardUtil;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class SimpleRankedPage implements BoardPage {
    private String title;
    private HashMap<String, Integer> content;

    public SimpleRankedPage(String title, HashMap<String, Integer> content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public void update(Player p) {
        ScoreboardUtil.rankedSidebarDisplay(p, this.title, this.content);
    }
}

