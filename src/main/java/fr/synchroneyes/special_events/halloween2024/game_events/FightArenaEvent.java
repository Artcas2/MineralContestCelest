package fr.synchroneyes.special_events.halloween2024.game_events;

import fr.synchroneyes.custom_events.MCBossKilledByPlayerEvent;
import fr.synchroneyes.custom_events.MCMassBlockSpawnEndedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.Core.Boss.Boss;
import fr.synchroneyes.mineral.Core.Boss.BossType.AngryZombie;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.MassBlockSpawner;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.special_events.halloween2024.game_events.HalloweenEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FightArenaEvent extends HalloweenEvent implements Listener {
    private Location arenaLocation;
    private List<Player> alivePlayers;
    private Boolean enabled = false;
    private HashMap<Equipe, Location> teamSpawnLocation;
    private Location arenaCenter;
    private Boss boss;
    private List<Block> blocks;
    private boolean isEnabled = false;
    private MassBlockSpawner blockSpawner;
    private boolean blockSpawnEnded = false;

    public FightArenaEvent(Game partie) {
        super(partie);
        this.arenaLocation = partie.getArene().getCoffre().getLocation().clone();
        this.arenaLocation.setX(18000.0);
        this.arenaLocation.setY(222.0);
        this.arenaLocation.setZ(18000.0);
        this.alivePlayers = new LinkedList<Player>();
        this.teamSpawnLocation = new HashMap();
        this.blocks = new ArrayList<Block>();
        this.blockSpawner = new MassBlockSpawner();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    @Override
    public String getEventName() {
        return "FightArena";
    }

    @Override
    public void executionContent() {
        if (!this.blockSpawnEnded) {
            return;
        }
        for (Player joueur : this.alivePlayers) {
            PlayerUtils.respawnPlayer(joueur);
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            Equipe equipe = mcPlayer.getEquipe();
            Location spawnLocation = this.teamSpawnLocation.get(equipe);
            joueur.teleport(spawnLocation);
        }
        for (Player p : this.getPartie().groupe.getPlayers()) {
            if (!this.getPartie().isReferee(p)) continue;
            p.teleport(this.arenaLocation);
        }
    }

    @Override
    public void beforeExecute() {
        for (Player player : this.getPartie().groupe.getPlayers()) {
            if (this.getPartie().isReferee(player)) continue;
            this.alivePlayers.add(player);
        }
        for (Player player : this.alivePlayers) {
            player.sendMessage(mineralcontest.prefixPrive + "[???] J'ai appuy\u00e9 sur un bouton, \u00e7a risque de ne pas \u00eatre bon... Des lags sont \u00e0 pr\u00e9voir pendant un instant...");
        }
        ArrayList<Equipe> teams = new ArrayList<Equipe>();
        for (Player joueur : this.alivePlayers) {
            Equipe team = this.getPartie().getPlayerTeam(joueur);
            if (teams.contains(team)) continue;
            teams.add(team);
        }
        World world = this.getPartie().groupe.getMonde();
        int xLocation = 18000;
        int yLocation = 250;
        int zLocation = 18000;
        File arenaContent = new File(mineralcontest.plugin.getDataFolder(), FileList.Halloween_Arena.toString());
        YamlConfiguration arenaContentConfig = YamlConfiguration.loadConfiguration((File)arenaContent);
        ConfigurationSection blocksSection = arenaContentConfig.getConfigurationSection("blocks");
        for (String blockId : blocksSection.getKeys(false)) {
            int x = Integer.parseInt(arenaContentConfig.get("blocks." + blockId + ".x").toString());
            int y = Integer.parseInt(arenaContentConfig.get("blocks." + blockId + ".y").toString());
            int z = Integer.parseInt(arenaContentConfig.get("blocks." + blockId + ".z").toString());
            String itemType = arenaContentConfig.get("blocks." + blockId + ".type").toString();
            Material itemTypeMaterial = Material.valueOf((String)itemType);
            this.blockSpawner.addBlock(new Location(world, (double)(xLocation + x), (double)(yLocation + y), (double)(zLocation + z)), itemTypeMaterial);
            if (itemTypeMaterial == Material.YELLOW_WOOL) {
                if (teams.isEmpty()) continue;
                Equipe _team = (Equipe)teams.get(0);
                teams.remove(_team);
                this.teamSpawnLocation.put(_team, new Location(world, (double)(xLocation + x), (double)(yLocation + y + 1), (double)(zLocation + z)));
            }
            if (itemTypeMaterial != Material.REDSTONE_BLOCK) continue;
            this.arenaCenter = new Location(world, (double)(xLocation + x), (double)(yLocation + y + 2), (double)(zLocation + z));
        }
        this.blockSpawner.setBlockPerBatch(500);
        this.blockSpawner.spawnBlocks();
    }

    @Override
    public void afterExecute() {
        if (!this.blockSpawnEnded) {
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            AngryZombie boss = new AngryZombie();
            this.boss = boss;
            this.getPartie().getBossManager().spawnNewBoss(this.arenaCenter, boss);
        }, 80L);
        this.isEnabled = true;
    }

    @Override
    public String getEventTitle() {
        return "Ar\u00e8ne maudite...";
    }

    @Override
    public String getEventDescription() {
        return "Vous avez \u00e9t\u00e9 envoy\u00e9 dans l'ar\u00e8ne maudite, combattez pour votre survie!";
    }

    @Override
    public boolean isTextMessageNotificationEnabled() {
        return true;
    }

    @Override
    public boolean isNotificationDelayed() {
        return true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathByPlayerEvent event) {
        if (!this.isEnabled) {
            return;
        }
        this.alivePlayers.remove(event.getPlayerDead());
        if (this.getTeamLeftCount() == 0) {
            this.punishPlayers();
        }
        if (this.getTeamLeftCount() == 1 && !this.boss.isAlive()) {
            this.sendRewardToPlayers();
        }
    }

    private int getTeamLeftCount() {
        int teamCount = 0;
        HashSet<String> teams = new HashSet<String>();
        for (Player joueur : this.alivePlayers) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            if (teams.contains(mcPlayer.getEquipe().getNomEquipe())) continue;
            teams.add(mcPlayer.getEquipe().getNomEquipe());
            ++teamCount;
        }
        return teamCount;
    }

    @EventHandler
    public void onBossDeath(MCBossKilledByPlayerEvent event) {
        if (!this.isEnabled) {
            return;
        }
        if (this.getTeamLeftCount() == 1 && this.boss.equals(event.getBoss())) {
            this.sendRewardToPlayers();
        }
    }

    private void sendRewardToPlayers() {
        for (Player joueur : this.alivePlayers) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            Equipe equipe = mcPlayer.getEquipe();
            joueur.teleport(equipe.getMaison().getHouseLocation());
            joueur.sendMessage(mineralcontest.prefixPrive + "Vous avez surv\u00e9cu \u00e0 l'ar\u00e8ne maudite! Vous avez \u00e9t\u00e9 t\u00e9l\u00e9port\u00e9 dans votre base. Vous avez \u00e9galement re\u00e7u une r\u00e9compense.");
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.REDSTONE, 10)});
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_INGOT, 3)});
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLD_INGOT, 2)});
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.DIAMOND, 1)});
        }
        this.cleanArena();
        this.boss.remove();
    }

    private void punishPlayers() {
        for (Player joueur : this.getPartie().groupe.getPlayers()) {
            if (this.getPartie().isReferee(joueur)) continue;
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            Equipe equipe = mcPlayer.getEquipe();
            equipe.retirerPoints(100);
            joueur.sendMessage(mineralcontest.prefixPrive + "[???] J'ai triomph\u00e9. Vous n'\u00eates qu'une bande de nullard sans talent.");
            joueur.sendMessage(mineralcontest.prefixPrive + "Vous avez fait perdre " + ChatColor.RED + " -100 points" + ChatColor.RESET + " \u00e0 votre \u00e9quipe.");
        }
        this.cleanArena();
        this.boss.remove();
    }

    private void cleanArena() {
        this.blockSpawner.removeSpawnedBlocks();
        this.isEnabled = false;
        for (Player p : this.getPartie().groupe.getPlayers()) {
            this.boss.removePlayerBossBar(p);
            if (!this.getPartie().isReferee(p)) continue;
            p.teleport(this.getPartie().getArene().getCoffre().getLocation());
        }
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        if (!this.enabled.booleanValue()) {
            return;
        }
        if (this.alivePlayers.contains(event.getJoueur())) {
            event.getJoueur().teleport(this.teamSpawnLocation.get(mineralcontest.plugin.getMCPlayer(event.getJoueur()).getEquipe()));
        }
    }

    @EventHandler
    public void onBlockSpawnEnd(MCMassBlockSpawnEndedEvent event) {
        if (event.getSpawner().equals(this.blockSpawner)) {
            this.blockSpawnEnded = true;
            this.sendEventNotification();
            this.executionContent();
            this.afterExecute();
        }
    }
}

