package fr.synchroneyes.groups.Core;

import fr.synchroneyes.custom_events.MCWorldLoadedEvent;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.groups.Utils.FileManager.FileCopy;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.Referee.Referee;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldLoader {
    private Groupe groupe;
    private String nomMonde;
    private Location spawnLocation;
    protected static int defaultX = 999999;
    protected static int defaultY = 150;
    protected static int defaultZ = 999999;
    private static String folder_name = mineralcontest.plugin.getDataFolder() + File.separator + "worlds" + File.separator;

    public WorldLoader(Groupe g) {
        this.groupe = g;
    }

    public World chargerMonde(String nomMap, String identifiant) throws Exception {
        File[] maps;
        File dossierMaps = new File(folder_name);
        for (File map : maps = dossierMaps.listFiles()) {
            if (!map.isDirectory() || !nomMap.equalsIgnoreCase(map.getName())) continue;
            return this.doChargerMonde(nomMap, identifiant);
        }
        return null;
    }

    private World doChargerMonde(String nomMap, String identifiant) throws Exception {
        String server_executable_path = System.getProperty("user.dir") + File.separator;
        File dossierMondeACopier = new File(folder_name + nomMap);
        this.nomMonde = nomMap;
        String nomMondeDossier = server_executable_path + nomMap + "_" + identifiant;
        File repertoireServer = new File(nomMondeDossier);
        try {
            FileCopy.copyDirectoryContent(dossierMondeACopier, repertoireServer);
            File uidDat = new File(dossierMondeACopier, "uid.dat");
            File level_dat_new = new File(dossierMondeACopier, "level.dat_new");
            if (!level_dat_new.exists()) {
                level_dat_new.createNewFile();
            }
            uidDat.delete();
            WorldCreator wc = new WorldCreator(nomMap + "_" + identifiant);
            World createdWorld = Bukkit.getServer().createWorld(wc);
            createdWorld.setDifficulty(Difficulty.NORMAL);
            this.lireConfigurationPartie();
            this.lireFichierMonde(nomMondeDossier, createdWorld);
            this.lireFichierConfigurationContenuCoffreArene(nomMondeDossier, createdWorld);
            if (this.spawnLocation != null) {
                this.spawnLocation.setWorld(createdWorld);
            } else {
                this.spawnLocation = new Location(createdWorld, (double)defaultX, (double)defaultY, (double)defaultZ);
            }
            createdWorld.setSpawnLocation(this.spawnLocation);
            createdWorld.setAutoSave(false);
            MCWorldLoadedEvent mcWorldLoadedEvent = new MCWorldLoadedEvent(nomMap, createdWorld, this.groupe);
            Bukkit.getPluginManager().callEvent((Event)mcWorldLoadedEvent);
            return createdWorld;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    protected void chargerMondeThreade(final String nomMap, final Groupe groupe) {
        File[] maps;
        BukkitRunnable chargementMap = new BukkitRunnable(){

            public void run() {
                try {
                    World mondeCharge = WorldLoader.this.doChargerMonde(nomMap, groupe.getIdentifiant());
                    mondeCharge.setAutoSave(false);
                    groupe.setGameWorld(mondeCharge);
                    groupe.getGame().setGameEnded(false);
                    groupe.getGame().setGameStarted(false);
                    groupe.setEtat(Etats.ATTENTE_DEBUT_PARTIE);
                    Location worldSpawnLocation = mondeCharge.getSpawnLocation();
                    try {
                        if (worldSpawnLocation.getX() == (double)defaultX && worldSpawnLocation.getY() == (double)defaultY && worldSpawnLocation.getZ() == (double)defaultZ) {
                            worldSpawnLocation = groupe.getGame().getArene().getCoffre().getLocation();
                        }
                    } catch (Exception e) {
                        worldSpawnLocation = mondeCharge.getSpawnLocation();
                    }
                    for (Player joueur : groupe.getPlayers()) {
                        joueur.getInventory().clear();
                        if (groupe.getGame().isReferee(joueur)) {
                            joueur.getInventory().setItemInMainHand(Referee.getRefereeItem());
                        } else if (groupe.getParametresPartie().getCVAR("mp_randomize_team").getValeurNumerique() == 0) {
                            joueur.getInventory().setItemInMainHand(Game.getTeamSelectionItem());
                        }
                        joueur.teleport(worldSpawnLocation);
                        joueur.sendMessage(mineralcontest.prefixPrive + Lang.set_yourself_as_ready_to_start_game.toString());
                    }
                    groupe.setMapName(WorldLoader.this.nomMonde);
                    if (groupe.getMapVote() != null) {
                        groupe.getMapVote().clearVotes();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        File dossierMaps = new File(folder_name);
        for (File map : maps = dossierMaps.listFiles()) {
            if (!map.isDirectory() || !nomMap.equalsIgnoreCase(map.getName())) continue;
            CompletableFuture.runAsync(() -> chargementMap.runTask((Plugin)mineralcontest.plugin));
            break;
        }
    }

    public void supprimerMonde(World world) {
        String nomMonde = world.getName();
        try {
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + File.separator + nomMonde));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void lireFichierMonde(String nomDossier, World monde) throws Exception {
        boolean spawnNPC;
        String nomFichierConfig = "mc_world_settings.yml";
        for (Entity entity : monde.getEntities()) {
            if (!(entity instanceof Villager)) continue;
            entity.remove();
        }
        boolean loadNPC = true;
        int rayonDeBloc = 20;
        ArrayList<Block> chestToAdd = new ArrayList<Block>();
        File fichierConfigMonde = new File(nomDossier + File.separator + nomFichierConfig);
        if (!fichierConfigMonde.exists()) {
            throw new Exception(nomFichierConfig + " doesnt exists in world folder. Can't load world settings");
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierConfigMonde);
        ConfigurationSection arene = yamlConfiguration.getConfigurationSection("arena");
        ConfigurationSection houses = yamlConfiguration.getConfigurationSection("house");
        ConfigurationSection npcs = yamlConfiguration.getConfigurationSection("npcs");
        ConfigurationSection settings = yamlConfiguration.getConfigurationSection("settings");
        if (arene == null) {
            throw new Exception("Unable to load \"arena\" section from " + nomFichierConfig + ". World file settings is not correct.");
        }
        if (houses == null) {
            throw new Exception("Unable to load \"house\" section from " + nomFichierConfig + ". World file settings is not correct.");
        }
        if (npcs == null) {
            loadNPC = false;
        }
        if (settings == null) {
            throw new Exception("Unable to load \"npcs\" section from " + nomFichierConfig + ". World file settings is not correct.");
        }
        boolean bl = spawnNPC = this.groupe.getParametresPartie().getCVAR("enable_kits").getValeurNumerique() == 1;
        if (yamlConfiguration.getConfigurationSection("default_spawn") == null) {
            this.spawnLocation = null;
        } else {
            ConfigurationSection spawn_loc = yamlConfiguration.getConfigurationSection("default_spawn");
            Object loc = null;
            if (spawn_loc.get("x") != null) {
                this.spawnLocation = new Location(null, Double.parseDouble(spawn_loc.get("x").toString()), Double.parseDouble(spawn_loc.get("y").toString()), Double.parseDouble(spawn_loc.get("z").toString()));
            }
        }
        ConfigurationSection arena_chest = yamlConfiguration.getConfigurationSection("arena.chest");
        Location chestLocation = new Location(monde, Double.parseDouble(arena_chest.get("x").toString()), Double.parseDouble(arena_chest.get("y").toString()), Double.parseDouble(arena_chest.get("z").toString()));
        ConfigurationSection arena_teleport = yamlConfiguration.getConfigurationSection("arena.teleport");
        Location teleportLocation = new Location(monde, Double.parseDouble(arena_teleport.get("x").toString()), Double.parseDouble(arena_teleport.get("y").toString()), Double.parseDouble(arena_teleport.get("z").toString()));
        Game partie = this.groupe.getGame();
        partie.getArene().setCoffre(chestLocation);
        partie.getArene().setTeleportSpawn(teleportLocation);
        for (String nomEquipe : houses.getKeys(false)) {
            String teamColorString = houses.get(nomEquipe + ".color").toString().replace("\u00a7", "");
            ChatColor couleur = ChatColor.getByChar((String)teamColorString);
            House nouvelleEquipe = new House(nomEquipe, couleur, this.groupe);
            Location chestLoc = new Location(monde, Double.parseDouble(houses.get(nomEquipe + ".coffre.x").toString()), Double.parseDouble(houses.get(nomEquipe + ".coffre.y").toString()), Double.parseDouble(houses.get(nomEquipe + ".coffre.z").toString()));
            Location spawnLoc = new Location(monde, Double.parseDouble(houses.get(nomEquipe + ".spawn.x").toString()), Double.parseDouble(houses.get(nomEquipe + ".spawn.y").toString()), Double.parseDouble(houses.get(nomEquipe + ".spawn.z").toString()));
            for (String idPorte : houses.getConfigurationSection(nomEquipe + ".porte").getKeys(false)) {
                ConfigurationSection configPorte = houses.getConfigurationSection(nomEquipe + ".porte");
                Location locPorte = new Location(monde, Double.parseDouble(configPorte.get(idPorte + ".x").toString()), Double.parseDouble(configPorte.get(idPorte + ".y").toString()), Double.parseDouble(configPorte.get(idPorte + ".z").toString()));
                nouvelleEquipe.getPorte().addToDoor(locPorte.getBlock());
            }
            chestToAdd.addAll(WorldLoader.getNearbyBlocksByMaterial(Material.CHEST, spawnLoc, rayonDeBloc));
            nouvelleEquipe.setCoffreEquipe(chestLoc);
            nouvelleEquipe.setHouseLocation(spawnLoc);
            if (mineralcontest.debug) {
                this.groupe.sendToadmin(mineralcontest.prefixPrive + "L'\u00e9quipe " + couleur + nomEquipe + ChatColor.WHITE + " a bien \u00e9t\u00e9 cr\u00e9e");
            }
            this.groupe.getGame().addEquipe(nouvelleEquipe);
        }
        for (Block block : chestToAdd) {
            if (partie.isThisChestAlreadySaved(block)) continue;
            partie.addAChest(block);
        }
        if (!chestToAdd.isEmpty()) {
            Bukkit.getLogger().info(mineralcontest.prefix + " Allowed " + chestToAdd.size() + " chests to be opened");
        }
        ShopManager shopManager = this.groupe.getGame().getShopManager();
        if (loadNPC) {
            for (String idNpc : npcs.getKeys(false)) {
                ConfigurationSection npc = npcs.getConfigurationSection(idNpc);
                Location npcLocation = new Location(monde, 0.0, 0.0, 0.0);
                npcLocation.setX((double)Float.parseFloat(npc.get("x").toString()));
                npcLocation.setY((double)Float.parseFloat(npc.get("y").toString()));
                npcLocation.setZ((double)Float.parseFloat(npc.get("z").toString()));
                npcLocation.setYaw(Float.parseFloat(npc.get("yaw").toString()));
                npcLocation.setPitch(Float.parseFloat(npc.get("pitch").toString()));
                BonusSeller vendeur = ShopManager.creerVendeur(npcLocation);
                shopManager.ajouterVendeur(vendeur);
                vendeur.spawn();
            }
        }
        this.groupe.getParametresPartie().setCVARValeur("mp_set_playzone_radius", settings.get("mp_set_playzone_radius").toString());
        this.groupe.getParametresPartie().setCVARValeur("protected_zone_area_radius", settings.get("protected_zone_area_radius").toString());
        this.groupe.getGame().isGameInitialized = true;
        this.groupe.setEtat(Etats.ATTENTE_DEBUT_PARTIE);
    }

    private void lireConfigurationPartie() {
        YamlConfiguration yamlConfiguration;
        ConfigurationSection config;
        GameSettings parametres = this.groupe.getParametresPartie();
        String nomFichierConfig = "mc_game_settings.yml";
        File fichierConfigPartie = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_game.toString());
        if (!fichierConfigPartie.exists()) {
            this.groupe.sendToadmin(mineralcontest.prefixAdmin + Lang.error_cant_load_game_settings_file.toString());
            fichierConfigPartie = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_game.toString());
        }
        if ((config = (yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierConfigPartie)).getConfigurationSection("config")) == null) {
            this.groupe.sendToadmin(mineralcontest.prefixAdmin + Lang.error_cant_load_game_settings_file.toString());
            return;
        }
        for (String section : config.getKeys(false)) {
            for (String variable : config.getConfigurationSection(section).getKeys(false)) {
                try {
                    parametres.setCVARValeur(variable, (String)config.get(section + "." + variable));
                } catch (Exception exception) {}
            }
        }
        this.groupe.sendToadmin(mineralcontest.prefixGroupe + "Les param\u00e8tres de la carte ont bien \u00e9t\u00e9 charg\u00e9!");
    }

    private void lireFichierConfigurationContenuCoffreArene(String nomDossier, World monde) {
        GameSettings parametres = this.groupe.getParametresPartie();
        String nomFichierConfig = "mc_arena_chest_content.yml";
        File fichierConfigPartie = new File(nomDossier + File.separator + nomFichierConfig);
        if (!fichierConfigPartie.exists()) {
            fichierConfigPartie = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_arena_chest.toString());
        }
        try {
            CoffreArene coffreArene = (CoffreArene)this.groupe.getGame().getArene().getCoffre();
            coffreArene.getArenaChestContentGenerator().initialize(fichierConfigPartie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ChatColor toChatColor(String v) {
        for (ChatColor couleur : ChatColor.values()) {
            Bukkit.getLogger().info(couleur.getChar() + " == v: " + v);
        }
        return null;
    }

    private static List<Block> getNearbyBlocksByMaterial(Material itemMaterial, Location location, int radius) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; ++x) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; ++y) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; ++z) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (!block.getType().equals((Object)itemMaterial)) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
}

