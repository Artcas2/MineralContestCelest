package fr.synchroneyes.mineral.Statistics.Class;

import fr.synchroneyes.mineral.Statistics.MeilleurStatistic;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BossKiller extends MeilleurStatistic {
    @Override
    public void perform(Player p, Object target) {
        if (!this.infoJoueurs.containsKey(p)) {
            this.infoJoueurs.put(p, 0);
        }
        int nbPouletsTue = (Integer)this.infoJoueurs.get(p);
        this.infoJoueurs.replace(p, nbPouletsTue + 1);
    }

    @Override
    public String getHighestPlayerTitle() {
        return "Tueur de boss";
    }

    @Override
    public String getHighestItemSubTitle() {
        return "Avec %d de boss tu\u00e9";
    }

    @Override
    public Material getHighestPlayerIcon() {
        return Material.SPIDER_EYE;
    }
}

