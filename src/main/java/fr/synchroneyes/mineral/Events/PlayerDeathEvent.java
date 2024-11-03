package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Kits.Classes.Mineur;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Statistics.Class.KillStat;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player joueur = event.getEntity();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Game partie = mineralcontest.getPlayerGame(joueur);
            if (partie == null) {
                return;
            }
            if (partie.isGameStarted()) {
                if (joueur.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)joueur.getLastDamageCause();
                    if (partie.getBossManager().wasPlayerKilledByBoss(entityDamageByEntityEvent.getDamager())) {
                        partie.getBossManager().fireBossMadeKill(entityDamageByEntityEvent.getDamager(), joueur);
                    }
                }
                if (joueur.getKiller() == null) {
                    event.setDeathMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_died.toString(), joueur));
                } else {
                    event.setDeathMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_killed.toString(), joueur, joueur.getKiller()));
                }
                int radiusProtection = partie.groupe.getParametresPartie().getCVAR("protected_zone_area_radius").getValeurNumerique();
                Location arenaCenter = partie.getArene().getCoffre().getLocation();
                if (partie.groupe.getParametresPartie().getCVAR("drop_chest_on_death").getValeurNumerique() == 1 && !Radius.isBlockInRadius(arenaCenter, joueur.getLocation(), radiusProtection)) {
                    event.getDrops().clear();
                }
                event.getDrops().removeIf(item -> item.isSimilar(Mineur.getBarrierItem()));
                event.getDrops().removeIf(item -> item.getType() == Material.POTION);
                event.getDrops().removeIf(ShopManager::isAnShopItem);
                LinkedList<Material> item_a_drop = new LinkedList<Material>();
                item_a_drop.add(Material.IRON_INGOT);
                item_a_drop.add(Material.GOLD_INGOT);
                item_a_drop.add(Material.DIAMOND);
                item_a_drop.add(Material.EMERALD);
                item_a_drop.add(Material.IRON_ORE);
                item_a_drop.add(Material.GOLD_ORE);
                item_a_drop.add(Material.DIAMOND_ORE);
                item_a_drop.add(Material.EMERALD_ORE);
                item_a_drop.add(Material.IRON_ORE);
                item_a_drop.add(Material.GOLD_ORE);
                item_a_drop.add(Material.EMERALD_ORE);
                item_a_drop.add(Material.DIAMOND_ORE);
                item_a_drop.add(Material.POTION);
                item_a_drop.add(Material.REDSTONE);
                item_a_drop.add(Material.ENCHANTED_BOOK);
                switch (partie.groupe.getParametresPartie().getCVAR("mp_enable_item_drop").getValeurNumerique()) {
                    case 0: {
                        event.getDrops().clear();
                        break;
                    }
                    case 1: {
                        event.getDrops().removeIf(item -> !item_a_drop.contains(item.getType()));
                    }
                }
                MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
                if (mcPlayer != null) {
                    mcPlayer.cancelDeathEvent();
                }
                Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> partie.getArene().getDeathZone().add(joueur), 1L);
                PlayerDeathByPlayerEvent event1 = new PlayerDeathByPlayerEvent(joueur, joueur.getKiller(), partie);
                Bukkit.getPluginManager().callEvent((Event)event1);
                int nombre_points_bonus = partie.groupe.getParametresPartie().getCVAR("points_per_kill").getValeurNumerique();
                if (nombre_points_bonus > 0 && event.getEntity().getKiller() != null && !event.getEntity().getKiller().equals((Object)event.getEntity()) && partie.getPlayerTeam(joueur) != partie.getPlayerTeam(joueur.getKiller())) {
                    Equipe equipe_tueuse = partie.getPlayerTeam(joueur.getKiller());
                    int teamScore = equipe_tueuse.getScore();
                    equipe_tueuse.setScore(teamScore += nombre_points_bonus);
                    equipe_tueuse.sendMessage(mineralcontest.prefixTeamChat + "Vous avez re\u00e7u " + nombre_points_bonus + " points gr\u00e2ce au kill fait par " + joueur.getKiller().getDisplayName());
                }
                if (joueur.getKiller() != null) {
                    partie.getStatsManager().register(KillStat.class, joueur, joueur.getKiller());
                } else {
                    partie.getStatsManager().register(KillStat.class, joueur, joueur);
                }
            }
        }
    }
}

