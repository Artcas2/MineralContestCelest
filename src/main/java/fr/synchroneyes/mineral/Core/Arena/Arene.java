package fr.synchroneyes.mineral.Core.Arena;

import fr.synchroneyes.custom_events.MCArenaChestSpawnEvent;
import fr.synchroneyes.custom_events.MCArenaChestTickEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Arena.Zones.DeathZone;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Arene {
    private Location teleportSpawn;
    private AutomatedChestAnimation coffreArene;
    private boolean allowTeleport;
    private DeathZone deathZone;
    private int MAX_TIME_BETWEEN_CHEST = 0;
    private int MIN_TIME_BETWEEN_CHEST = 0;
    private int TIME_BEFORE_CHEST = 0;
    private int TIMELEFT_REQUIRED_BEFORE_WARNING = 0;
    private double TELEPORT_TIME_LEFT = 0.0;
    private double TELEPORT_TIME_LEFT_VAR = 0.0;
    private boolean CHEST_SPAWNED = false;
    private boolean CHEST_INITIALIZED = false;
    public boolean CHEST_USED = false;
    public int arenaRadius = 60;
    private BossBar teleportStatusBar;
    public Groupe groupe;
    public ChickenWaves chickenWaves;
    private List<Equipe> teamsToNotify;
    private List<Equipe> teamsToAutomaticallyTeleport;
    private List<Equipe> teamsToSingleTeleport;

    public boolean isChestSpawned() {
        return this.CHEST_SPAWNED;
    }

    public void setChestSpawned(boolean CHEST_SPAWNED) {
        this.CHEST_SPAWNED = CHEST_SPAWNED;
    }

    public Arene(Groupe g) {
        this.groupe = g;
        this.deathZone = new DeathZone(g);
        this.chickenWaves = new ChickenWaves(this);
        this.teamsToNotify = new LinkedList<Equipe>();
        this.teamsToAutomaticallyTeleport = new LinkedList<Equipe>();
        this.teamsToSingleTeleport = new LinkedList<Equipe>();
        this.coffreArene = new CoffreArene(this.groupe.getAutomatedChestManager(), this);
        try {
            this.MAX_TIME_BETWEEN_CHEST = g.getParametresPartie().getCVAR("max_time_between_chests").getValeurNumerique();
            this.MIN_TIME_BETWEEN_CHEST = g.getParametresPartie().getCVAR("min_time_between_chests").getValeurNumerique();
            this.TELEPORT_TIME_LEFT_VAR = this.TELEPORT_TIME_LEFT = (double)g.getParametresPartie().getCVAR("max_teleport_time").getValeurNumerique();
            this.TIMELEFT_REQUIRED_BEFORE_WARNING = g.getParametresPartie().getCVAR("arena_warn_chest_time").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.MAX_TIME_BETWEEN_CHEST < this.MIN_TIME_BETWEEN_CHEST) {
            int tmp = this.MIN_TIME_BETWEEN_CHEST;
            this.MIN_TIME_BETWEEN_CHEST = this.MAX_TIME_BETWEEN_CHEST;
            this.MAX_TIME_BETWEEN_CHEST = tmp;
        }
    }

    public void addTeamToNotify(Equipe equipe) {
        if (!this.teamsToNotify.contains(equipe)) {
            this.teamsToNotify.add(equipe);
        }
    }

    public void addTeamToAutomatedTeleport(Equipe equipe) {
        if (!this.teamsToAutomaticallyTeleport.contains(equipe)) {
            this.teamsToAutomaticallyTeleport.add(equipe);
        }
    }

    public void addTeamToSinglePlayerTeleport(Equipe equipe) {
        if (!this.teamsToSingleTeleport.contains(equipe)) {
            this.teamsToSingleTeleport.add(equipe);
        }
    }

    public void clearSingleTeleportTeams() {
        this.teamsToSingleTeleport.clear();
    }

    public Location getTeleportSpawn() {
        return this.teleportSpawn;
    }

    public DeathZone getDeathZone() {
        return this.deathZone;
    }

    public void clear() {
        if (this.coffreArene != null) {
            this.coffreArene.getInventory().clear();
        }
        this.removePlayerTeleportBar();
        this.chickenWaves.setEnabled(false);
    }

    public void generateTimeBetweenChest() {
        try {
            this.MAX_TIME_BETWEEN_CHEST = this.groupe.getParametresPartie().getCVAR("max_time_between_chests").getValeurNumerique();
            this.MIN_TIME_BETWEEN_CHEST = this.groupe.getParametresPartie().getCVAR("min_time_between_chests").getValeurNumerique();
            this.TIMELEFT_REQUIRED_BEFORE_WARNING = this.groupe.getParametresPartie().getCVAR("arena_warn_chest_time").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.MAX_TIME_BETWEEN_CHEST < this.MIN_TIME_BETWEEN_CHEST) {
            int tmp = this.MIN_TIME_BETWEEN_CHEST;
            this.MIN_TIME_BETWEEN_CHEST = this.MAX_TIME_BETWEEN_CHEST;
            this.MAX_TIME_BETWEEN_CHEST = tmp;
        }
        int time = (int)(Math.random() * (double)(this.MAX_TIME_BETWEEN_CHEST - this.MIN_TIME_BETWEEN_CHEST + 1) + (double)this.MIN_TIME_BETWEEN_CHEST);
        time *= 60;
        this.TIME_BEFORE_CHEST = time += (int)(Math.random() * 59.0 + 1.0);
        this.TELEPORT_TIME_LEFT = this.TELEPORT_TIME_LEFT_VAR;
        this.CHEST_INITIALIZED = true;
    }

    public void startAutoMobKill() {
        new BukkitRunnable(){

            public void run() {
                List<Entity> list_entity = Arene.this.groupe.getMonde().getEntities();
                list_entity.removeIf(entite -> !(entite instanceof Monster));
                list_entity.removeIf(entite -> !Radius.isBlockInRadius(Arene.this.coffreArene.getLocation(), entite.getLocation(), Arene.this.groupe.getParametresPartie().getCVAR("protected_zone_area_radius").getValeurNumerique()));
                if (Arene.this.groupe.getParametresPartie().getCVAR("enable_monster_in_protected_zone").getValeurNumerique() != 1) {
                    for (Entity entite2 : list_entity) {
                        if (Arene.this.groupe.getGame().getArene().chickenWaves.isFromChickenWave((LivingEntity)entite2)) {
                            return;
                        }
                        if (entite2 instanceof Bat) {
                            return;
                        }
                        if (Arene.this.groupe.getGame().getBossManager().isThisEntityABoss((LivingEntity)entite2)) {
                            return;
                        }
                        if (Arene.this.groupe.getGame().getBossManager().isThisEntitySpawnedByBoss(entite2)) {
                            return;
                        }
                        Bukkit.getLogger().info("Removing entity: " + entite2.getName());
                        entite2.remove();
                    }
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 40L);
    }

    private void notifyTeams() {
        for (Equipe equipe : this.teamsToNotify) {
            equipe.sendMessage(mineralcontest.prefixTeamChat + Lang.translate(Lang.arena_chest_will_spawn_in.toString(), this.groupe));
        }
        this.teamsToNotify.clear();
    }

    public void automaticallyTeleportTeams() {
        for (Equipe equipe : this.teamsToAutomaticallyTeleport) {
            for (Player membre : equipe.getJoueurs()) {
                PlayerUtils.teleportPlayer(membre, this.getTeleportSpawn().getWorld(), this.getTeleportSpawn());
            }
        }
        this.teamsToAutomaticallyTeleport.clear();
    }

    public void startArena() {
        this.generateTimeBetweenChest();
        new BukkitRunnable(){

            public void run() {
                if (Arene.this.groupe.getGame().isGameStarted() && !Arene.this.groupe.getGame().isGamePaused()) {
                    try {
                        if (Arene.this.CHEST_INITIALIZED) {
                            if (Arene.this.TIME_BEFORE_CHEST > 0) {
                                MCArenaChestTickEvent mcArenaChestTickEvent = new MCArenaChestTickEvent(Arene.this.TIME_BEFORE_CHEST, Arene.this.groupe.getGame());
                                Bukkit.getPluginManager().callEvent((Event)mcArenaChestTickEvent);
                                Arene.this.TIME_BEFORE_CHEST--;
                                if (Arene.this.TIME_BEFORE_CHEST == Arene.this.TIMELEFT_REQUIRED_BEFORE_WARNING) {
                                    Arene.this.notifyTeams();
                                }
                            } else {
                                Arene.this.coffreArene.getLocation().getBlock().setType(Material.AIR);
                                Arene.this.coffreArene.spawn();
                                MCArenaChestSpawnEvent mcArenaChestSpawnEvent = new MCArenaChestSpawnEvent(Arene.this.groupe.getGame());
                                Bukkit.getPluginManager().callEvent((Event)mcArenaChestSpawnEvent);
                            }
                        }
                        if (Arene.this.CHEST_USED) {
                            Arene.this.CHEST_SPAWNED = false;
                            Arene.this.disableTeleport();
                            Arene.this.TELEPORT_TIME_LEFT = Arene.this.TELEPORT_TIME_LEFT_VAR;
                            Arene.this.CHEST_USED = false;
                        }
                        if (Arene.this.getCoffre().isChestSpawned() && Arene.this.isTeleportAllowed()) {
                            Arene.this.updateTeleportBar();
                            Arene.access$610(Arene.this);
                            if (Arene.this.TELEPORT_TIME_LEFT <= 0.0) {
                                Arene.this.disableTeleport();
                                Arene.this.removePlayerTeleportBar();
                                Arene.this.TELEPORT_TIME_LEFT = Arene.this.TELEPORT_TIME_LEFT_VAR;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 20L);
    }

    public boolean isTeleportAllowed() {
        return this.allowTeleport;
    }

    public boolean canTeamUseSingleTeleport(Equipe e) {
        return this.teamsToSingleTeleport.contains(e);
    }

    public void enableTeleport() {
        String separator = ChatColor.GOLD + "----------------";
        for (Player online : this.groupe.getPlayers()) {
            online.sendMessage(separator);
            online.sendMessage(mineralcontest.prefixGlobal + Lang.arena_chest_spawned.toString());
            online.sendMessage(separator);
        }
        this.allowTeleport = true;
        this.createTeleportBar();
    }

    private void createTeleportBar() {
        if (this.teleportStatusBar == null) {
            this.teleportStatusBar = Bukkit.createBossBar((String)Lang.arena_teleport_now_enabled.toString(), (BarColor)BarColor.BLUE, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);
        }
    }

    public void removePlayerTeleportBar() {
        if (this.teleportStatusBar != null) {
            this.teleportStatusBar.removeAll();
        }
    }

    public void updateTeleportBar() {
        this.createTeleportBar();
        double status = this.TELEPORT_TIME_LEFT / this.TELEPORT_TIME_LEFT_VAR;
        this.teleportStatusBar.setProgress(status);
        for (Player player : this.groupe.getPlayers()) {
            this.teleportStatusBar.removePlayer(player);
            this.teleportStatusBar.addPlayer(player);
        }
    }

    public void disableTeleport() {
        String separator = ChatColor.GOLD + "----------------";
        this.TELEPORT_TIME_LEFT = this.TELEPORT_TIME_LEFT_VAR;
        if (this.allowTeleport) {
            for (Player online : this.groupe.getPlayers()) {
                online.sendMessage(separator);
                online.sendMessage(mineralcontest.prefixGlobal + Lang.arena_teleport_now_disabled.toString());
                online.sendMessage(separator);
            }
        }
        this.removePlayerTeleportBar();
        this.clearSingleTeleportTeams();
        this.allowTeleport = false;
    }

    public void setTeleportSpawn(Location z) {
        if (mineralcontest.debug) {
            mineralcontest.plugin.getLogger().info(mineralcontest.prefixGlobal + Lang.arena_spawn_added.toString());
        }
        this.teleportSpawn = z;
    }

    public void setCoffre(Location position) {
        if (position == null) {
            Bukkit.getLogger().severe("Position is null !");
        }
        this.coffreArene.setChestLocation(position);
        if (position.getBlock() != null) {
            position.getBlock().setType(Material.AIR);
        }
        this.groupe.getAutomatedChestManager().replace(CoffreArene.class, this.coffreArene);
        mineralcontest.plugin.getLogger().info(mineralcontest.prefixGlobal + Lang.arena_chest_added.toString());
    }

    public AutomatedChestAnimation getCoffre() {
        return this.coffreArene;
    }

    public int getTIME_BEFORE_CHEST() {
        return this.TIME_BEFORE_CHEST;
    }

    static /* synthetic */ double access$610(Arene x0) {
        double d = x0.TELEPORT_TIME_LEFT;
        x0.TELEPORT_TIME_LEFT = d - 1.0;
        return d;
    }
}

