package fr.synchroneyes.mineral.Core.Boss.BossType;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.DeathAnimations.Animations.HalloweenHurricaneAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class AngryZombie extends Boss {
    private int maxSbire = 5;
    private List<LivingEntity> list_sbire = new ArrayList<LivingEntity>();

    @Override
    public void onBossRemove() {
        for (LivingEntity sbire : this.list_sbire) {
            sbire.remove();
        }
    }

    @Override
    public String getName() {
        return "???";
    }

    @Override
    public double getSanteMax() {
        return 150.0;
    }

    @Override
    public double getDegatsParAttaque() {
        return 10.0;
    }

    @Override
    public EntityType getMobType() {
        return EntityType.ZOMBIE;
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
        return items;
    }

    @Override
    public boolean shouldEntityGlow() {
        return true;
    }

    @Override
    public BarColor getBossBarColor() {
        return BarColor.GREEN;
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SOLID;
    }

    @Override
    public void doMobSpecialAttack() {
        if (this.list_sbire.size() >= this.maxSbire) {
            return;
        }
        int nb_sbire_genere = 3;
        for (int i = 0; i < nb_sbire_genere && this.list_sbire.size() < this.maxSbire; ++i) {
            ZombieVillager zombieVillager = this.addSbire(this.entity.getLocation());
            this.list_sbire.add((LivingEntity)zombieVillager);
            this.spawnedEntities.add(zombieVillager);
        }
        this.entity.getWorld().strikeLightningEffect(this.entity.getLocation());
        this.entity.getWorld().playEffect(this.entity.getLocation(), Effect.END_GATEWAY_SPAWN, 1);
        List<Entity> joueurs = this.entity.getNearbyEntities((double)this.getRayonDetectionJoueur(), (double)this.getRayonDetectionJoueur(), (double)this.getRayonDetectionJoueur());
        joueurs.removeIf(entity1 -> !(entity1 instanceof Player));
        int duree_effet = 5;
        for (Entity joueur : joueurs) {
            Player j = (Player)joueur;
            j.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * duree_effet, 5));
            j.sendTitle(ChatColor.GREEN + this.getName(), this.getRandomInfectionMessage(), 20, 20 * duree_effet / 2, 20);
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
        for (LivingEntity sbire : this.list_sbire) {
            sbire.setHealth(0.0);
        }
        HalloweenHurricaneAnimation animationMort = new HalloweenHurricaneAnimation();
        animationMort.playAnimation((LivingEntity)this.entity);
    }

    @Override
    public void onBossSpawn() {
        if (this.entity instanceof Zombie) {
            Zombie zombie = (Zombie)this.entity;
            zombie.setAdult();
        }
        Groupe groupe = this.getChestManager().getGroupe();
        for (House maison : groupe.getGame().getHouses()) {
            int team_member_cout = maison.getTeam().getJoueurs().size();
            if (team_member_cout <= 0) continue;
            Player joueurAleatoire = maison.getTeam().getJoueurs().get(new Random().nextInt(team_member_cout));
            this.addSbire(joueurAleatoire.getLocation());
        }
        this.entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 99999999, 10));
    }

    @Override
    protected void performAnnouncement() {
    }

    @Override
    protected boolean canSpawnMobs() {
        return true;
    }

    @Override
    public void onPlayerKilled(Player p) {
        HalloweenHurricaneAnimation animationMort = new HalloweenHurricaneAnimation();
        animationMort.playAnimation((LivingEntity)p);
        mineralcontest.broadcastMessage(ChatColor.RED + this.getName() + ChatColor.RESET + ": " + p.getDisplayName() + "a succomb\u00e9 face \u00e0 ma force l\u00e9gendaire. \u00c0 qui le tour?");
    }

    private String getRandomInfectionMessage() {
        String[] messages = new String[]{"Tu me donne envie de vomir", "Ma beaut\u00e9 t'\u00e9bloui?", "Mes copains sont l\u00e0 pour toi", "C'est tout ce que t'as?", "Bats toi comme un homme", "Sombre bloc de bouse", "M\u00eame Herobrine fait mieux que toi"};
        return messages[new Random().nextInt(messages.length)];
    }

    private ZombieVillager addSbire(Location position) {
        final ZombieVillager zombieSbire = (ZombieVillager)this.entity.getWorld().spawnEntity(position, EntityType.ZOMBIE_VILLAGER);
        if (zombieSbire.isBaby()) {
            zombieSbire.setAdult();
        }
        zombieSbire.setCustomNameVisible(true);
        zombieSbire.setCustomName("Sbire ???");
        zombieSbire.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.getSanteMax() / 3.0);
        zombieSbire.setHealth(this.getSanteMax() / 3.0);
        zombieSbire.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getDegatsParAttaque() / 3.0);
        new BukkitRunnable(){

            public void run() {
                if (zombieSbire.isDead()) {
                    this.cancel();
                }
                zombieSbire.setCustomName("Sbire " + (int)zombieSbire.getHealth() + ChatColor.RED + "\u2665" + ChatColor.RESET);
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 5L);
        return zombieSbire;
    }
}

