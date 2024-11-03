package fr.synchroneyes.mineral.Core.Referee;

import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import java.util.LinkedList;
import java.util.Stack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Referee {
    public static Player refereeForcingVote;

    public static void forceVote(Player refereeForcingVote) {
        Referee.refereeForcingVote = refereeForcingVote;
    }

    public static void displayLeaderboard() {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedList<Equipe> equipes = new LinkedList<>();
        Stack<Equipe> leaderboard = new Stack<Equipe>();
        int minScore = Integer.MIN_VALUE;
        Equipe highestTeam = null;
        while (equipes.size() > 0) {
            for (Equipe equipe : equipes) {
                if (equipe.getScore() < minScore) continue;
                minScore = equipe.getScore();
                highestTeam = equipe;
            }
            equipes.remove(highestTeam);
            minScore = Integer.MIN_VALUE;
            leaderboard.add(highestTeam);
        }
        stringBuilder.append("=== " + Lang.referee_item_leaderboard.toString() + " ===\n");
        int position = 3;
        while (!leaderboard.empty()) {
            highestTeam = (Equipe)leaderboard.pop();
            stringBuilder.append(position + " - " + Lang.translate(Lang.team_score.toString(), highestTeam) + "\n");
            --position;
        }
        stringBuilder.append(ChatColor.WHITE + "===========\n");
    }

    public static void displayTeamScore() {
        LinkedList<Equipe> equipes = new LinkedList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Equipe equipe : equipes) {
            stringBuilder.append(equipe.getCouleur() + equipe.getNomEquipe() + ": " + equipe.getScore() + " points\n" + ChatColor.WHITE);
        }
    }

    public static ItemStack getRefereeItem() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Lang.referee_item_name.toString());
        item.setItemMeta(itemMeta);
        return item;
    }
}

