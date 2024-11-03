package fr.synchroneyes.mineral.Statistics;

import fr.synchroneyes.mineral.Statistics.Statistic;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class MeilleurStatistic extends Statistic {
    protected Map<Player, Integer> infoJoueurs = new HashMap<Player, Integer>();

    @Override
    public Player getHighestPlayer() {
        return this.getMaxPlayer().getKey();
    }

    @Override
    public int getHighestPlayerValue() {
        return this.getMaxPlayer().getValue();
    }

    @Override
    public boolean isStatUsable() {
        return !this.infoJoueurs.isEmpty();
    }

    @Override
    public Player getLowestPlayer() {
        return null;
    }

    @Override
    public String getLowerPlayerTitle() {
        return null;
    }

    @Override
    public String getLowestItemSubTitle() {
        return null;
    }

    @Override
    public Material getLowestPlayerIcon() {
        return null;
    }

    @Override
    public int getLowerPlayerValue() {
        return 0;
    }

    @Override
    public boolean isLowestValueRequired() {
        return false;
    }

    private Map.Entry<Player, Integer> getMaxPlayer() {
        int max = -1;
        Map.Entry<Player, Integer> meilleur = null;
        for (Map.Entry<Player, Integer> info : this.infoJoueurs.entrySet()) {
            if (info.getValue() <= max) continue;
            max = info.getValue();
            meilleur = info;
        }
        return meilleur;
    }
}

