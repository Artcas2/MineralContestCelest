package fr.synchroneyes.mineral.Core.Boss.BossType;

import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.DeathAnimations.Animations.HalloweenHurricaneAnimation;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArenaMonster extends Boss {
    private Game partie;

    public ArenaMonster(Game partie) {
        this.partie = partie;
    }

    @Override
    public String getName() {
        return "Commandant de l'enfer";
    }

    @Override
    public double getSanteMax() {
        return 150.0;
    }

    @Override
    public double getDegatsParAttaque() {
        return 5.0;
    }

    @Override
    public EntityType getMobType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public int getRayonDetectionJoueur() {
        return 5;
    }

    @Override
    public void onPlayerTarget(Player targetedPlayer) {
    }

    @Override
    public List<ItemStack> getKillRewards() {
        int i;
        LinkedList<ItemStack> items = new LinkedList<ItemStack>();
        for (i = 0; i < 5; ++i) {
            items.add(new ItemStack(Material.EMERALD, 1));
        }
        for (i = 0; i < 15; ++i) {
            items.add(new ItemStack(Material.DIAMOND, 1));
        }
        for (i = 0; i < 4; ++i) {
            items.add(new ItemStack(Material.GOLD_INGOT, 5));
        }
        for (i = 0; i < 3; ++i) {
            items.add(new ItemStack(Material.IRON_INGOT, 10));
        }
        for (i = 0; i < 3; ++i) {
            items.add(new ItemStack(Material.REDSTONE, 20));
        }
        return items;
    }

    @Override
    public boolean shouldEntityGlow() {
        return true;
    }

    @Override
    public BarColor getBossBarColor() {
        return BarColor.PURPLE;
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SEGMENTED_20;
    }

    @Override
    public void doMobSpecialAttack() {
        for (Player joueur : this.partie.groupe.getPlayers()) {
            if (!Radius.isBlockInRadius(this.entity.getLocation(), joueur.getLocation(), 5)) continue;
            joueur.sendMessage(mineralcontest.prefixPrive + this.getName() + ": Ceci n'est qu'un avant-go\u00fbt de ma puissance ... Souffrez !");
            joueur.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
            joueur.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 1));
            joueur.getWorld().strikeLightningEffect(joueur.getLocation());
        }
    }

    @Override
    public int getSpecialAttackTimer() {
        return 15;
    }

    @Override
    public int getBossBarDetectionRadius() {
        return 20;
    }

    @Override
    public void defineCustomAttributes() {
        this.entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(2.0);
    }

    @Override
    public void onBossDeath() {
        HalloweenHurricaneAnimation animationMort = new HalloweenHurricaneAnimation();
        animationMort.playAnimation((LivingEntity)this.entity);
    }

    @Override
    public void onBossSpawn() {
        this.entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999999, 10));
    }

    @Override
    protected void performAnnouncement() {
    }

    @Override
    protected boolean canSpawnMobs() {
        return false;
    }

    @Override
    public void onBossRemove() {
    }
}

