package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.Board;
import fr.synchroneyes.mineral.Scoreboard.ScoreboardUtil;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaticSidebarBoard extends Board {
    private SidebarBoardType type;
    private Object data;

    public StaticSidebarBoard(String ... elements) {
        this.data = elements;
        this.type = SidebarBoardType.UNRANKED;
    }

    public StaticSidebarBoard(String title, HashMap<String, Integer> elements) {
        this.data = new Object[]{title, elements};
        this.type = SidebarBoardType.UNRANKED;
    }

    @Override
    public void startDisplay(Player p) {
        switch (this.type) {
            case RANKED: {
                ScoreboardUtil.rankedSidebarDisplay(p, (String)((Object[])this.data)[0], (HashMap<String, Integer>)((HashMap)((Object[])this.data)[1]));
                return;
            }
            case UNRANKED: {
                ScoreboardUtil.unrankedSidebarDisplay(p, (String[])this.data);
            }
        }
    }

    @Override
    public void stopDisplay(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static enum SidebarBoardType {
        RANKED,
        UNRANKED;

    }
}

