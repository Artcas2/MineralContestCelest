package fr.synchroneyes.mineral.Core.Boss;

import fr.synchroneyes.custom_events.MCBossKilledByPlayerEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreBoss;
import fr.synchroneyes.mineral.Statistics.Class.BossKiller;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class Boss {
    private BossBar bossBar;
    protected Mob entity;
    private BukkitTask boucle;
    private int compteur = 1;
    private AutomatedChestManager chestManager;
    protected List<Entity> spawnedEntities;
    protected List<Boss> spawnedBoss = new ArrayList<Boss>();

    public abstract String getName();

    public abstract double getSanteMax();

    public abstract double getDegatsParAttaque();

    public abstract EntityType getMobType();

    public abstract int getRayonDetectionJoueur();

    public abstract void onPlayerTarget(Player var1);

    public abstract List<ItemStack> getKillRewards();

    public abstract boolean shouldEntityGlow();

    public abstract BarColor getBossBarColor();

    public abstract BarStyle getBarStyle();

    public abstract void doMobSpecialAttack();

    public abstract int getSpecialAttackTimer();

    public abstract int getBossBarDetectionRadius();

    public abstract void defineCustomAttributes();

    public abstract void onBossDeath();

    public abstract void onBossSpawn();

    protected abstract void performAnnouncement();

    protected abstract boolean canSpawnMobs();

    public Boss() {
        this.spawnedEntities = new ArrayList<Entity>();
    }

    public abstract void onBossRemove();

    public void spawn(Location position) {
        World monde = position.getWorld();
        if (monde == null) {
            Bukkit.getLogger().severe("Unable to spawn the boss, invalid location given.");
            return;
        }
        Location mobLocation = position.clone();
        mobLocation.setY((double)(position.getBlockY() + 1));
        this.entity = (Mob)monde.spawnEntity(mobLocation, this.getMobType());
        this.entity.setCustomNameVisible(true);
        this.entity.setGlowing(this.shouldEntityGlow());
        this.entity.setCustomName(this.getName());
        this.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.getSanteMax());
        this.entity.setHealth(this.getSanteMax());
        this.entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getDegatsParAttaque());
        if (this.bossBar != null) {
            this.removeBossBar();
        }
        this.bossBar = Bukkit.createBossBar((String)this.getName(), (BarColor)this.getBossBarColor(), (BarStyle)this.getBarStyle(), (BarFlag[])new BarFlag[0]);
        this.entity.setTarget((LivingEntity)this.getNearestPlayer());
        this.entity.setMetadata("boss", (MetadataValue)new FixedMetadataValue((Plugin)mineralcontest.plugin, (Object)true));
        this.startMobTask();
        this.onBossSpawn();
        this.defineCustomAttributes();
    }

    private void removeBossBar() {
        if (this.bossBar == null) {
            return;
        }
        this.bossBar.removeAll();
        this.bossBar.setProgress(0.0);
        this.bossBar.setVisible(false);
        this.bossBar = null;
    }

    private Player getNearestPlayer() {
        if (this.entity == null || this.entity.isDead()) {
            return null;
        }
        int radius = this.getRayonDetectionJoueur();
        List entite_proche = this.entity.getNearbyEntities((double)radius, (double)radius, (double)radius);
        entite_proche.removeIf(entity1 -> !(entity1 instanceof Player));
        entite_proche.removeIf(entity1 -> mineralcontest.getPlayerGame((Player)entity1).isReferee((Player)entity1));
        if (entite_proche.isEmpty()) {
            return null;
        }
        return (Player)entite_proche.get(0);
    }

    private void startMobTask() {
        if (this.boucle != null) {
            this.boucle.cancel();
            this.boucle = null;
            this.removeBossBar();
        }
        this.boucle = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
            Player targeted;
            if (this.entity == null) {
                this.boucle.cancel();
                this.removeBossBar();
                return;
            }
            if (this.entity.isDead()) {
                if (this.entity.getKiller() != null) {
                    Bukkit.getPluginManager().callEvent((Event)new MCBossKilledByPlayerEvent(this, this.entity.getKiller()));
                }
                this.removeBossBar();
                this.spawnMobKillRewards();
                if (this.entity.getKiller() != null) {
                    mineralcontest.broadcastMessage(this.entity.getKiller().getDisplayName() + " a tu\u00e9 " + this.getName());
                }
                this.getChestManager().getGroupe().getGame().getStatsManager().register(BossKiller.class, this.entity.getKiller(), null);
                this.onBossDeath();
                this.boucle.cancel();
                return;
            }
            LivingEntity targetedPlayer = this.entity.getTarget();
            if (targetedPlayer instanceof Player && !(targeted = (Player)targetedPlayer).equals((Object)this.getNearestPlayer())) {
                this.onPlayerTarget(targeted);
                this.entity.setTarget((LivingEntity)this.getNearestPlayer());
            }
            if (this.compteur % (this.getSpecialAttackTimer() * 4) == 0) {
                this.doMobSpecialAttack();
            }
            this.handleCrossBar();
            this.entity.setCustomName(this.getNameWithHealth());
            ++this.compteur;
        }, 0L, 5L);
    }

    private void spawnMobKillRewards() {
        List<ItemStack> items = this.getKillRewards();
        CoffreBoss coffreBoss = new CoffreBoss(items, this.chestManager);
        coffreBoss.setChestLocation(this.entity.getLocation());
        coffreBoss.spawn();
        this.chestManager.addChest(coffreBoss);
    }

    private void handleCrossBar() {
        if (this.entity == null) {
            return;
        }
        World monde = this.entity.getWorld();
        List<Player> joueurs = monde.getPlayers();
        for (Player joueur : joueurs) {
            if (Radius.isBlockInRadius(this.entity.getLocation(), joueur.getLocation(), this.getBossBarDetectionRadius())) {
                this.bossBar.addPlayer(joueur);
                continue;
            }
            this.bossBar.removePlayer(joueur);
        }
        this.bossBar.setProgress(this.entity.getHealth() / this.getSanteMax());
    }

    public void removePlayerBossBar(Player p) {
        if (this.bossBar == null) {
            return;
        }
        this.bossBar.removePlayer(p);
    }

    private String getNameWithHealth() {
        if (this.entity.getHealth() == 0.0) {
            return this.getName();
        }
        return this.getName() + " " + (int)this.entity.getHealth() + ChatColor.RED + "\u2665" + ChatColor.RESET;
    }

    protected void setChestManager(AutomatedChestManager manager) {
        this.chestManager = manager;
    }

    protected AutomatedChestManager getChestManager() {
        return this.chestManager;
    }

    public void onPlayerKilled(Player p) {
    }

    public boolean isThisEntitySpawnedByBoss(Entity spawnedEntity) {
        if (!this.canSpawnMobs()) {
            return false;
        }
        for (Entity e : this.spawnedEntities) {
            if (!spawnedEntity.equals((Object)e)) continue;
            return true;
        }
        for (Boss b : this.spawnedBoss) {
            if (b.entity == null || !b.entity.equals((Object)spawnedEntity)) continue;
            return true;
        }
        return false;
    }

    public void remove() {
        if (this.entity != null) {
            this.entity.setHealth(0.0);
        }
        this.onBossRemove();
        this.boucle.cancel();
    }

    public boolean isAlive() {
        return this.entity != null && !this.entity.isDead();
    }
}

