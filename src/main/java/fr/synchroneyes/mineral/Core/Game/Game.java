package fr.synchroneyes.mineral.Core.Game;

import fr.synchroneyes.custom_events.MCGameEndEvent;
import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCGameTickEvent;
import fr.synchroneyes.custom_events.MCPlayerBecomeRefereeEvent;
import fr.synchroneyes.custom_events.MCPlayerLocationHUDUpdatedEvent;
import fr.synchroneyes.custom_events.MCPlayerQuitRefereeEvent;
import fr.synchroneyes.custom_events.MCPreGameStartEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Arena.Arene;
import fr.synchroneyes.mineral.Core.Boss.BossManager;
import fr.synchroneyes.mineral.Core.Game.BlockManager;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.Inventories.InventoryInterface;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.Inventories.SelectionEquipeInventory;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Core.Parachute.ParachuteManager;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardAPI;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardFields;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Statistics.StatsManager;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.Utils.ChatColorString;
import fr.synchroneyes.mineral.Utils.Door.DisplayBlock;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayerTeam;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.Utils.TimeConverter;
import fr.synchroneyes.mineral.Utils.VersionChecker.Version;
import fr.synchroneyes.mineral.Utils.WorldUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Game implements Listener {
    private Arene arene;
    private LinkedList<Player> playersReady;
    private static int DUREE_PARTIE = 60;
    private int tempsPartie = 60 * DUREE_PARTIE;
    public int PreGameTimeLeft = 10;
    private boolean GameStarted = false;
    private boolean GamePaused = false;
    private boolean PreGame = false;
    private boolean GameEnded = false;
    private boolean GameForced = false;
    public boolean isGameInitialized = false;
    public int killCounter = 0;
    private LinkedList<CouplePlayerTeam> disconnectedPlayers;
    private HashMap<String, Boolean> PlayerThatTriedToLogIn;
    private LinkedList<Block> addedChests;
    private BukkitTask gameLoopManager = null;
    private int databaseGameId;
    private InventoryInterface available_teams_inventory;
    private StatsManager statsManager;
    private PlayerBonus playerBonusManager;
    private ShopManager shopManager;
    private BossManager bossManager;
    public Groupe groupe;
    private LinkedList<House> equipes;
    public LinkedList<BlockSaver> affectedBlocks;
    private LinkedList<Player> referees;
    private ParachuteManager parachuteManager;

    public Game(Groupe g) {
        this.groupe = g;
        this.disconnectedPlayers = new LinkedList();
        this.affectedBlocks = new LinkedList();
        this.referees = new LinkedList();
        this.playersReady = new LinkedList();
        this.PlayerThatTriedToLogIn = new HashMap();
        this.equipes = new LinkedList();
        this.addedChests = new LinkedList();
        this.arene = new Arene(this.groupe);
        this.parachuteManager = new ParachuteManager(g);
        this.statsManager = new StatsManager(this);
        this.playerBonusManager = new PlayerBonus(this);
        this.shopManager = new ShopManager(this);
        this.bossManager = new BossManager(this);
        this.initGameSettings();
    }

    public ParachuteManager getParachuteManager() {
        return this.parachuteManager;
    }

    public LinkedList<Player> getPlayersReady() {
        LinkedList<Player> liste_joueurs = new LinkedList<Player>();
        for (Player membre : this.groupe.getPlayers()) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(membre);
            if (mcPlayer == null || !mcPlayer.isInPlugin() || !this.isPlayerReady(membre)) continue;
            liste_joueurs.add(membre);
        }
        return liste_joueurs;
    }

    public StatsManager getStatsManager() {
        return this.statsManager;
    }

    public Equipe getWinningTeam() {
        int maxScore = Integer.MIN_VALUE;
        Equipe winner = null;
        for (House maison : this.equipes) {
            if (maison.getTeam().getScore() <= maxScore) continue;
            winner = maison.getTeam();
            maxScore = winner.getScore();
        }
        return winner;
    }

    public boolean isPlayerInGame(Player p) {
        for (Player joueur : this.groupe.getPlayers()) {
            if (!p.equals((Object)joueur)) continue;
            return true;
        }
        return false;
    }

    public void openTeamSelectionMenuToPlayer(Player p) {
        if (this.available_teams_inventory == null) {
            this.available_teams_inventory = new SelectionEquipeInventory(this.equipes);
        }
        this.available_teams_inventory.openInventory(p);
    }

    public InventoryInterface getTeamSelectionMenu() {
        if (this.available_teams_inventory == null) {
            this.available_teams_inventory = new SelectionEquipeInventory(this.equipes);
        }
        this.available_teams_inventory.setInventoryItems();
        return this.available_teams_inventory;
    }

    private void initGameSettings() {
        try {
            DUREE_PARTIE = this.groupe.getParametresPartie().getCVAR("game_time").getValeurNumerique();
            this.tempsPartie = DUREE_PARTIE * 60;
            this.PreGameTimeLeft = this.groupe.getParametresPartie().getCVAR("pre_game_timer").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<House> getHouses() {
        return this.equipes;
    }

    public void setGroupe(Groupe g) {
        this.groupe = g;
    }

    public boolean isTheBlockAChest(Block b) {
        return b.getState() instanceof InventoryHolder || b.getState() instanceof Chest;
    }

    public void addAChest(Block block) {
        if (this.isTheBlockAChest(block) && !this.addedChests.contains(block)) {
            this.addedChests.add(block);
            GameLogger.addLog(new Log("ChestSaverAdd", "A chest got added", "block_event"));
        }
    }

    public boolean isThisChestAlreadySaved(Block b) {
        return this.addedChests.contains(b);
    }

    public House getHouseFromName(String name) {
        for (House maison : this.equipes) {
            if (!maison.getTeam().getNomEquipe().equalsIgnoreCase(name) && !ChatColorString.toString(maison.getTeam().getCouleur()).equalsIgnoreCase(name)) continue;
            return maison;
        }
        return null;
    }

    public void addEquipe(House t) {
        if (!this.equipes.contains(t)) {
            this.equipes.add(t);
        }
    }

    public boolean isThisBlockAGameChest(Block b) {
        if (!this.isTheBlockAChest(b)) {
            return false;
        }
        return this.addedChests.contains(b);
    }

    public void teleportToLobby(Player player) {
        Location spawnLocation = mineralcontest.plugin.pluginWorld.getSpawnLocation();
        Vector playerVelocity = player.getVelocity();
        player.setFallDistance(0.0f);
        playerVelocity.setY(0.05);
        player.setVelocity(playerVelocity);
        PlayerUtils.teleportPlayer(player, spawnLocation.getWorld(), spawnLocation);
    }

    public boolean havePlayerTriedToLogin(String playerDisplayName) {
        for (Map.Entry<String, Boolean> entry : this.PlayerThatTriedToLogIn.entrySet()) {
            if (!entry.getKey().toLowerCase().equals(playerDisplayName.toLowerCase())) continue;
            return true;
        }
        return false;
    }

    public boolean allowPlayerLogin(String playerDisplayName) {
        if (this.havePlayerTriedToLogin(playerDisplayName)) {
            for (Map.Entry<String, Boolean> entry : this.PlayerThatTriedToLogIn.entrySet()) {
                if (!entry.getKey().toLowerCase().equals(playerDisplayName.toLowerCase())) continue;
                entry.setValue(true);
            }
            return true;
        }
        return false;
    }

    public boolean areAllPlayersReady() {
        return this.getPlayersReady().size() == this.groupe.getPlayers().size();
    }

    public boolean isPlayerReady(Player p) {
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(p);
        if (mcPlayer == null) {
            return false;
        }
        return mcPlayer.isInPlugin() && this.playersReady.contains(p);
    }

    public void removePlayerReady(Player p) {
        if (this.isPlayerReady(p)) {
            this.playersReady.remove(p);
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.player_is_no_longer_ready.toString(), p), this.groupe);
        }
        ScoreboardAPI.updateField(p, ScoreboardFields.SCOREBOARD_PLAYER_READY, ScoreboardAPI.prefix + ChatColor.RED + (Object)((Object)Lang.not_ready_tag));
    }

    public void setPlayerReady(Player p) throws Exception {
        if (!this.isPlayerReady(p)) {
            this.playersReady.add(p);
            ScoreboardAPI.updateField(p, ScoreboardFields.SCOREBOARD_PLAYER_READY, ScoreboardAPI.prefix + ChatColor.GREEN + (Object)((Object)Lang.ready_tag));
            this.groupe.sendToEveryone(mineralcontest.prefixGlobal + Lang.translate(Lang.player_is_now_ready.toString(), p));
            if (this.areAllPlayersReady()) {
                if (mineralcontest.communityVersion && this.groupe.getPlayers().size() < mineralcontest.min_player_per_group) {
                    this.groupe.sendToEveryone(mineralcontest.prefixErreur + "Il n'y a pas assez de joueur dans le groupe pour pouvoir d\u00e9marrer une partie. Nombre de joueur(s) requis: " + mineralcontest.min_player_per_group);
                    return;
                }
                if (this.groupe.getEtatPartie().equals((Object)Etats.EN_ATTENTE)) {
                    Bukkit.getLogger().info(mineralcontest.prefix + " Starting vote for group " + this.groupe.getNom());
                    this.groupe.initVoteMap();
                    this.playersReady.clear();
                } else {
                    if (!this.isGameStarted()) {
                        if (!this.allPlayerHaveTeam()) {
                            if (this.groupe.getParametresPartie().getCVAR("mp_randomize_team").getValeurNumerique() == 1) {
                                this.randomizeTeam(true);
                                return;
                            }
                            this.startAllPlayerHaveTeamTimer();
                            return;
                        }
                        this.demarrerPartie(true);
                        return;
                    }
                    if (this.isGamePaused()) {
                        this.resumeGame();
                    } else if (!this.groupe.getKitManager().isKitsEnabled()) {
                        this.demarrerPartie(false);
                    }
                }
            } else {
                this.groupe.sendToEveryone(mineralcontest.prefixGroupe + ChatColor.BOLD + "" + ChatColor.RED + Lang.non_ready_hud.toString() + ChatColor.RESET + this.groupe.getNomsJoueurNonPret());
            }
        }
    }

    private void startAllPlayerHaveTeamTimer() {
        Game instance = this;
        new BukkitRunnable(){

            public void run() {
                if (Game.this.allPlayerHaveTeam()) {
                    try {
                        Game.this.demarrerPartie(false);
                        this.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Game.this.warnPlayerWithNoTeam();
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 100L);
    }

    public boolean isGameStarted() {
        return this.GameStarted;
    }

    public boolean isGamePaused() {
        return this.GamePaused;
    }

    public boolean isPreGame() {
        return this.PreGame;
    }

    public boolean isGameEnded() {
        return this.GameEnded;
    }

    public boolean isGameForced() {
        return this.GameForced;
    }

    public Arene getArene() {
        return this.arene;
    }

    public void clear() {
        this.isGameInitialized = false;
        this.arene.clear();
        this.referees.clear();
        this.disconnectedPlayers.clear();
        this.playersReady.clear();
        BlockManager instance = BlockManager.getInstance();
        for (Block block : instance.getPlacedBlocks()) {
            block.setType(Material.AIR);
        }
        if (mineralcontest.plugin.pluginWorld != null && !mineralcontest.debug) {
            for (Player player : this.groupe.getPlayers()) {
                this.teleportToLobby(player);
                PlayerUtils.clearPlayer(player, true);
            }
        }
        this.equipes.clear();
    }

    public void addBlock(Block b, BlockSaver.Type type) {
        this.affectedBlocks.add(new BlockSaver(b, type));
        GameLogger.addLog(new Log("BlockSaverAdd", "A block got " + (Object)((Object)type) + " (Type: " + b.getType().toString() + " - Loc: " + b.getLocation().toVector().toString() + ")", "block_event"));
    }

    public void addReferee(Player player) {
        if (!this.isReferee(player)) {
            MCPlayerBecomeRefereeEvent event = new MCPlayerBecomeRefereeEvent(mineralcontest.plugin.getMCPlayer(player));
            event.callEvent();
            if (event.isCancelled()) {
                return;
            }
            player.sendMessage(mineralcontest.prefixPrive + Lang.now_referee.toString());
            this.referees.add(player);
            PlayerUtils.equipReferee(player);
            if (this.getPlayerTeam(player) != null) {
                this.getPlayerTeam(player).removePlayer(player);
            }
        }
    }

    public void removeReferee(Player player, boolean switchToTeam) {
        if (this.isReferee(player)) {
            MCPlayerQuitRefereeEvent event = new MCPlayerQuitRefereeEvent(mineralcontest.plugin.getMCPlayer(player));
            event.callEvent();
            if (event.isCancelled()) {
                return;
            }
            player.sendMessage(mineralcontest.prefixPrive + Lang.no_longer_referee.toString());
            this.referees.remove(player);
            PlayerUtils.clearPlayer(player, true);
            if ((this.isGameStarted() || this.isPreGame()) && switchToTeam) {
                this.setPlayerRandomTeam(player);
            }
        }
        for (Player joueur : this.groupe.getPlayers()) {
            joueur.showPlayer((Plugin)mineralcontest.plugin, player);
            player.showPlayer((Plugin)mineralcontest.plugin, joueur);
        }
        if (this.groupe.getGame() != null && (this.groupe.getGame().isGameStarted() || this.groupe.getGame().isPreGame()) && this.groupe.getKitManager().getPlayerKit(player) == null) {
            this.groupe.getKitManager().openInventoryToPlayer(player);
        }
    }

    public void setPlayerRandomTeam(Player p) {
        int nombreAleatoire = new Random().nextInt(this.equipes.size());
        if (this.getPlayerTeam(p) != null) {
            this.getPlayerTeam(p).removePlayer(p);
        }
        try {
            this.equipes.get(nombreAleatoire).getTeam().addPlayerToTeam(p, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Player> getReferees() {
        return this.referees;
    }

    public boolean isReferee(Player p) {
        return this.referees.contains(p);
    }

    public void resetMap() {
        this.clear();
    }

    public void handleDoors() {
        final int rayonPorte = 3;
        int nomrbeTicks = 5;
        new BukkitRunnable(){

            public void run() {
                if (Game.this.isGameStarted() && !Game.this.isPreGame() && !Game.this.isGamePaused()) {
                    for (Player online : Game.this.groupe.getPlayers()) {
                        double y;
                        if (Game.this.isReferee(online)) {
                            for (House maison : Game.this.equipes) {
                                double y2;
                                Equipe equipe = maison.getTeam();
                                Location firestDoorBlock = equipe.getMaison().getPorte().getPorte().getFirst().getPosition();
                                Location playerLocation = online.getLocation();
                                double x = Math.pow(firestDoorBlock.getX() - playerLocation.getX(), 2.0);
                                if (Math.sqrt(x + (y2 = Math.pow(firestDoorBlock.getZ() - playerLocation.getZ(), 2.0))) > 5.0) continue;
                                for (DisplayBlock blockDePorte : maison.getPorte().getPorte()) {
                                    if (Radius.isBlockInRadius(blockDePorte.getPosition(), online.getLocation(), rayonPorte)) {
                                        maison.getPorte().playerIsNearDoor(online);
                                        continue;
                                    }
                                    maison.getPorte().playerIsNotNearDoor(online);
                                }
                            }
                            continue;
                        }
                        House maison = Game.this.getPlayerHouse(online);
                        if (maison == null || Game.this.getArene().getDeathZone().isPlayerDead(online)) continue;
                        Location firstDoorBlock = maison.getPorte().getPorte().getFirst().getPosition();
                        Location playerLocation = online.getLocation();
                        double x = Math.pow(firstDoorBlock.getX() - playerLocation.getX(), 2.0);
                        if (Math.sqrt(x + (y = Math.pow(firstDoorBlock.getZ() - playerLocation.getZ(), 2.0))) > 5.0) {
                            maison.getPorte().playerIsNotNearDoor(online);
                            continue;
                        }
                        for (DisplayBlock blockDePorte : maison.getPorte().getPorte()) {
                            if (Radius.isBlockInRadius(blockDePorte.getPosition(), online.getLocation(), rayonPorte)) {
                                maison.getPorte().playerIsNearDoor(online);
                                continue;
                            }
                            maison.getPorte().playerIsNotNearDoor(online);
                        }
                    }
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, (long)nomrbeTicks);
    }

    public void startGameLoop() {
        if (this.gameLoopManager != null) {
            this.gameLoopManager.cancel();
            this.gameLoopManager = null;
        }
        this.gameLoopManager = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, this::gameTick, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
            for (Player player : this.groupe.getPlayers()) {
                MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
                MCPlayerLocationHUDUpdatedEvent event = new MCPlayerLocationHUDUpdatedEvent(mcPlayer);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) {
                    return;
                }
                ChatColor resetColor = ChatColor.RESET;
                ChatColor teamColor = ChatColor.GOLD;
                if (!this.isReferee(player) && mcPlayer.getEquipe() != null) {
                    teamColor = mcPlayer.getEquipe().getCouleur();
                }
                String position = teamColor + "X: " + resetColor + player.getLocation().getBlockX() + " " + teamColor + "Y:" + resetColor + player.getLocation().getBlockY() + teamColor + " Z:" + resetColor + player.getLocation().getBlockZ();
                ScoreboardAPI.updateField(player, ScoreboardFields.SCOREBOARD_PLAYERLOCATION_VALUE, position);
            }
        }, 0L, (long)mineralcontest.player_location_hud_refresh_rate);
    }

    private void gameTick() {
        if (this.isPreGame()) {
            this.doGameStartingTick();
        } else if (this.isGamePaused()) {
            this.doGamePausedTick();
        } else {
            if (!this.isGameStarted() && !this.isGameEnded()) {
                this.gameLoopManager.cancel();
                return;
            }
            this.doGameTick();
        }
    }

    private void doGamePausedTick() {
        for (MCPlayer joueur : mineralcontest.plugin.getMCPlayers()) {
            String subTitleMessage = "";
            subTitleMessage = !joueur.getJoueur().isOp() ? Lang.hud_player_resume_soon.toString() : Lang.hud_admin_resume_help.toString();
            joueur.getJoueur().sendTitle(Lang.hud_player_paused.toString(), subTitleMessage, 0, 40, 0);
        }
    }

    private void doGameStartingTick() {
        boolean shouldClearPlayer;
        boolean bl = shouldClearPlayer = this.tempsPartie == 60 * this.groupe.getParametresPartie().getCVAR("game_time").getValeurNumerique();
        if (this.PreGameTimeLeft > 0) {
            --this.PreGameTimeLeft;
            for (MCPlayer joueur : mineralcontest.plugin.getMCPlayers()) {
                if (joueur.getEquipe() != null && shouldClearPlayer) {
                    joueur.clearInventory();
                    joueur.clearPlayerPotionEffects();
                }
                joueur.getJoueur().sendTitle(Lang.game_starting.toString(), Lang.translate(Lang.hud_game_starting.toString(), this), 0, 20, 0);
            }
            return;
        }
        this.PreGame = false;
        this.GameStarted = true;
        if (shouldClearPlayer) {
            for (MCPlayer joueur : mineralcontest.plugin.getMCPlayers()) {
                if (joueur.getEquipe() == null) continue;
                joueur.clearInventory();
                joueur.clearPlayerPotionEffects();
                joueur.giveBaseItems();
                if (joueur.getEquipe() != null && !this.isReferee(joueur.getJoueur())) {
                    joueur.teleportToHouse();
                }
                joueur.getJoueur().sendTitle(Lang.game_successfully_started.toString(), "", 20, 40, 20);
            }
            MCGameStartedEvent event = new MCGameStartedEvent(this);
            Bukkit.getPluginManager().callEvent((Event)event);
        }
    }

    private void doGameTick() {
        MCGameTickEvent event = new MCGameTickEvent(this);
        event.callEvent();
        try {
            if (this.tempsPartie == 0 && !this.isGameEnded()) {
                this.terminerPartie();
                this.gameLoopManager.cancel();
                return;
            }
            this.arene.getDeathZone().reducePlayerTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.tempsPartie > 0) {
            --this.tempsPartie;
        }
        try {
            if (this.tempsPartie <= this.groupe.getParametresPartie().getCVAR("chicken_spawn_time").getValeurNumerique() * 60 && !this.arene.chickenWaves.isStarted()) {
                this.arene.chickenWaves.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherScores() {
        for (House house : this.equipes) {
            if (house.getTeam().getJoueurs().isEmpty()) continue;
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.team_score.toString(), house.getTeam()), this.groupe);
        }
    }

    private Equipe afficherGagnant() {
        LinkedList<Equipe> _equipes = new LinkedList<Equipe>();
        for (House house : this.equipes) {
            _equipes.add(house.getTeam());
        }
        Equipe gagnante = null;
        int scoreMax = Integer.MIN_VALUE;
        for (Equipe equipe : _equipes) {
            if (equipe.getScore() <= scoreMax) continue;
            gagnante = equipe;
            scoreMax = equipe.getScore();
        }
        if (gagnante != null) {
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.team_winning.toString(), gagnante), this.groupe);
        }
        return gagnante;
    }

    public Equipe getPlayerTeam(Player j) {
        for (House house : this.equipes) {
            if (!house.getTeam().getJoueurs().contains(j)) continue;
            return house.getTeam();
        }
        return null;
    }

    public House getPlayerHouse(Player j) {
        for (House house : this.equipes) {
            if (!house.getTeam().getJoueurs().contains(j)) continue;
            return house;
        }
        return null;
    }

    public void terminerPartie() throws Exception {
        int n;
        if (this.isGameEnded()) {
            return;
        }
        this.GameEnded = true;
        this.gameLoopManager.cancel();
        MCGameEndEvent endEvent = new MCGameEndEvent(this);
        Bukkit.getPluginManager().callEvent((Event)endEvent);
        if (this.groupe.getPlayers().size() == 0) {
            return;
        }
        for (Player player : this.groupe.getPlayers()) {
            PlayerUtils.teleportPlayer(player, this.groupe.getMonde(), this.getArene().getCoffre().getLocation());
            PlayerUtils.clearPlayer(player, true);
            if (this.isReferee(player)) continue;
            player.setGameMode(GameMode.SPECTATOR);
        }
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.game_over.toString(), this.groupe);
        this.afficherScores();
        Equipe gagnant = this.afficherGagnant();
        this.arene.chickenWaves.setEnabled(false);
        this.arene.chickenWaves.stop();
        for (Entity entity : this.groupe.getMonde().getEntities()) {
            if (entity instanceof Player || entity instanceof ArmorStand) continue;
            entity.remove();
        }
        if (mineralcontest.communityVersion) {
            Bukkit.broadcastMessage((String)(mineralcontest.prefixGlobal + Lang.translate(Lang.group_finished_their_game_winner_display.toString(), this)));
        }
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            Inventory inventaireStat = this.getMenuStatistiques();
            for (Player joueur : this.groupe.getPlayers()) {
                joueur.openInventory(inventaireStat);
            }
            for (Player online : this.groupe.getPlayers()) {
                if (online == null || this.referees == null || this.isReferee(online) || this.getPlayerTeam(online) == null || !this.getPlayerTeam(online).equals(gagnant)) continue;
                PlayerUtils.setFirework(online, gagnant.toColor());
            }
        }, 20L);
        this.tempsPartie = n = this.groupe.getParametresPartie().getCVAR("end_game_timer").getValeurNumerique();
        this.groupe.sendToEveryone(mineralcontest.prefixGlobal + "Vous serez t\u00e9l\u00e9port\u00e9 au HUB dans " + n + " secondes.");
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            for (Player joueur : this.groupe.getPlayers()) {
                joueur.setGameMode(GameMode.SURVIVAL);
                this.removePlayerReady(joueur);
                ScoreboardAPI.clearScoreboard(joueur);
                ScoreboardAPI.createScoreboard(joueur, true);
            }
            this.resetMap();
            this.clear();
            this.GamePaused = false;
            this.GameStarted = false;
            this.groupe.setEtat(Etats.EN_ATTENTE);
            this.groupe.setGroupLocked(false);
            this.groupe.enableVote();
            this.groupe.resetGame();
        }, (long)(20 * n));
    }

    public Inventory getMenuStatistiques() {
        Inventory inventaireStats = Bukkit.createInventory(null, (int)27, (String)Lang.stats_menu_title.getDefault());
        for (ItemStack item : this.getStatsManager().getAllStatsAsItemStack()) {
            inventaireStats.addItem(new ItemStack[]{item});
        }
        return inventaireStats;
    }

    public void pauseGame() {
        if (this.isGameStarted() || this.isPreGame()) {
            this.GamePaused = true;
            this.playersReady.clear();
            for (Player online : this.groupe.getPlayers()) {
                online.sendMessage(mineralcontest.prefixPrive + Lang.hud_game_paused.toString());
                online.sendMessage(mineralcontest.prefixPrive + Lang.set_yourself_as_ready_to_start_game.toString());
                if (!online.isOp()) continue;
                online.sendMessage(mineralcontest.prefixAdmin + Lang.hud_admin_resume_help.toString());
            }
        }
    }

    public void resumeGame() {
        if (this.isPreGame() && this.isGamePaused()) {
            this.PreGame = true;
            this.GamePaused = false;
            return;
        }
        if (this.isGamePaused()) {
            Equipe team = null;
            if (team != null && !this.isGameForced()) {
                mineralcontest.broadcastMessage(mineralcontest.prefixErreur + "Impossible de reprendre la partie, il manque des joueurs dans l'\u00e9quipe " + team.getCouleur() + team.getNomEquipe(), this.groupe);
            } else {
                mineralcontest.plugin.getLogger().info("ON RESUME LA PARTIE");
                this.PreGame = true;
                this.PreGameTimeLeft = 5;
                this.GamePaused = false;
            }
        }
    }

    public boolean isPreGameAndGameStarted() {
        return this.isPreGame() && this.tempsPartie != DUREE_PARTIE * 60;
    }

    public boolean allPlayerHaveTeam() {
        LinkedList<Player> playersOnline = new LinkedList<Player>();
        int playerWithTeamCount = 0;
        for (Player player : this.groupe.getMonde().getPlayers()) {
            if (this.isReferee(player)) continue;
            playersOnline.add(player);
            if (this.getPlayerTeam(player) == null) continue;
            ++playerWithTeamCount;
        }
        return playerWithTeamCount == playersOnline.size();
    }

    private void warnPlayerWithNoTeam() {
        for (Player player : this.groupe.getMonde().getPlayers()) {
            if (this.isReferee(player) || this.getPlayerTeam(player) != null) continue;
            player.sendMessage(ChatColor.RED + "---------------");
            player.sendMessage(mineralcontest.prefixPrive + Lang.warn_player_you_dont_have_a_team.toString());
            player.sendMessage(ChatColor.RED + "---------------");
        }
    }

    public boolean demarrerPartie(boolean forceGameStart) throws Exception {
        if (this.isGameStarted()) {
            throw new Exception(Lang.get("game_already_started"));
        }
        this.statsManager = null;
        this.statsManager = new StatsManager(this);
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info(mineralcontest.prefixGlobal + Lang.get("game_starting"));
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info("=============================");
        }
        for (House house : this.equipes) {
            if (house.getHouseLocation() == null) {
                mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "[Verification] spawn maison equipe " + house.getTeam().getNomEquipe() + ": " + ChatColor.RED + "X", this.groupe);
                return false;
            }
            if (house.getCoffreEquipeLocation() != null) continue;
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "[Verification] spawn coffre maison equipe " + house.getTeam().getNomEquipe() + ": " + ChatColor.RED + "X", this.groupe);
            return false;
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info(mineralcontest.prefixGlobal + "[Verification] Spawn coffre arene: " + ChatColor.GREEN + "OK");
        }
        if (this.arene.getCoffre().getLocation() == null) {
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "[check] spawn coffre arene: " + ChatColor.RED + "X", this.groupe);
            return false;
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info(mineralcontest.prefixGlobal + "[Verification] Spawn coffre arene: " + ChatColor.GREEN + "OK");
        }
        if (this.arene.getTeleportSpawn() == null) {
            mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + "[check] spawn arene: " + ChatColor.RED + "X", this.groupe);
            return false;
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info(mineralcontest.prefixGlobal + "[Verification] Spawn arene: " + ChatColor.GREEN + "OK");
        }
        if (this.groupe.getParametresPartie().getCVAR("mp_randomize_team").getValeurNumerique() == 1 && !forceGameStart && !this.allPlayerHaveTeam()) {
            this.randomizeTeam(forceGameStart);
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info(mineralcontest.prefixGlobal + "GAME_SUCCESSFULLY_STARTED");
        }
        if (mineralcontest.debug) {
            mineralcontest.plugin.getServer().getLogger().info("=============================");
        }
        if (forceGameStart && !this.allPlayerHaveTeam()) {
            this.randomizeTeam(true);
        }
        MCPreGameStartEvent event = new MCPreGameStartEvent(this);
        event.callEvent();
        if (event.isCancelled()) {
            return false;
        }
        if (!forceGameStart && !this.allPlayerHaveTeam()) {
            this.groupe.sendToadmin(mineralcontest.prefixAdmin + "Il y a des joueurs sans team, la partie ne peut pas d\u00e9marrer");
            return false;
        }
        for (Player online : this.groupe.getPlayers()) {
            if (this.isReferee(online)) continue;
            PlayerUtils.setMaxHealth(online);
            online.setGameMode(GameMode.SURVIVAL);
            online.getInventory().clear();
        }
        for (House house : this.equipes) {
            house.spawnCoffreEquipe();
        }
        this.getArene().clear();
        this.initGameSettings();
        this.PreGame = true;
        this.GameStarted = false;
        this.tempsPartie = 60 * DUREE_PARTIE;
        this.getArene().startArena();
        this.getArene().startAutoMobKill();
        this.getParachuteManager().handleDrops();
        this.handleDoors();
        WorldUtils.removeAllDroppedItems(this.groupe.getMonde());
        this.groupe.removeAllDroppedItem();
        if (!mineralcontest.communityVersion) {
            Version.fetchAllMessages(mineralcontest.plugin.getMessagesFromWebsite());
            mineralcontest.afficherMessageVersion();
        }
        this.startGameLoop();
        return true;
    }

    public void randomizeTeam(boolean force) throws Exception {
        LinkedList<House> equipesDispo = new LinkedList<House>();
        LinkedList<Player> joueursEnAttente = new LinkedList<Player>();
        for (Player joueur : this.groupe.getMonde().getPlayers()) {
            if (this.isReferee(joueur)) continue;
            joueursEnAttente.add(joueur);
        }
        for (int index = 0; index < joueursEnAttente.size(); ++index) {
            equipesDispo.add(this.equipes.get(index % this.equipes.size()));
        }
        int nb_melange = 10;
        for (int i = 0; i < nb_melange; ++i) {
            Collections.shuffle(equipesDispo);
            Collections.shuffle(joueursEnAttente);
        }
        Random randomisateur = new Random();
        int numeroJoueurRandom = -1;
        int numeroEquipeRandom = -1;
        while (!equipesDispo.isEmpty()) {
            numeroJoueurRandom = randomisateur.nextInt(joueursEnAttente.size());
            numeroEquipeRandom = randomisateur.nextInt(equipesDispo.size());
            Player joueuraAttribuer = (Player)joueursEnAttente.get(numeroJoueurRandom);
            House equipeAAttribuer = (House)equipesDispo.get(numeroEquipeRandom);
            equipeAAttribuer.getTeam().addPlayerToTeam(joueuraAttribuer, true);
            equipesDispo.remove(numeroEquipeRandom);
            joueursEnAttente.remove(numeroJoueurRandom);
        }
    }

    public void switchPlayer(Player joueur, String teamName) throws Exception {
        Equipe team = this.getPlayerTeam(joueur);
        StringBuilder nomEquipes = new StringBuilder();
        for (House house : this.equipes) {
            if (ChatColorString.toString(house.getTeam().getCouleur()).equalsIgnoreCase(teamName)) {
                if (team != null) {
                    team.removePlayer(joueur);
                }
                house.getTeam().addPlayerToTeam(joueur, false);
                return;
            }
            nomEquipes.append(ChatColorString.toString(house.getTeam().getCouleur()) + ",");
        }
        this.groupe.sendToadmin(mineralcontest.prefixErreur + Lang.error_switch_fail_team_doesnt_exists.toString());
        this.groupe.sendToadmin(mineralcontest.prefixAdmin + Lang.team_available_list_text.toString());
        String nomEquipesdispo = nomEquipes.toString();
        nomEquipesdispo = nomEquipesdispo.substring(0, nomEquipesdispo.length() - 2);
        this.groupe.sendToadmin(mineralcontest.prefixAdmin + nomEquipesdispo);
    }

    public String getTempsRestant() {
        return TimeConverter.intToString(this.tempsPartie);
    }

    public static ItemStack getTeamSelectionItem() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Lang.item_team_selection_title.toString());
        item.setItemMeta(itemMeta);
        return item;
    }

    public int getTempsPartie() {
        return this.tempsPartie;
    }

    public void setGameStarted(boolean GameStarted) {
        this.GameStarted = GameStarted;
    }

    public void setGameEnded(boolean GameEnded) {
        this.GameEnded = GameEnded;
    }

    public int getDatabaseGameId() {
        return this.databaseGameId;
    }

    public void setDatabaseGameId(int databaseGameId) {
        this.databaseGameId = databaseGameId;
    }

    public PlayerBonus getPlayerBonusManager() {
        return this.playerBonusManager;
    }

    public ShopManager getShopManager() {
        return this.shopManager;
    }

    public BossManager getBossManager() {
        return this.bossManager;
    }
}

