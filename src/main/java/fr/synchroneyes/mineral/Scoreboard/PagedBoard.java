package fr.synchroneyes.mineral.Scoreboard;

import fr.synchroneyes.mineral.Scoreboard.AutomaticBoard;
import fr.synchroneyes.mineral.Scoreboard.BoardPage;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class PagedBoard extends AutomaticBoard {
    private HashMap<BoardPage, Integer> pages = new HashMap();
    private int count = 0;
    private int currentPageId;

    public PagedBoard() {
        super(1);
    }

    public void addPage(BoardPage page, int ticks) {
        this.pages.put(page, ticks);
    }

    public void removePage(BoardPage page) {
        this.pages.remove(page);
    }

    @Override
    public void update(Player p) {
        this.getPage().update(p);
    }

    public BoardPage getPage() {
        return new ArrayList<BoardPage>(this.pages.keySet()).get(this.currentPageId);
    }

    @Override
    public void run() {
        super.run();
        if (++this.count >= this.pages.get(this.getPage())) {
            this.count = 0;
            ++this.currentPageId;
            if (this.currentPageId >= this.pages.size()) {
                this.currentPageId = 0;
            }
        }
    }
}

