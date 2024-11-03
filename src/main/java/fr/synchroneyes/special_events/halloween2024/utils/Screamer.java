package fr.synchroneyes.special_events.halloween2024.utils;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.DeathAnimations.Animations.GroundFreezingAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class Screamer {
    public static void playWarden(Player player) {
        player.playSound(player.getLocation(), "minecraft:entity.enderman.scream", 1.0f, 1.0f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        player.sendTitle("\u00a74\u00a7l???", "\u00a7c\u00a7lAttention \u00e0 toi...", 20, 20, 20);
        Location playerLocation = player.getLocation();
        player.getWorld().strikeLightningEffect(playerLocation);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1000));
        Location spawnLocation = player.getLocation().add(player.getLocation().getDirection().multiply(4));
        Warden warden = (Warden)player.getWorld().spawnEntity(spawnLocation, EntityType.WARDEN);
        warden.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10));
        warden.setAI(false);
        warden.setTarget((LivingEntity)player);
        GroundFreezingAnimation animationMort = new GroundFreezingAnimation();
        animationMort.setSendNotification(false);
        animationMort.playAnimation((LivingEntity)player);
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> ((Warden)warden).remove(), 100L);
    }

    public static void playCreeper(Player player) {
        player.playSound(player.getLocation(), "minecraft:entity.enderman.scream", 1.0f, 1.0f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        player.sendTitle("\u00a74\u00a7l???", "\u00a7c\u00a7lMmmh, \u00e7a sent la poudre non?", 20, 20, 20);
        Location playerLocation = player.getLocation();
        for (int i = 0; i < 10; ++i) {
            double angle = Math.PI * 2 * (double)i / 10.0;
            double xOffset = Math.cos(angle) * 2.0;
            double zOffset = Math.sin(angle) * 2.0;
            Location spawnLocation = playerLocation.clone().add(xOffset, 0.0, zOffset);
            Creeper creeper = (Creeper)player.getWorld().spawnEntity(spawnLocation, EntityType.CREEPER);
            creeper.setMaxFuseTicks(1200);
            creeper.setFuseTicks(200);
            creeper.ignite();
            creeper.setExplosionRadius(0);
            creeper.setAI(false);
            creeper.setGlowing(true);
            creeper.setTarget((LivingEntity)player);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> ((Creeper)creeper).remove(), 100L);
        }
    }

    public static void playAnvilRain(Player player) {
        LinkedList<FallingBlock> anvilList = new LinkedList<>();
        int radius = 10;
        int duration = 200;
        player.sendTitle("\u00a74\u00a7l???", "\u00a7c\u00a7lAttention \u00e0 la pluie...", 20, 20, 20);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
            Location playerLocation = player.getLocation();
            for (int i = 0; i < 100; ++i) {
                double angle = Math.PI * 2 * Math.random();
                double distance = Math.random() * (double)radius;
                double xOffset = Math.cos(angle) * distance;
                double zOffset = Math.sin(angle) * distance;
                Location spawnLocation = playerLocation.clone().add(xOffset, 30.0, zOffset);
                FallingBlock anvil = player.getWorld().spawnFallingBlock(spawnLocation, Material.ANVIL.createBlockData());
                anvil.setDropItem(false);
                anvil.setHurtEntities(false);
                anvil.setVisualFire(true);
                anvilList.add(anvil);
                anvil.setMetadata("anvilRain", (MetadataValue)new FixedMetadataValue((Plugin)mineralcontest.plugin, (Object)true));
            }
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            task.cancel();
            for (FallingBlock anvil : anvilList) {
                if (anvil.isDead() && anvil.hasMetadata("anvilRain")) {
                    anvil.getLocation().getBlock().setType(Material.AIR);
                    continue;
                }
                anvil.remove();
            }
        }, (long)duration);
    }

    public static void playMonsterRoulette(final Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        player.sendTitle("\u00a74\u00a7l???", "\u00a7c\u00a7lAttention \u00e0 la roulette...", 20, 20, 20);
        final List<EntityType> monsters = Screamer.toList(EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SKELETON, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WARDEN, EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKELETON);
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, new Runnable(){
                private int ticks = 0;
                private Entity currentMonster = null;

                @Override
                public void run() {
                    if (this.ticks >= 100) {
                        if (this.currentMonster != null) {
                            this.currentMonster.remove();
                        }
                        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                            EntityType finalMonster = (EntityType)Screamer.toList(new EntityType[]{EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER}).get((int)(Math.random() * 3.0));
                            player.getWorld().spawnEntity(player.getLocation().add(player.getLocation().getDirection().multiply(2)).add(0.0, 1.0, 0.0), finalMonster);
                        }, 1L);
                        return;
                    }
                    if (this.currentMonster != null) {
                        this.currentMonster.remove();
                    }
                    EntityType monsterType = (EntityType)monsters.get((int)(Math.random() * (double)monsters.size()));
                    this.currentMonster = player.getWorld().spawnEntity(player.getLocation().add(player.getLocation().getDirection().multiply(2)).add(0.0, 1.0, 0.0), monsterType);
                    ((Monster)this.currentMonster).setAI(false);
                    ((Monster)this.currentMonster).setTarget((LivingEntity)player);
                    this.ticks += 5;
                }
            }, 0L, 5L);
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                player.removePotionEffect(PotionEffectType.SLOW);
                task.cancel();
            }, 100L);
        }, 0L);
    }

    private static List<EntityType> toList(EntityType ... types) {
        ArrayList<EntityType> list = new ArrayList<EntityType>();
        list.addAll(Arrays.asList(types));
        return list;
    }

    public static void playCreeperSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
    }

    public static void playZombieSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 1.0f);
    }

    public static void playWardenSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_AMBIENT, 1.0f, 1.0f);
    }

    public static void playThunderSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
    }

    public static void playEndermanSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 1.0f);
    }

    public static void playEffectToAllPlayers(String effectName, Game partie) {
        try {
            Method method = Screamer.class.getMethod(effectName, Player.class);
            for (Player joueur : partie.groupe.getPlayers()) {
                method.invoke(null, joueur);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

