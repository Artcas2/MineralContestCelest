package fr.synchroneyes.halloween_event;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.halloween_event.FreezeWorldTime;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.DeathAnimations.Animations.GroundFreezingAnimation;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnGameStart implements Listener {
    @EventHandler
    public void OnGameStart(MCGameStartedEvent event) {
        boolean halloweenEnabled = false;
        Game partie = event.getGame();
        GameSettings parametres = partie.groupe.getParametresPartie();
        halloweenEnabled = parametres.getCVAR("enable_halloween_event").getValeurNumerique() == 1;
        halloweenEnabled = false;
        if (halloweenEnabled) {
            String unknownName = ChatColor.RED + "???";
            World gameWorld = event.getGame().groupe.getMonde();
            FreezeWorldTime.setFrozenWorld(gameWorld);
            FreezeWorldTime.freezeWorld();
            this.sendDelayedTitleToEveryOne(ChatColor.WHITE + "\u2620 " + ChatColor.RED + "Mineral" + ChatColor.RED + " Contest" + ChatColor.WHITE + " \u2620", "Mode Halloween " + ChatColor.GREEN + "activ\u00e9!", 5, 5, true, event.getGame());
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "Je suis pr\u00e9sent pour vous jouer de mauvais tours...", 5, 23, true, event.getGame());
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "Je viens de tuer le soleil ...", 5, 37, true, event.getGame());
            this.sendDelayedTitleToEveryOne(ChatColor.RED + "???", "N'ayez pas peur ...", 5, 300, true, event.getGame());
            boolean finalHalloweenEnabled = halloweenEnabled;
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    joueur.playSound(joueur.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.8f, 1.0f);
                }
            }, 6000L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    joueur.playSound(joueur.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.8f, 1.0f);
                    joueur.sendTitle(ChatColor.GOLD + "???: ", ChatColor.BLUE + " Il fait un peu froid non ?", 20, 100, 20);
                    GroundFreezingAnimation animationMort = new GroundFreezingAnimation();
                    ((DeathAnimation)animationMort).playAnimation((LivingEntity)joueur);
                }
            }, 12000L);
            this.sendDelayedTitleToEveryOne(unknownName, "Attention o\u00f9 vous marchez..", 5, 1020, true, partie);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    WitherSkeleton babyZombie = (WitherSkeleton)joueur.getWorld().spawn(joueur.getLocation(), WitherSkeleton.class);
                    babyZombie.setCustomNameVisible(true);
                    babyZombie.setCustomName("Luss");
                    babyZombie.setHealth(1.0);
                    babyZombie.setAI(false);
                }
            }, 20400L);
            this.sendDelayedTitleToEveryOne(unknownName, "Nourissez vous mes petits.", 5, 1500, true, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    Zombie babyZombie = (Zombie)joueur.getWorld().spawn(joueur.getLocation(), Zombie.class);
                    babyZombie.setBaby();
                    babyZombie.setCustomNameVisible(true);
                    babyZombie.setCustomName("Pgjgj");
                    babyZombie.setHealth(1.0);
                    babyZombie.setAI(true);
                }
            }, 30000L);
            this.sendDelayedTitleToEveryOne(unknownName, "Besoin d'un opticien?", 5, 2040, false, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    ItemStack old_casque = joueur.getInventory().getHelmet();
                    ItemStack casque = null;
                    if (old_casque != null) {
                        casque = new ItemStack(old_casque.getType());
                        casque.setData(old_casque.getData());
                        casque.setItemMeta(old_casque.getItemMeta());
                        casque.setDurability(casque.getDurability());
                    }
                    ItemStack casque_halloween = new ItemStack(Material.CARVED_PUMPKIN);
                    joueur.getInventory().setHelmet(casque_halloween);
                    ItemStack finalCasque = casque;
                    Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> joueur.getInventory().setHelmet(finalCasque), 200L);
                }
            }, 40800L);
            this.sendDelayedTitleToEveryOne(unknownName, "Bon courage avec mon ami...", 5, 2640, false, event.getGame());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                if (parametres.getCVAR("enable_halloween_event").getValeurNumerique() != 1) {
                    return;
                }
                for (Player joueur : partie.groupe.getPlayers()) {
                    Enderman enderman = (Enderman)joueur.getWorld().spawn(joueur.getLocation(), Enderman.class);
                    enderman.setAI(false);
                    enderman.setCustomNameVisible(true);
                    enderman.setCustomName("Vezzen_");
                }
            }, 52800L);
        }
    }

    private void sendDelayedTitleToEveryOne(String title, String message, int duree_seconde, int duree_avant_annonce, boolean blindPlayers, Game game) {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            boolean halloweenEnabled;
            GameSettings parametres = game.groupe.getParametresPartie();
            boolean bl = halloweenEnabled = parametres.getCVAR("enable_halloween_event").getValeurNumerique() == 1;
            if (!halloweenEnabled) {
                return;
            }
            for (Player joueur : game.groupe.getPlayers()) {
                joueur.sendTitle(title, message, 20, 20 * duree_seconde, 20);
                if (!blindPlayers) continue;
                joueur.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * (duree_seconde + 2), 5));
            }
        }, (long)(duree_avant_annonce * 20));
    }
}

