package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCBossKilledByPlayerEvent;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Statistics.Class.ChickenKillerStat;
import fr.synchroneyes.mineral.Statistics.Class.MonsterKillerStat;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Range;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EntityDeathEvent implements Listener {
    @EventHandler
    public void OnEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        if (mineralcontest.isAMineralContestWorld(event.getEntity().getWorld())) {
            Player tueur;
            Game partie = mineralcontest.getWorldGame(event.getEntity().getWorld());
            event.getEntity();
            if (partie != null && partie.isGameStarted()) {
                if (partie.getBossManager().isThisEntityABoss(event.getEntity())) {
                    Boss boss = partie.getBossManager().toBoss((Entity)event.getEntity());
                    MCBossKilledByPlayerEvent event1 = new MCBossKilledByPlayerEvent(boss, event.getEntity().getKiller());
                    Bukkit.getPluginManager().callEvent((Event)event1);
                }
                if (partie.getArene().chickenWaves.isFromChickenWave(event.getEntity())) {
                    LivingEntity poulet = event.getEntity();
                    Player tueur2 = poulet.getKiller();
                    event.getDrops().clear();
                    Range[] items = new Range[]{new Range(Material.IRON_INGOT, 0, 75), new Range(Material.GOLD_INGOT, 75, 95), new Range(Material.DIAMOND, 95, 98), new Range(Material.EMERALD, 98, 100)};
                    GameSettings settings = partie.groupe.getParametresPartie();
                    try {
                        int min = settings.getCVAR("chicken_spawn_min_item_count").getValeurNumerique();
                        int max = settings.getCVAR("chicken_spawn_max_item_count").getValeurNumerique();
                        Random random = new Random();
                        int nombre = random.nextInt(max - min - 1) + min;
                        for (int i = 0; i < nombre; ++i) {
                            Material droppedItem = Range.getInsideRange(items, random.nextInt(100));
                            GameLogger.addLog(new Log("chicken_drop", "Chicken dropped 1x " + droppedItem.toString() + "", "chicken_killed"));
                            event.getDrops().add(new ItemStack(droppedItem, 1));
                        }
                        if (tueur2 != null) {
                            partie.getStatsManager().register(ChickenKillerStat.class, tueur2, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (event.getEntity() instanceof Monster && partie != null && partie.isGameStarted() && (tueur = event.getEntity().getKiller()) != null) {
                partie.getStatsManager().register(MonsterKillerStat.class, tueur, null);
            }
        }
    }
}

