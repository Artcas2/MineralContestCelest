package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.BoardPage;
import fr.synchroneyes.mineral.Scoreboard.ScoreboardUtil;
import org.bukkit.entity.Player;

public class SimpleUnrankedPage implements BoardPage {
    private String[] content;

    public SimpleUnrankedPage(String[] content) {
        this.content = content;
    }

    @Override
    public void update(Player p) {
        ScoreboardUtil.unrankedSidebarDisplay(p, this.content);
    }
}

