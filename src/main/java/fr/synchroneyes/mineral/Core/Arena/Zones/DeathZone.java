package fr.synchroneyes.mineral.Core.Arena.Zones;

import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.Referee.Referee;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayer;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathZone {
    ConcurrentLinkedQueue<CouplePlayer> joueurs = new ConcurrentLinkedQueue();
    private int timeInDeathzone = 0;
    private Groupe groupe;

    public DeathZone(Groupe g) {
        this.groupe = g;
        try {
            this.timeInDeathzone = g.getParametresPartie().getCVAR("death_time").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConcurrentLinkedQueue<CouplePlayer> getPlayers() {
        return this.joueurs;
    }

    public CouplePlayer getPlayerInfo(Player p) {
        for (CouplePlayer playerInfo : this.getPlayers()) {
            if (!playerInfo.getJoueur().equals((Object)p)) continue;
            return playerInfo;
        }
        return null;
    }

    public synchronized void reducePlayerTimer() throws Exception {
        if (this.joueurs.size() != 0) {
            for (CouplePlayer joueur : this.joueurs) {
                if (joueur.getJoueur() == null || !joueur.getJoueur().isOnline()) {
                    this.joueurs.remove(joueur);
                    return;
                }
                if (joueur.getValeur() <= 0) {
                    this.libererJoueur(joueur);
                }
                if (joueur.getValeur() >= 1) {
                    joueur.getJoueur().sendTitle(ChatColor.RED + Lang.translate(Lang.deathzone_you_are_dead.toString()), Lang.translate(Lang.deathzone_respawn_in.toString(), joueur.getJoueur()), 0, 20, 0);
                }
                joueur.setValeur(joueur.getValeur() - 1);
                joueur.getJoueur().setFireTicks(0);
            }
        }
    }

    public synchronized int getPlayerDeathTime(Player joueur) {
        if (this.isPlayerDead(joueur)) {
            for (CouplePlayer cp : this.getPlayers()) {
                if (!cp.getJoueur().equals((Object)joueur)) continue;
                return cp.getValeur();
            }
        }
        return 0;
    }

    public synchronized void add(Player joueur) {
        this.timeInDeathzone = this.groupe.getParametresPartie().getCVAR("death_time").getValeurNumerique();
        if (!this.isPlayerDead(joueur)) {
            this.joueurs.add(new CouplePlayer(joueur, this.timeInDeathzone));
        }
        this.applyDeathEffectToPlayer(joueur);
    }

    public synchronized void add(CouplePlayer couplePlayer) throws Exception {
        Player joueur = couplePlayer.getJoueur();
        this.joueurs.add(couplePlayer);
        this.applyDeathEffectToPlayer(joueur);
    }

    private void applyDeathEffectToPlayer(Player joueur) {
        this.timeInDeathzone = this.groupe.getParametresPartie().getCVAR("death_time").getValeurNumerique();
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (partie.isReferee(joueur) && partie.isGameStarted()) {
            joueur.setGameMode(GameMode.SURVIVAL);
            joueur.setFireTicks(0);
            PlayerUtils.teleportPlayer(joueur, partie.groupe.getMonde(), partie.getArene().getCoffre().getLocation());
            return;
        }
        joueur.setGameMode(GameMode.ADVENTURE);
        PlayerUtils.setMaxHealth(joueur);
        joueur.getInventory().clear();
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.deathzone_respawn_in.toString(), joueur));
        PlayerUtils.teleportPlayer(joueur, partie.groupe.getMonde(), partie.getPlayerHouse(joueur).getHouseLocation());
        joueur.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * (this.timeInDeathzone * 3), 1));
    }

    public synchronized boolean isPlayerDead(Player joueur) {
        for (CouplePlayer cp : this.getPlayers()) {
            if (!cp.getJoueur().equals((Object)joueur)) continue;
            return true;
        }
        return false;
    }

    public synchronized void libererJoueur(CouplePlayer DeathZonePlayer) throws Exception {
        if (DeathZonePlayer.getValeur() <= 0) {
            Player joueur = DeathZonePlayer.getJoueur();
            if (!joueur.isOnline()) {
                this.joueurs.remove(DeathZonePlayer);
                return;
            }
            joueur.setGameMode(GameMode.SURVIVAL);
            joueur.setFireTicks(0);
            Game partie = mineralcontest.getPlayerGame(joueur);
            Equipe team = mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur);
            House teamHouse = mineralcontest.getPlayerGame(joueur).getPlayerHouse(joueur);
            if (team == null) {
                PlayerUtils.teleportPlayer(joueur, partie.groupe.getMonde(), partie.getArene().getCoffre().getLocation());
                if (partie.isReferee(joueur)) {
                    joueur.getInventory().clear();
                    joueur.getInventory().setItemInMainHand(Referee.getRefereeItem());
                    joueur.setGameMode(GameMode.CREATIVE);
                } else {
                    mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "Le joueur " + joueur.getDisplayName() + " a \u00e9t\u00e9 TP au centre de l'ar\u00e8ne car il n'a pas d'\u00e9quipe et vient de r\u00e9apparaitre suite \u00e0 une mort", partie.groupe);
                    mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "Le joueur " + joueur.getDisplayName() + " a \u00e9galement \u00e9t\u00e9 mis spectateur. Vous devez changer son gamemode", partie.groupe);
                    joueur.setGameMode(GameMode.SPECTATOR);
                }
            } else {
                joueur.removePotionEffect(PotionEffectType.INVISIBILITY);
                joueur.removePotionEffect(PotionEffectType.BLINDNESS);
                for (PotionEffect potion : joueur.getActivePotionEffects()) {
                    joueur.removePotionEffect(potion.getType());
                }
                joueur.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                joueur.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
                joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            }
            if (!this.joueurs.contains(DeathZonePlayer)) {
                this.remove(joueur);
            } else {
                this.joueurs.remove(DeathZonePlayer);
            }
            PlayerUtils.setMaxHealth(joueur);
            try {
                MCPlayerRespawnEvent respawnEvent = new MCPlayerRespawnEvent(joueur);
                Bukkit.getPluginManager().callEvent((Event)respawnEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!partie.isReferee(joueur)) {
                    this.groupe.getPlayerBaseItem().giveItemsToPlayer(joueur);
                }
                this.groupe.getGame().getPlayerBonusManager().triggerEnabledBonusOnRespawn(joueur);
            } catch (Exception e) {
                mineralcontest.broadcastMessage(mineralcontest.prefixErreur + e.getMessage(), partie.groupe);
                e.printStackTrace();
            }
            DeathZonePlayer.getJoueur().sendTitle(ChatColor.GREEN + Lang.translate(Lang.deathzone_respawned.toString()), "", 1, 40, 1);
        }
    }

    private synchronized void remove(Player joueur) {
        for (CouplePlayer cp : this.getPlayers()) {
            if (!cp.getJoueur().equals((Object)joueur)) continue;
            this.joueurs.remove(cp);
            return;
        }
    }
}

