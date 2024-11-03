package fr.synchroneyes.mineral.Statistics.Class;

import fr.synchroneyes.mineral.Statistics.Statistic;
import fr.synchroneyes.mineral.Translation.Lang;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MinerStat extends Statistic {
    private Map<Player, Integer> playerInformation = new HashMap<Player, Integer>();

    @Override
    public void perform(Player p, Object target) {
        if (!this.playerInformation.containsKey(p)) {
            this.playerInformation.put(p, 0);
        }
        int nombreDeBlockCasse = this.playerInformation.get(p);
        this.playerInformation.replace(p, nombreDeBlockCasse + 1);
    }

    @Override
    public Player getHighestPlayer() {
        int max = -1;
        Player maxPlayer = null;
        for (Map.Entry<Player, Integer> infoJoueur : this.playerInformation.entrySet()) {
            if (infoJoueur.getValue() <= max) continue;
            max = infoJoueur.getValue();
            maxPlayer = infoJoueur.getKey();
        }
        return maxPlayer;
    }

    @Override
    public Player getLowestPlayer() {
        int max = Integer.MAX_VALUE;
        Player maxPlayer = null;
        for (Map.Entry<Player, Integer> infoJoueur : this.playerInformation.entrySet()) {
            if (infoJoueur.getValue() >= max) continue;
            max = infoJoueur.getValue();
            maxPlayer = infoJoueur.getKey();
        }
        return maxPlayer;
    }

    @Override
    public String getHighestPlayerTitle() {
        return Lang.stats_miner_best_ranked_title.toString();
    }

    @Override
    public String getLowerPlayerTitle() {
        return Lang.stats_miner_worst_ranked_title.getDefault();
    }

    @Override
    public String getHighestItemSubTitle() {
        return Lang.stats_miner_subtitle.toString().replace("%d", this.getHighestPlayerValue() + "");
    }

    @Override
    public String getLowestItemSubTitle() {
        return Lang.stats_miner_subtitle.toString().replace("%d", this.getLowerPlayerValue() + "");
    }

    @Override
    public Material getHighestPlayerIcon() {
        return Material.DIAMOND_PICKAXE;
    }

    @Override
    public Material getLowestPlayerIcon() {
        return Material.WOODEN_PICKAXE;
    }

    @Override
    public int getHighestPlayerValue() {
        int max = -1;
        Player maxPlayer = null;
        for (Map.Entry<Player, Integer> infoJoueur : this.playerInformation.entrySet()) {
            if (infoJoueur.getValue() <= max) continue;
            max = infoJoueur.getValue();
            maxPlayer = infoJoueur.getKey();
        }
        return max;
    }

    @Override
    public int getLowerPlayerValue() {
        int max = Integer.MAX_VALUE;
        Player maxPlayer = null;
        for (Map.Entry<Player, Integer> infoJoueur : this.playerInformation.entrySet()) {
            if (infoJoueur.getValue() >= max) continue;
            max = infoJoueur.getValue();
            maxPlayer = infoJoueur.getKey();
        }
        return max;
    }

    @Override
    public boolean isLowestValueRequired() {
        return true;
    }

    @Override
    public boolean isStatUsable() {
        return !this.playerInformation.isEmpty();
    }
}

