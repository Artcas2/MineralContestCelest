package fr.synchroneyes.special_events.halloween2024.game_events;

import fr.synchroneyes.custom_events.MCMassBlockSpawnEndedEvent;
import fr.synchroneyes.custom_events.MCPlayerOpenChestEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.MassBlockSpawner;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.special_events.halloween2024.chests.ParkourChest;
import fr.synchroneyes.special_events.halloween2024.game_events.HalloweenEvent;
import fr.synchroneyes.special_events.halloween2024.utils.ClonedInventory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class HellParkourEvent extends HalloweenEvent implements Listener {
    private Game game;
    private Location parkourSpawnLocation;
    private Location parkourChestLocation;
    private boolean isEnabled = false;
    private int lavaHeight = 252;
    private BukkitTask lavaLoop;
    private List<Player> playersAlive;
    private HashMap<Player, ClonedInventory> playersInventory;
    private List<Player> playerWithoutInventory;
    private ParkourChest parkourChest;
    private MassBlockSpawner spawner;
    private boolean blockSpawnEnded = false;

    public HellParkourEvent(Game partie) {
        super(partie);
        this.game = partie;
        this.playersAlive = new ArrayList<Player>();
        this.playersInventory = new HashMap();
        this.playerWithoutInventory = new ArrayList<Player>();
        this.parkourChest = new ParkourChest(54, partie.groupe.getAutomatedChestManager());
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
        this.spawner = new MassBlockSpawner();
    }

    @Override
    public String getEventName() {
        return "HellParkour";
    }

    @Override
    public void executionContent() {
        if (!this.blockSpawnEnded) {
            return;
        }
        for (Player player : this.game.groupe.getPlayers()) {
            PlayerUtils.respawnPlayer(player);
            player.teleport(this.parkourSpawnLocation);
        }
    }

    @Override
    public void beforeExecute() {
        for (Player player : this.game.groupe.getPlayers()) {
            if (this.game.isReferee(player)) continue;
            this.playersInventory.put(player, new ClonedInventory(player.getInventory()));
            this.playerWithoutInventory.add(player);
            player.getInventory().clear();
            player.sendMessage(mineralcontest.prefixPrive + "[???] J'ai ENCORE appuy\u00e9 sur ce bouton, cette fois-ci \u00e7a va \u00eatre un vrai massacre... Des lags sont \u00e0 pr\u00e9voir pendant un instant...");
        }
        for (Player p : this.getPartie().groupe.getPlayers()) {
            if (this.getPartie().isReferee(p)) continue;
            this.playersAlive.add(p);
        }
        World world = this.getPartie().groupe.getMonde();
        int xLocation = 20000;
        int yLocation = 250;
        int zLocation = 20000;
        File parkourContent = new File(mineralcontest.plugin.getDataFolder(), FileList.Halloween_Parkour.toString());
        YamlConfiguration parkourContentConfig = YamlConfiguration.loadConfiguration((File)parkourContent);
        ConfigurationSection blocksSection = parkourContentConfig.getConfigurationSection("blocks");
        for (String blockId : blocksSection.getKeys(false)) {
            int x = Integer.parseInt(parkourContentConfig.get("blocks." + blockId + ".x").toString()) + 10;
            int y = Integer.parseInt(parkourContentConfig.get("blocks." + blockId + ".y").toString()) + 150;
            int z = Integer.parseInt(parkourContentConfig.get("blocks." + blockId + ".z").toString()) + 10;
            String itemType = parkourContentConfig.get("blocks." + blockId + ".type").toString();
            Material itemTypeMaterial = Material.valueOf((String)itemType);
            this.spawner.addBlock(new Location(world, (double)(xLocation + x), (double)(yLocation + y), (double)(zLocation + z)), itemTypeMaterial);
            if (itemTypeMaterial == Material.OAK_PLANKS) {
                this.parkourSpawnLocation = new Location(world, (double)(xLocation + x), (double)(yLocation + y + 2), (double)(zLocation + z));
            }
            if (itemTypeMaterial != Material.CHEST) continue;
            this.parkourChestLocation = new Location(world, (double)(xLocation + x), (double)(yLocation + y), (double)(zLocation + z));
            this.parkourChest.setChestLocation(this.parkourChestLocation);
            this.parkourChest.spawn();
        }
        this.spawner.spawnBlocks();
    }

    @Override
    public void afterExecute() {
        if (!this.blockSpawnEnded) {
            return;
        }
        this.isEnabled = true;
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            this.lavaLoop = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
                if (this.isEnabled) {
                    this.addLava();
                    ++this.lavaHeight;
                }
            }, 0L, 100L);
        }, 160L);
    }

    @Override
    public String getEventTitle() {
        return "Parkour de l'enfer";
    }

    @Override
    public String getEventDescription() {
        return "Atteindrez-vous le bout du parcours ?";
    }

    @Override
    public boolean isTextMessageNotificationEnabled() {
        return false;
    }

    @Override
    public boolean isNotificationDelayed() {
        return true;
    }

    public boolean isPlayerOnParkour(Player player) {
        Location playerLocation = player.getLocation();
        int xLocation = 20000;
        int yLocation = 250;
        int zLocation = 20000;
        return playerLocation.getBlockX() >= xLocation && playerLocation.getBlockX() <= xLocation + 20 && playerLocation.getBlockZ() >= zLocation && playerLocation.getBlockZ() <= zLocation + 20 && playerLocation.getBlockY() >= yLocation && playerLocation.getBlockY() <= yLocation + 25;
    }

    private void addLava() {
        if (this.lavaHeight > 275) {
            this.lavaLoop.cancel();
            return;
        }
        World world = this.getPartie().groupe.getMonde();
        int xLocation = 20001;
        int zLocation = 20001;
        for (int x = xLocation; x < xLocation + 19; ++x) {
            for (int z = zLocation; z < zLocation + 19; ++z) {
                world.getBlockAt(x, this.lavaHeight, z).setType(Material.LAVA);
                this.spawner.addBlock(new Location(world, (double)x, (double)this.lavaHeight, (double)z), Material.LAVA);
            }
        }
    }

    private void cleanParkour() {
        this.lavaLoop.cancel();
        this.spawner.removeSpawnedBlocks();
        for (Player player : this.playersAlive) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
            player.teleport(mcPlayer.getEquipe().getMaison().getHouseLocation());
            this.resetPlayerInventory(player);
            this.playerWithoutInventory.remove(player);
        }
        for (Player p : this.getPartie().groupe.getPlayers()) {
            if (!this.getPartie().isReferee(p)) continue;
            p.teleport(this.getPartie().getArene().getCoffre().getLocation());
        }
        this.playersAlive.clear();
        this.isEnabled = false;
    }

    private void resetPlayerInventory(Player player) {
        PlayerInventory inventory = this.playersInventory.get(player).reset();
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.getInventory().setContents(inventory.getContents());
        player.getInventory().setExtraContents(inventory.getExtraContents());
        this.playersInventory.remove(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!this.isEnabled) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (!this.isPlayerOnParkour(player)) {
            return;
        }
        if (!event.getCause().equals((Object)EntityDamageEvent.DamageCause.FALL) && !event.getCause().equals((Object)EntityDamageEvent.DamageCause.LAVA)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathByPlayerEvent event) {
        if (!this.isEnabled) {
            return;
        }
        if (!this.isPlayerOnParkour(event.getPlayerDead())) {
            return;
        }
        Player player = event.getPlayerDead();
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
        Equipe equipe = mcPlayer.getEquipe();
        equipe.retirerPoints(100);
        player.sendMessage(mineralcontest.prefixPrive + "[???] J'ai triomph\u00e9. Vous n'\u00eates qu'une bande de nullard sans talent.");
        player.sendMessage(mineralcontest.prefixPrive + "Vous avez fait perdre" + ChatColor.RED + " -100 points" + ChatColor.RESET + " \u00e0 votre \u00e9quipe.");
        this.playersAlive.remove(player);
        if (this.playersAlive.isEmpty()) {
            this.cleanParkour();
        }
    }

    @EventHandler
    public void onPlayerSpawn(MCPlayerRespawnEvent event) {
        if (this.playersAlive.contains(event.getJoueur())) {
            event.getJoueur().teleport(this.parkourSpawnLocation);
            this.playerWithoutInventory.remove(event.getJoueur());
            return;
        }
        if (this.playerWithoutInventory.contains(event.getJoueur())) {
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
                event.getJoueur().sendMessage(mineralcontest.prefixPrive + "Vous avez r\u00e9cup\u00e9r\u00e9 votre inventaire.");
                event.getJoueur().sendMessage(mineralcontest.prefixPrive + ChatColor.RED + "Si vous n'avez aucun item, suicidez-vous dans la lave de l'ar\u00e8ne.");
                this.resetPlayerInventory(event.getJoueur());
                this.playerWithoutInventory.remove(event.getJoueur());
            }, 40L);
        }
    }

    @EventHandler
    public void onChestOpenEvent(MCPlayerOpenChestEvent event) {
        if (!event.getCoffre().equals(this.parkourChest)) {
            return;
        }
        this.cleanParkour();
        this.parkourChest.remove();
    }

    @EventHandler
    public void onBlockSpawnEnd(MCMassBlockSpawnEndedEvent event) {
        if (event.getSpawner().equals(this.spawner)) {
            this.blockSpawnEnded = true;
            this.sendEventNotification();
            this.executionContent();
            this.afterExecute();
        }
    }

    public Game getGame() {
        return this.game;
    }
}

