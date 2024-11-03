package fr.synchroneyes.mineral.Statistics.Class;

import fr.synchroneyes.mineral.Statistics.Statistic;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Pair;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KillStat extends Statistic {
    Map<Player, Pair<Integer, Integer>> informationsKills = new HashMap<Player, Pair<Integer, Integer>>();

    @Override
    public void perform(Player tueur, Object cible) {
        if (cible instanceof Player) {
            Player victime = (Player)cible;
            if (cible.equals(tueur)) {
                this.enregister(tueur, Type.DEATH);
                return;
            }
            this.enregister(tueur, Type.KILL);
            this.enregister(victime, Type.DEATH);
            return;
        }
        if (cible == null) {
            this.enregister(tueur, Type.DEATH);
            return;
        }
    }

    @Override
    public Player getHighestPlayer() {
        int maxKills = Integer.MIN_VALUE;
        Player highest = null;
        for (Map.Entry<Player, Pair<Integer, Integer>> infoJoueur : this.informationsKills.entrySet()) {
            Pair<Integer, Integer> ratio = infoJoueur.getValue();
            if (ratio.getKey() <= maxKills) continue;
            highest = infoJoueur.getKey();
            maxKills = ratio.getKey();
        }
        return highest;
    }

    @Override
    public Player getLowestPlayer() {
        int maxDeaths = Integer.MIN_VALUE;
        Player lowest = null;
        for (Map.Entry<Player, Pair<Integer, Integer>> infoJoueur : this.informationsKills.entrySet()) {
            Pair<Integer, Integer> ratio = infoJoueur.getValue();
            if (ratio.getValue() <= maxDeaths) continue;
            lowest = infoJoueur.getKey();
            maxDeaths = ratio.getValue();
        }
        return lowest;
    }

    @Override
    public String getHighestPlayerTitle() {
        return Lang.stats_kill_best_ranked_title.toString();
    }

    @Override
    public String getLowerPlayerTitle() {
        return Lang.stats_kill_worst_ranked_title.toString();
    }

    @Override
    public String getHighestItemSubTitle() {
        return Lang.stats_kill_best_ranked_subtitle.toString().replace("%d", this.getHighestPlayerValue() + "");
    }

    @Override
    public String getLowestItemSubTitle() {
        return Lang.stats_kill_worst_ranked_subtitle.toString().replace("%d", this.getLowerPlayerValue() + "");
    }

    @Override
    public Material getHighestPlayerIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public Material getLowestPlayerIcon() {
        return Material.WOODEN_SWORD;
    }

    @Override
    public int getHighestPlayerValue() {
        Pair<Integer, Integer> ratio = this.informationsKills.get(this.getHighestPlayer());
        return ratio.getKey();
    }

    @Override
    public int getLowerPlayerValue() {
        Pair<Integer, Integer> ratio = this.informationsKills.get(this.getLowestPlayer());
        return ratio.getValue();
    }

    @Override
    public boolean isLowestValueRequired() {
        return true;
    }

    @Override
    public boolean isStatUsable() {
        if (this.informationsKills.isEmpty()) {
            return false;
        }
        for (Map.Entry<Player, Pair<Integer, Integer>> infoJoueur : this.informationsKills.entrySet()) {
            if (infoJoueur.getValue().getKey() <= 0) continue;
            return true;
        }
        return false;
    }

    private void enregister(Player joueur, Type type) {
        Pair<Integer, Integer> ratioActuel;
        if (!this.informationsKills.containsKey(joueur)) {
            ratioActuel = new Pair<Integer, Integer>(0, 0);
            this.informationsKills.put(joueur, ratioActuel);
        } else {
            ratioActuel = this.informationsKills.get(joueur);
        }
        if (type == Type.KILL) {
            Pair<Integer, Integer> nouveauRatio = new Pair<Integer, Integer>(ratioActuel.getKey() + 1, ratioActuel.getValue());
            this.informationsKills.replace(joueur, nouveauRatio);
        } else if (type == Type.DEATH) {
            Pair<Integer, Integer> nouveauRatio = new Pair<Integer, Integer>(ratioActuel.getKey(), ratioActuel.getValue() + 1);
            this.informationsKills.replace(joueur, nouveauRatio);
        }
    }

    private static enum Type {
        KILL,
        DEATH;

    }
}

