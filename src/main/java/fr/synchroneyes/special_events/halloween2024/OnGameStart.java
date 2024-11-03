package fr.synchroneyes.special_events.halloween2024;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.special_events.halloween2024.FreezeWorldTime;
import fr.synchroneyes.special_events.halloween2024.game_events.ArenaMonsterEvent;
import fr.synchroneyes.special_events.halloween2024.game_events.FakeChestEvent;
import fr.synchroneyes.special_events.halloween2024.game_events.FightArenaEvent;
import fr.synchroneyes.special_events.halloween2024.game_events.HellParkourEvent;
import fr.synchroneyes.special_events.halloween2024.utils.Screamer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnGameStart implements Listener {
    @EventHandler
    public void OnGameStart(MCGameStartedEvent event) {
        boolean halloweenEnabled = false;
        Game partie = event.getGame();
        GameSettings parametres = partie.groupe.getParametresPartie();
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "Halloween 2024 activ\u00e9!");
        halloweenEnabled = true;
        if (halloweenEnabled) {
            World gameWorld = event.getGame().groupe.getMonde();
            FreezeWorldTime.setFrozenWorld(gameWorld);
            FreezeWorldTime.freezeWorld();
            FakeChestEvent fakeChestEvent = new FakeChestEvent(event.getGame());
            FightArenaEvent fightArenaEvent = new FightArenaEvent(event.getGame());
            HellParkourEvent hellParkourEvent = new HellParkourEvent(event.getGame());
            ArenaMonsterEvent arenaMonsterEvent = new ArenaMonsterEvent(event.getGame());
            this.sendDelayedTitleToEveryOne(ChatColor.WHITE + "\u2620 " + ChatColor.RED + "Mineral" + ChatColor.RED + " Contest" + ChatColor.WHITE + " \u2620", "Mode Halloween " + ChatColor.GREEN + "activ\u00e9!", 5, 5, true, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playThunderSound", partie), 100L);
            Bukkit.getServer().getScheduler().runTaskLater((Plugin)mineralcontest.plugin, fightArenaEvent::execute, 100L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playCreeperSound", partie), 4800L);
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "N'ayez pas peur.. ce n'\u00e9tait que le d\u00e9but", 5, 120, false, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playWarden", partie), 6000L);
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "Vous avez l'air trop en forme...", 5, 510, false, event.getGame());
            Bukkit.getServer().getScheduler().runTaskLater((Plugin)mineralcontest.plugin, hellParkourEvent::execute, 10800L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playWardenSound", partie), 16800L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playCreeper", partie), 9600L);
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "Peur d'exploser? :)", 5, 1150, false, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playWardenSound", partie), 16800L);
            Bukkit.getServer().getScheduler().runTaskLater((Plugin)mineralcontest.plugin, fakeChestEvent::execute, 33600L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playAnvilRain", partie), 37200L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playMonsterRoulette", partie), 43200L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playZombieSound", partie), 48000L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> Screamer.playEffectToAllPlayers("playEndermanSound", partie), 48000L);
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "Ne vous inquietez pas, j'arrive ...", 5, 3090, true, event.getGame());
            Bukkit.getServer().getScheduler().runTaskLater((Plugin)mineralcontest.plugin, arenaMonsterEvent::execute, 62400L);
        }
    }

    private void sendDelayedTitleToEveryOne(String title, String message, int duree_seconde, int duree_avant_annonce, boolean blindPlayers, Game game) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            for (Player joueur : game.groupe.getPlayers()) {
                joueur.sendTitle(title, message, 20, 20 * duree_seconde, 20);
                if (!blindPlayers) continue;
                joueur.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (duree_seconde + 2), 5));
            }
        }, (long)(duree_avant_annonce * 20));
    }
}

