package fr.synchroneyes.mineral.Statistics.Class;

import fr.synchroneyes.mineral.Statistics.MeilleurStatistic;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VilainJoueurStat extends MeilleurStatistic {
    @Override
    public void perform(Player p, Object target) {
        if (!(target instanceof Integer)) {
            return;
        }
        int nbPointAjoute = (Integer)target;
        if (!this.infoJoueurs.containsKey(p)) {
            this.infoJoueurs.put(p, 0);
        }
        int nbPointsRapporteTotal = (Integer)this.infoJoueurs.get(p);
        this.infoJoueurs.replace(p, nbPointsRapporteTotal + nbPointAjoute);
    }

    @Override
    public String getHighestPlayerTitle() {
        return Lang.stats_mostpoints_taken_title.toString();
    }

    @Override
    public String getHighestItemSubTitle() {
        return Lang.stats_mostpoints_taken_subtitle.toString().replace("%d", this.getHighestPlayerValue() + "");
    }

    @Override
    public Material getHighestPlayerIcon() {
        return Material.REDSTONE;
    }
}

