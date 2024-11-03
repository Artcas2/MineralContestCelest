package fr.synchroneyes.mineral.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardUtil {
    private ScoreboardUtil() {
    }

    public static String[] cutUnranked(String[] content) {
        String[] elements = Arrays.copyOf(content, 16);
        if (elements[0] == null) {
            elements[0] = "Unamed board";
        }
        if (elements[0].length() > 32) {
            elements[0] = elements[0].substring(0, 32);
        }
        for (int i = 1; i < elements.length; ++i) {
            if (elements[i] == null || elements[i].length() <= 40) continue;
            elements[i] = elements[i].substring(0, 40);
        }
        return elements;
    }

    public static String cutRankedTitle(String title) {
        if (title == null) {
            return "Unamed board";
        }
        if (title.length() > 32) {
            return title.substring(0, 32);
        }
        return title;
    }

    public static HashMap<String, Integer> cutRanked(HashMap<String, Integer> content) {
        HashMap<String, Integer> elements = new HashMap<String, Integer>();
        elements.putAll(content);
        while (elements.size() > 15) {
            String minimumKey = (String)elements.keySet().toArray()[0];
            int minimum = (Integer)elements.get(minimumKey);
            for (String string : elements.keySet()) {
                if (elements.get(string) >= minimum && (elements.get(string) != minimum || string.compareTo(minimumKey) >= 0)) continue;
                minimumKey = string;
                minimum = elements.get(string);
            }
            elements.remove(minimumKey);
        }
        for (String string : new ArrayList<>(elements.keySet())) {
            if (string == null || string.length() <= 40) continue;
            int value = elements.get(string);
            elements.remove(string);
            elements.put(string.substring(0, 40), value);
        }
        return elements;
    }

    public static boolean unrankedSidebarDisplay(Player p, String ... elements) {
        elements = ScoreboardUtil.cutUnranked(elements);
        try {
            if (p.getScoreboard() == null || p.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() || p.getScoreboard().getObjectives().size() != 1) {
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            if (p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)) == null) {
                p.getScoreboard().registerNewObjective(p.getUniqueId().toString().substring(0, 16), "dummy");
                p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)).setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
            for (int i = 1; i < elements.length; ++i) {
                if (elements[i] == null || p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() == 16 - i) continue;
                p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
                for (String string : p.getScoreboard().getEntries()) {
                    if (p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)).getScore(string).getScore() != 16 - i || string.equals(elements[i])) continue;
                    p.getScoreboard().resetScores(string);
                }
            }
            for (String entry : p.getScoreboard().getEntries()) {
                boolean toErase = true;
                for (String element : elements) {
                    if (element == null || !element.equals(entry) || p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)).getScore(entry).getScore() != 16 - Arrays.asList(elements).indexOf(element)) continue;
                    toErase = false;
                    break;
                }
                if (!toErase) continue;
                p.getScoreboard().resetScores(entry);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unrankedSidebarDisplay(Collection<Player> players, String[] elements) {
        for (Player player : players) {
            if (ScoreboardUtil.unrankedSidebarDisplay(player, elements)) continue;
            return false;
        }
        return true;
    }

    public static boolean unrankedSidebarDisplay(Collection<Player> players, Scoreboard board, String ... elements) {
        try {
            String objName = "COLLAB-SB-WINTER";
            if (board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
            }
            elements = ScoreboardUtil.cutUnranked(elements);
            for (Player player : players) {
                if (player.getScoreboard() == board) continue;
                player.setScoreboard(board);
            }
            if (board.getObjective(objName) == null) {
                board.registerNewObjective(objName, "dummy");
                board.getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(elements[0]);
            for (int i = 1; i < elements.length; ++i) {
                if (elements[i] == null || board.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).getScore() == 16 - i) continue;
                board.getObjective(DisplaySlot.SIDEBAR).getScore(elements[i]).setScore(16 - i);
                for (String string : board.getEntries()) {
                    if (board.getObjective(objName).getScore(string).getScore() != 16 - i || string.equals(elements[i])) continue;
                    board.resetScores(string);
                }
            }
            for (String string : board.getEntries()) {
                boolean toErase = true;
                for (String element : elements) {
                    if (element == null || !element.equals(string) || board.getObjective(objName).getScore(string).getScore() != 16 - Arrays.asList(elements).indexOf(element)) continue;
                    toErase = false;
                    break;
                }
                if (!toErase) continue;
                board.resetScores(string);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rankedSidebarDisplay(Player p, String title, HashMap<String, Integer> elements) {
        try {
            title = ScoreboardUtil.cutRankedTitle(title);
            elements = ScoreboardUtil.cutRanked(elements);
            if (p.getScoreboard() == null || p.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard() || p.getScoreboard().getObjectives().size() != 1) {
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            if (p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)) == null) {
                p.getScoreboard().registerNewObjective(p.getUniqueId().toString().substring(0, 16), "dummy");
                p.getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)).setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
            for (String string : elements.keySet()) {
                if (p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string).getScore() == elements.get(string).intValue()) continue;
                p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string).setScore(elements.get(string).intValue());
            }
            for (String string : new ArrayList<>(p.getScoreboard().getEntries())) {
                if (elements.keySet().contains(string)) continue;
                p.getScoreboard().resetScores(string);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean rankedSidebarDisplay(Collection<Player> players, String title, HashMap<String, Integer> elements) {
        for (Player player : players) {
            if (ScoreboardUtil.rankedSidebarDisplay(player, title, elements)) continue;
            return false;
        }
        return true;
    }

    public static boolean rankedSidebarDisplay(Collection<Player> players, String title, HashMap<String, Integer> elements, Scoreboard board) {
        try {
            title = ScoreboardUtil.cutRankedTitle(title);
            elements = ScoreboardUtil.cutRanked(elements);
            String objName = "COLLAB-SB-WINTER";
            if (board == null) {
                board = Bukkit.getScoreboardManager().getNewScoreboard();
            }
            for (Player player : players) {
                if (player.getScoreboard() == board) continue;
                player.setScoreboard(board);
            }
            if (board.getObjective(objName) == null) {
                board.registerNewObjective(objName, "dummy");
                board.getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            board.getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
            for (String string : elements.keySet()) {
                if (board.getObjective(DisplaySlot.SIDEBAR).getScore(string).getScore() == elements.get(string).intValue()) continue;
                board.getObjective(DisplaySlot.SIDEBAR).getScore(string).setScore(elements.get(string).intValue());
            }
            for (String string : new ArrayList<>(board.getEntries())) {
                if (elements.keySet().contains(string)) continue;
                board.resetScores(string);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

