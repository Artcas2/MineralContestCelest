package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Statistics.Class.KillStat;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player joueur = (Player)event.getEntity();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (!mineralcontest.isInAMineralContestWorld(joueur)) {
                return;
            }
            if (mineralcontest.isInMineralContestHub(joueur) || playerGroup == null || !playerGroup.getGame().isGameStarted() || playerGroup.getGame().isPreGame() || playerGroup.getGame().isGamePaused()) {
                event.setCancelled(true);
                return;
            }
            if (event instanceof EntityDamageByEntityEvent) {
                Player tireur;
                Equipe equipeTireur;
                Arrow fleche;
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)event;
                if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    Material[] items_nerf;
                    Player attaquant = (Player)entityDamageByEntityEvent.getDamager();
                    for (Material item : items_nerf = new Material[]{Material.WOODEN_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.STONE_AXE}) {
                        if (attaquant.getInventory().getItemInMainHand().getType() != item) continue;
                        event.setDamage(event.getDamage() / 2.0);
                        break;
                    }
                    if (playerGroup.getPlayerTeam(attaquant) == null) {
                        event.setCancelled(true);
                        return;
                    }
                    if (playerGroup.getPlayerTeam(joueur).equals(playerGroup.getPlayerTeam(attaquant)) && playerGroup.getParametresPartie().getCVAR("mp_enable_friendly_fire").getValeurNumerique() == 0) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (entityDamageByEntityEvent.getDamager() instanceof Arrow && (fleche = (Arrow)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player && (equipeTireur = playerGroup.getPlayerTeam(tireur = (Player)fleche.getShooter())) != null && equipeTireur.equals(playerGroup.getPlayerTeam(joueur)) && playerGroup.getParametresPartie().getCVAR("mp_enable_friendly_fire").getValeurNumerique() == 0) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getCause().equals((Object)EntityDamageEvent.DamageCause.LIGHTNING)) {
                    event.setCancelled(true);
                    return;
                }
                if ((entityDamageByEntityEvent.getDamager() instanceof Player || entityDamageByEntityEvent.getDamager() instanceof Arrow && ((Arrow)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player) && joueur.equals((Object)playerGroup.getGame().getArene().getCoffre().getOpeningPlayer())) {
                    playerGroup.getGame().getArene().getCoffre().closeInventory();
                    joueur.closeInventory();
                }
            }
        }
    }

    private void registerKill(Player dead, Player attacker) {
        if (mineralcontest.getPlayerGame(dead) == null) {
            return;
        }
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_killed.toString(), dead, attacker), mineralcontest.getPlayerGame((Player)dead).groupe);
        Game partie = mineralcontest.getPlayerGame(dead);
        if (partie != null && partie.isGameStarted()) {
            partie.getStatsManager().register(KillStat.class, attacker, dead);
        }
        if (partie != null) {
            partie.getPlayerBonusManager().triggerEnabledBonusOnPlayerKillerKilled(dead);
        }
        PlayerUtils.killPlayer(dead);
        attacker.playSound(attacker.getLocation(), Sound.ENTITY_CHICKEN_DEATH, 1.0f, 1.0f);
        ++mineralcontest.getPlayerGame((Player)dead).killCounter;
    }

    private void registerPlayerSuicide(Player dead) {
        Game partie = mineralcontest.getPlayerGame(dead);
        if (partie == null) {
            return;
        }
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_died.toString(), dead), partie.groupe);
        if (partie.isGameStarted()) {
            partie.getStatsManager().register(KillStat.class, dead, dead);
        }
        PlayerUtils.killPlayer(dead);
    }

    private void registerPlayerDeadByEntity(Player dead) {
        Game partie = mineralcontest.getPlayerGame(dead);
        if (partie == null) {
            return;
        }
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_died.toString(), dead), partie.groupe);
        if (partie.isGameStarted()) {
            partie.getStatsManager().register(KillStat.class, dead, dead);
        }
        PlayerUtils.killPlayer(dead);
    }
}

