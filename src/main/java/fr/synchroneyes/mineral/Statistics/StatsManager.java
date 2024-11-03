package fr.synchroneyes.mineral.Statistics;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Statistics.Class.ArenaChestStat;
import fr.synchroneyes.mineral.Statistics.Class.BossKiller;
import fr.synchroneyes.mineral.Statistics.Class.BuilderStat;
import fr.synchroneyes.mineral.Statistics.Class.ChickenKillerStat;
import fr.synchroneyes.mineral.Statistics.Class.KillStat;
import fr.synchroneyes.mineral.Statistics.Class.MeilleurJoueurStat;
import fr.synchroneyes.mineral.Statistics.Class.MinerStat;
import fr.synchroneyes.mineral.Statistics.Class.MonsterKillerStat;
import fr.synchroneyes.mineral.Statistics.Class.MostParachuteHitStat;
import fr.synchroneyes.mineral.Statistics.Class.TalkStat;
import fr.synchroneyes.mineral.Statistics.Class.VilainJoueurStat;
import fr.synchroneyes.mineral.Statistics.Statistic;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatsManager {
    private List<Statistic> availableStats;
    private Game partie;

    public StatsManager(Game game) {
        this.partie = game;
        this.availableStats = new LinkedList<Statistic>();
        this.registerEvents();
    }

    private void registerEvents() {
        this.availableStats.add(new KillStat());
        this.availableStats.add(new ArenaChestStat());
        this.availableStats.add(new MinerStat());
        this.availableStats.add(new BuilderStat());
        this.availableStats.add(new TalkStat());
        this.availableStats.add(new ChickenKillerStat());
        this.availableStats.add(new MonsterKillerStat());
        this.availableStats.add(new MostParachuteHitStat());
        this.availableStats.add(new MeilleurJoueurStat());
        this.availableStats.add(new VilainJoueurStat());
        this.availableStats.add(new BossKiller());
    }

    public void register(Class event, Player joueur, Object valeur) {
        for (Statistic stat : this.availableStats) {
            if (!stat.getClass().equals(event)) continue;
            stat.perform(joueur, valeur);
            return;
        }
    }

    public List<ItemStack> getAllStatsAsItemStack() {
        LinkedList<ItemStack> items = new LinkedList<ItemStack>();
        for (Statistic stat : this.availableStats) {
            items.addAll(stat.toItemStack());
        }
        return items;
    }
}

