package fr.synchroneyes.mineral;

import de.slikey.effectlib.EffectManager;
import fr.artcas2.mineralcontestcelest.commands.MapBuilderCommand;
import fr.synchroneyes.custom_events.MCGameEndEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.custom_events.MCPluginLoaded;
import fr.synchroneyes.custom_events.PermissionCheckerLoop;
import fr.synchroneyes.custom_events.PlayerLocationSaverLoop;
import fr.synchroneyes.custom_plugins.CustomPlugin;
import fr.synchroneyes.custom_plugins.CustomPluginManager;
import fr.synchroneyes.data_storage.Data_EventHandler;
import fr.synchroneyes.data_storage.DatabaseInitialisation;
import fr.synchroneyes.data_storage.SQLConnection;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.file_manager.RessourceFilesManager;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.GroupeExtension;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Commands.*;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.JoinTeamInventoryEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Core.Parachute.Events.ParachuteHitDetection;
import fr.synchroneyes.mineral.Core.Player.BaseItem.Commands.SetDefaultItems;
import fr.synchroneyes.mineral.Core.Player.BaseItem.Events.InventoryClick;
import fr.synchroneyes.mineral.Core.Referee.RefereeEvent;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimationManager;
import fr.synchroneyes.mineral.Events.ArmorStandPickup;
import fr.synchroneyes.mineral.Events.BlockDestroyed;
import fr.synchroneyes.mineral.Events.BlockPlaced;
import fr.synchroneyes.mineral.Events.BucketEvent;
import fr.synchroneyes.mineral.Events.ChestEvent;
import fr.synchroneyes.mineral.Events.EntityDamage;
import fr.synchroneyes.mineral.Events.EntityDeathEvent;
import fr.synchroneyes.mineral.Events.EntityInteract;
import fr.synchroneyes.mineral.Events.EntitySpawn;
import fr.synchroneyes.mineral.Events.EntityTarget;
import fr.synchroneyes.mineral.Events.ExplosionEvent;
import fr.synchroneyes.mineral.Events.ItemDropped;
import fr.synchroneyes.mineral.Events.MCPlayerLeavePlugin;
import fr.synchroneyes.mineral.Events.PlayerChat;
import fr.synchroneyes.mineral.Events.PlayerDeathEvent;
import fr.synchroneyes.mineral.Events.PlayerDisconnect;
import fr.synchroneyes.mineral.Events.PlayerHUDEvents;
import fr.synchroneyes.mineral.Events.PlayerInteract;
import fr.synchroneyes.mineral.Events.PlayerJoin;
import fr.synchroneyes.mineral.Events.PlayerJoinPlugin;
import fr.synchroneyes.mineral.Events.PlayerKilledByPlayer;
import fr.synchroneyes.mineral.Events.PlayerLeavePluginWorld;
import fr.synchroneyes.mineral.Events.PlayerMove;
import fr.synchroneyes.mineral.Events.PlayerPermissionChange;
import fr.synchroneyes.mineral.Events.PlayerPick;
import fr.synchroneyes.mineral.Events.PlayerSpawn;
import fr.synchroneyes.mineral.Events.PlayerWorldChangeEvent;
import fr.synchroneyes.mineral.Events.SafeZoneEvent;
import fr.synchroneyes.mineral.Events.SpeedWorldLoading;
import fr.synchroneyes.mineral.Events.Spigot_WorldChangeEvent;
import fr.synchroneyes.mineral.Events.WorldLoaded;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.DisconnectedPlayer;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.UrlFetcher.Urls;
import fr.synchroneyes.mineral.Utils.VersionChecker.Version;
import fr.synchroneyes.special_events.SpecialEventManager;
import fr.synchroneyes.world_downloader.WorldDownloader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class mineralcontest extends JavaPlugin {
    public static boolean debug = false;
    public static boolean communityVersion = false;
    public static String prefix = "[MineralContestCelest]";
    public static String prefixErreur;
    public static String prefixGlobal;
    public static String prefixPrive;
    public static String prefixAdmin;
    public static String prefixTeamChat;
    public static String prefixGroupe;
    public static String prefixWeb;
    public static Logger log;
    public static mineralcontest plugin;
    public World pluginWorld;
    public Location defaultSpawn;
    public MapBuilder mapBuilderInstance;
    public EffectManager effectManager;
    public SpecialEventManager eventManager;
    public static int player_location_hud_refresh_rate;
    public static boolean enable_block_warning;
    public static boolean enable_lobby_block_protection;
    private ArrayList<String> messagesFromWebsite;
    public GroupeExtension groupeExtension;
    public LinkedList<Groupe> groupes;
    public WorldDownloader worldDownloader;
    private List<MCPlayer> joueurs;
    private Connection connexion_database;
    private CustomPluginManager pluginManager;
    public static int min_player_per_group;
    public DeathAnimationManager deathAnimationManager;

    public mineralcontest() {
        plugin = this;
        this.joueurs = new ArrayList<MCPlayer>();
        this.pluginManager = new CustomPluginManager();
    }

    public static void afficherMessageVersion() {
        for (String message : mineralcontest.plugin.messagesFromWebsite) {
            mineralcontest.broadcastMessage(prefixWeb + message);
        }
    }

    public static void afficherMessageVersionToPlayer(Player joueur) {
        for (String message : mineralcontest.plugin.messagesFromWebsite) {
            joueur.sendMessage(prefixWeb + message);
        }
    }

    public static Object getPluginConfigValue(String configName) {
        File fichierConfigurationPlugin = new File(plugin.getDataFolder(), FileList.Config_default_plugin.toString());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)fichierConfigurationPlugin);
        return configuration.get(configName);
    }

    public static void supprimerGroupe(Groupe g) {
        mineralcontest.plugin.groupes.remove(g);
    }

    public static Groupe getPlayerGroupe(Player p) {
        mineralcontest instance = plugin;
        MCPlayer joueur = instance.getMCPlayer(p);
        if (joueur == null) {
            return null;
        }
        MCPlayer mcPlayer = plugin.getMCPlayer(p);
        if (mcPlayer == null) {
            return null;
        }
        if (!mcPlayer.isInPlugin()) {
            return null;
        }
        return joueur.getGroupe();
    }

    public static boolean enableMapBuilderPlugin() {
        Plugin mapBuilderPlugin = Bukkit.getPluginManager().getPlugin("Mapbuilder");

        if (mapBuilderPlugin == null) {
            return false;
        }

        mapBuilderPlugin.onEnable();
        HandlerList.bakeAll();

        return true;
    }

    public static boolean disableMapBuilderPlugin() {
        Plugin mapBuilderPlugin = Bukkit.getPluginManager().getPlugin("Mapbuilder");
        Field commandMapField;
        Field knownCommandsField;

        if (mapBuilderPlugin == null) {
            return false;
        }

        try {
            commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            SimpleCommandMap simpleCommandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(simpleCommandMap);
            Command buildMenuCommand = simpleCommandMap.getCommand(":buildmenu");

            if (buildMenuCommand != null) {
                buildMenuCommand.unregister(simpleCommandMap);
                knownCommands.remove(":" + buildMenuCommand.getName());
                knownCommands.remove(buildMenuCommand.getName());
                knownCommandsField.set(simpleCommandMap, knownCommands);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mapBuilderPlugin.onDisable();
        Bukkit.getScheduler().cancelTasks(mapBuilderPlugin);
        HandlerList.unregisterAll(mapBuilderPlugin);

        return true;
    }

    public Groupe getNonCommunityGroup() {
        return this.groupes.getFirst();
    }

    public void creerNouveauGroupe(Groupe nouveauGroupe) {
        if (!communityVersion) {
            return;
        }
        GameLogger.addLog(new Log("group_create", "Creating a new group with name " + nouveauGroupe.getNom(), "mineralcontest: creerNouveauGroupe"));
        for (Groupe groupe : this.groupes) {
            if (!groupe.getNom().equals(nouveauGroupe.getNom())) continue;
            groupe.sendToadmin(prefixErreur + Lang.error_group_with_this_name_already_exists.toString());
            GameLogger.addLog(new Log("error_group_create", Lang.error_group_with_this_name_already_exists.getDefault(), "mineralcontest: creerNouveauGroupe"));
            return;
        }
        while (!this.isGroupeIdentifiantUnique(nouveauGroupe)) {
            nouveauGroupe.genererIdentifiant();
        }
        this.groupes.add(nouveauGroupe);
        nouveauGroupe.sendToEveryone(prefixPrive + Lang.success_group_successfully_created.toString());
        GameLogger.addLog(new Log("group_created", "created a new group with name " + nouveauGroupe.getNom(), "mineralcontest: creerNouveauGroupe"));
    }

    private boolean isGroupeIdentifiantUnique(Groupe groupe) {
        for (Groupe g : this.groupes) {
            if (!g.getIdentifiant().equalsIgnoreCase(groupe.getIdentifiant())) continue;
            return false;
        }
        return true;
    }

    public void initCommunityVersion() {
        if (!communityVersion) {
            Groupe defaut = new Groupe();
            defaut.setEtat(Etats.EN_ATTENTE);
            defaut.setNom("MineralContest");
            this.groupes.add(defaut);
            if (this.pluginWorld == null) {
                this.pluginWorld = PlayerUtils.getPluginWorld();
            }
            if (this.pluginWorld == null) {
                return;
            }
            for (Player joueur : mineralcontest.plugin.pluginWorld.getPlayers()) {
                if (joueur.isOp()) {
                    this.getNonCommunityGroup().addAdmin(joueur);
                    continue;
                }
                this.getNonCommunityGroup().addJoueur(joueur);
            }
        }
    }

    public void onEnable() {
        RessourceFilesManager.createDefaultFiles();
        this.writeNonExistingConfigValuesToConfigFile();
        Lang.loadLang(mineralcontest.getPluginConfigValue("language").toString());
        min_player_per_group = Integer.parseInt(mineralcontest.getPluginConfigValue("min_player_per_group").toString());
        player_location_hud_refresh_rate = Integer.parseInt(mineralcontest.getPluginConfigValue("player_location_refresh_rate").toString());
        enable_block_warning = Boolean.parseBoolean(mineralcontest.getPluginConfigValue("enable_block_warning").toString());
        enable_lobby_block_protection = Boolean.parseBoolean(mineralcontest.getPluginConfigValue("enable_lobby_block_protection").toString());
        communityVersion = (Boolean)mineralcontest.getPluginConfigValue("enable_community_version");
        Bukkit.getLogger().info("Version communautaire: " + communityVersion);
        this.groupes = new LinkedList();
        this.groupeExtension = GroupeExtension.getInstance();
        this.registerCommands();
        this.registerEvents();
        if (((Boolean)mineralcontest.getPluginConfigValue("enable_mysql_storage")).booleanValue()) {
            this.connexion_database = SQLConnection.getInstance();
            if (this.connexion_database != null) {
                try {
                    DatabaseInitialisation.createDatabase();
                    Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GREEN + " Successfully connected to MySQL database");
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        this.initCommunityVersion();
        this.effectManager = new EffectManager((Plugin)this);
        this.deathAnimationManager = new DeathAnimationManager();
        this.mapBuilderInstance = MapBuilder.getInstance();
        this.messagesFromWebsite = new ArrayList();
        this.worldDownloader = WorldDownloader.getInstance();
        this.eventManager = new SpecialEventManager();
        PermissionCheckerLoop permissionCheckerLoop = new PermissionCheckerLoop(this, 1);
        permissionCheckerLoop.run();
        PlayerLocationSaverLoop playerLocationSaverLoop = new PlayerLocationSaverLoop(this, 1);
        playerLocationSaverLoop.run();
        this.eventManager.init();
        this.pluginWorld = PlayerUtils.getPluginWorld();
        Location location = this.defaultSpawn = this.pluginWorld != null ? this.pluginWorld.getSpawnLocation() : null;
        if (this.pluginWorld != null) {
            this.pluginWorld.setDifficulty(Difficulty.PEACEFUL);
        }
        if (!debug && !communityVersion && this.pluginWorld != null) {
            for (Player online : this.pluginWorld.getPlayers()) {
                PlayerUtils.teleportPlayer(online, this.defaultSpawn.getWorld(), this.defaultSpawn);
                if (online.isOp()) {
                    this.getNonCommunityGroup().addAdmin(online);
                } else {
                    this.getNonCommunityGroup().addJoueur(online);
                }
                MCPlayerJoinEvent event = new MCPlayerJoinEvent(online);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
            }
        }
        PlayerUtils.runScoreboardManager();
        GameLogger.addLog(new Log("server_event", "OnEnable", "plugin_startup"));
        if (((Boolean)mineralcontest.getPluginConfigValue("enable_auto_update")).booleanValue()) {
            this.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
                disableMapBuilderPlugin();

                Version.isCheckingStarted = true;
                Thread operationsThreade = new Thread(() -> {
                    Urls.FetchAllUrls();
                    if (Urls.isWebsiteDown) {
                        Bukkit.broadcastMessage((String)(ChatColor.RED + Urls.WEBSITE_URL + " is down. Please check on our discord to get the latest plugin version & maps mirrors link"));
                        return;
                    }
                    this.worldDownloader.initMapLists();
                    Version.fetchAllMessages(this.messagesFromWebsite);
                    mineralcontest.afficherMessageVersion();
                    Version.Check(true);
                });
                operationsThreade.start();
                new BukkitRunnable(){

                    public void run() {
                        if (Version.isCheckingStarted && !Urls.isWebsiteDown) {
                            if (Version.hasUpdated) {
                                Bukkit.reload();
                            }
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer((Plugin)this, 20L, 20L);
            });
        }
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks((Plugin)this);
        GameLogger.addLog(new Log("server_event", "OnDisable", "plugin_shutdown"));
        if (this.groupes.isEmpty()) {
            return;
        }
        for (Groupe groupe : this.groupes) {
            Game game = groupe.getGame();
            MCGameEndEvent endEvent = new MCGameEndEvent(game);
            if (game.isGameStarted()) {
                Bukkit.getPluginManager().callEvent((Event)endEvent);
            }
            if (this.pluginWorld == null || debug) continue;
            for (Player player : this.pluginWorld.getPlayers()) {
                game.teleportToLobby(player);
                PlayerUtils.clearPlayer(player, true);
            }
        }
    }

    private void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ArmorStandPickup(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new BlockDestroyed(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new BlockPlaced(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new EntityInteract(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerInteract(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new BucketEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ChestEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new EntityDamage(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new EntityTarget(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new EntitySpawn(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new EntityDeathEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ExplosionEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ItemDropped(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerDisconnect(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerKilledByPlayer(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerJoin(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerMove(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerSpawn(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerDeathEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new SafeZoneEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerChat(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new RefereeEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerPick(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new WorldLoaded(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerPermissionChange(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new InventoryClick(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new JoinTeamInventoryEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new ParachuteHitDetection(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new Data_EventHandler(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new Spigot_WorldChangeEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerWorldChangeEvent(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerLeavePluginWorld(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinPlugin(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new SpeedWorldLoading(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new MCPlayerLeavePlugin(), (Plugin)this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayerHUDEvents(), (Plugin)this);
    }

    private void registerCommands() {
        this.getCommand("mapbuilder").setExecutor((CommandExecutor)new MapBuilderCommand());
        this.getCommand("start").setExecutor((CommandExecutor)new StartGameCommand());
        this.getCommand("pause").setExecutor((CommandExecutor)new PauseGameCommand());
        this.getCommand("stopGame").setExecutor((CommandExecutor)new StopGameCommand());
        this.getCommand("arene").setExecutor((CommandExecutor)new AreneTeleportCommand());
        this.getCommand("arena").setExecutor((CommandExecutor)new AreneTeleportCommand());
        this.getCommand("join").setExecutor((CommandExecutor)new JoinCommand());
        this.getCommand("ready").setExecutor((CommandExecutor)new ReadyCommand());
        this.getCommand("t").setExecutor((CommandExecutor)new TeamChat());
        this.getCommand("team").setExecutor((CommandExecutor)new TeamChat());
        this.getCommand("switch").setExecutor((CommandExecutor)new SwitchCommand());
        this.getCommand("resume").setExecutor((CommandExecutor)new ResumeGameCommand());
        Field cmdMapField = null;
        try {
            cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
            cmdMapField.setAccessible(true);
            CommandMap bukkitCommandMap = (CommandMap)cmdMapField.get(Bukkit.getPluginManager());
            bukkitCommandMap.register("", (Command)new MCCvarCommand());
            bukkitCommandMap.register("", (Command)new SetDefaultItems());
            bukkitCommandMap.register("", (Command)new RefereeCommand());
            bukkitCommandMap.register("", (Command)new McStats());
            bukkitCommandMap.register("", (Command)new DisplayScoreCommand());
            bukkitCommandMap.register("", (Command)new SelectDeathAnimationCommand());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        this.getCommand("spawnchest").setExecutor((CommandExecutor)new SpawnChestCommand());
        this.getCommand("allow").setExecutor((CommandExecutor)new AllowCommand());
        this.getCommand("leaveteam").setExecutor((CommandExecutor)new LeaveTeamCommand());
    }

    public void setDefaultWorldBorder() {
        if (this.mapBuilderInstance.isBuilderModeEnabled) {
            return;
        }
        World game_world = mineralcontest.plugin.pluginWorld;
        int size = 30000000;
        if (game_world != null) {
            game_world.getWorldBorder().setCenter(mineralcontest.plugin.defaultSpawn);
            game_world.getWorldBorder().setSize((double)size);
        }
    }

    public static void broadcastMessage(String message, Groupe groupe) {
        groupe.sendToEveryone(message);
        Bukkit.getConsoleSender().sendMessage(message);
        GameLogger.addLog(new Log("broadcast-group", message, "server"));
    }

    public static void broadcastMessage(String message) {
        for (Player joueur : Bukkit.getOnlinePlayers()) {
            if (!mineralcontest.isInAMineralContestWorld(joueur)) continue;
            joueur.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
        GameLogger.addLog(new Log("broadcast-plugin", message, "server"));
    }

    public static boolean isInAMineralContestWorld(Player p) {
        if (p.getWorld().equals((Object)mineralcontest.plugin.pluginWorld)) {
            return true;
        }
        for (Groupe groupe : mineralcontest.plugin.groupes) {
            if (groupe.getMonde() == null || !groupe.getMonde().equals((Object)p.getWorld())) continue;
            return true;
        }
        return false;
    }

    public static boolean isInMineralContestHub(Player p) {
        return p.getWorld().equals((Object)mineralcontest.plugin.pluginWorld);
    }

    public static boolean isAMineralContestWorld(World w) {
        if (w.equals((Object)mineralcontest.plugin.pluginWorld)) {
            return true;
        }
        for (Groupe groupe : mineralcontest.plugin.groupes) {
            if (groupe.getMapName().equals(w.getName())) {
                return true;
            }
            if (w.getName().contains(groupe.getIdentifiant())) {
                return true;
            }
            if (groupe.getMonde() == null) {
                return false;
            }
            if (!w.equals((Object)groupe.getMonde())) continue;
            return true;
        }
        return w.equals((Object)mineralcontest.plugin.pluginWorld);
    }

    public static Game getPlayerGame(Player p) {
        MCPlayer mcPlayer = plugin.getMCPlayer(p);
        if (mcPlayer == null) {
            return null;
        }
        if (!mcPlayer.isInPlugin()) {
            return null;
        }
        Groupe g = mineralcontest.getPlayerGroupe(p);
        if (g != null) {
            return g.getGame();
        }
        return null;
    }

    public static Game getWorldGame(World world) {
        for (Groupe groupe : mineralcontest.plugin.groupes) {
            if (groupe.getMonde() == null && world != mineralcontest.plugin.pluginWorld) {
                return null;
            }
            if (groupe.getMonde() == null && world == mineralcontest.plugin.pluginWorld) {
                return groupe.getGame();
            }
            if (!groupe.getMonde().equals((Object)world)) continue;
            return groupe.getGame();
        }
        return null;
    }

    public void addNewPlayer(Player nouveauJoueur) {
        for (MCPlayer joueur : this.joueurs) {
            if (!joueur.getJoueur().equals((Object)nouveauJoueur)) continue;
            joueur.setInPlugin(true);
            return;
        }
        MCPlayer joueur = new MCPlayer(nouveauJoueur);
        this.joueurs.add(joueur);
    }

    public void removePlayer(Player joueur) {
        for (MCPlayer _joueur : this.joueurs) {
            if (!_joueur.getJoueur().equals((Object)joueur)) continue;
            _joueur.setInPlugin(false);
            return;
        }
    }

    public MCPlayer getMCPlayer(Player joueur) {
        for (MCPlayer _joueur : this.joueurs) {
            if (!_joueur.getJoueur().equals((Object)joueur)) continue;
            return _joueur;
        }
        return null;
    }

    public DisconnectedPlayer wasPlayerDisconnected(Player p) {
        for (Groupe groupe : this.groupes) {
            if (!groupe.havePlayerDisconnected(p)) continue;
            return groupe.getDisconnectedPlayerInfo(p);
        }
        return null;
    }

    public List<MCPlayer> getMCPlayers() {
        LinkedList<MCPlayer> mcPlayers = new LinkedList<MCPlayer>(this.joueurs);
        mcPlayers.removeIf(joueur -> !joueur.isInPlugin());
        return mcPlayers;
    }

    private void writeNonExistingConfigValuesToConfigFile() {
        try {
            File tmp_default_config_file = new File(this.getDataFolder(), "tmp_default_config_file");
            InputStream fichier_config_default = ((Object)((Object)this)).getClass().getClassLoader().getResourceAsStream("config/plugin_config.yml");
            if (fichier_config_default == null) {
                return;
            }
            byte[] buffer = new byte[fichier_config_default.available()];
            fichier_config_default.read(buffer);
            FileOutputStream outputStream = new FileOutputStream(tmp_default_config_file);
            ((OutputStream)outputStream).write(buffer);
            ((OutputStream)outputStream).close();
            File fichier_config_existant = new File(this.getDataFolder(), FileList.Config_default_plugin.toString());
            YamlConfiguration config_fichier_existant = YamlConfiguration.loadConfiguration((File)fichier_config_existant);
            YamlConfiguration config_fichier_default = YamlConfiguration.loadConfiguration((File)tmp_default_config_file);
            for (String valeur : config_fichier_default.getKeys(false)) {
                if (config_fichier_existant.get(valeur) != null) continue;
                config_fichier_existant.set(valeur, config_fichier_default.get(valeur));
            }
            config_fichier_existant.save(fichier_config_existant);
            tmp_default_config_file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerNewPlugin(CustomPlugin plugin) {
        this.pluginManager.registerPlugin(plugin);
        MCPluginLoaded mcPluginLoaded = new MCPluginLoaded(plugin);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> Bukkit.getPluginManager().callEvent((Event)mcPluginLoaded), 40L);
    }

    public ArrayList<String> getMessagesFromWebsite() {
        return this.messagesFromWebsite;
    }

    public LinkedList<Groupe> getGroupes() {
        return this.groupes;
    }

    public Connection getConnexion_database() {
        return this.connexion_database;
    }

    static {
        log = Bukkit.getLogger();
        player_location_hud_refresh_rate = 10;
        enable_block_warning = false;
        enable_lobby_block_protection = true;
        min_player_per_group = 3;
    }
}

