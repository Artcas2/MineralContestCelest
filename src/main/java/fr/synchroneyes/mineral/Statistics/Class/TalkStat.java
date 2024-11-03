package fr.synchroneyes.mineral.Statistics.Class;

import fr.synchroneyes.mineral.Statistics.MeilleurStatistic;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TalkStat extends MeilleurStatistic {
    @Override
    public void perform(Player p, Object target) {
        if (!this.infoJoueurs.containsKey(p)) {
            this.infoJoueurs.put(p, 0);
        }
        int nbMessageEnvoye = (Integer)this.infoJoueurs.get(p);
        this.infoJoueurs.replace(p, nbMessageEnvoye + 1);
    }

    @Override
    public String getHighestPlayerTitle() {
        return Lang.stats_most_talking_title.toString();
    }

    @Override
    public String getHighestItemSubTitle() {
        return Lang.stats_most_talking_subtitle.toString().replace("%d", this.getHighestPlayerValue() + "");
    }

    @Override
    public Material getHighestPlayerIcon() {
        return Material.WRITABLE_BOOK;
    }
}

