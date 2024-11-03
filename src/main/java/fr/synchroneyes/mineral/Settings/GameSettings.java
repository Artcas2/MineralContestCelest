package fr.synchroneyes.mineral.Settings;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Settings.GameCVAR;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GameSettings {
    private LinkedList<GameCVAR> parametres;
    private static LinkedList<GameCVAR> parametresParDefaut;
    private File fichierConfiguration;
    private static LinkedList<GameCVAR> parametresExclu;
    private Groupe groupe;

    public GameSettings(boolean loadDefaultSettings, Groupe g) {
        if (parametresExclu == null) {
            parametresExclu = new LinkedList();
            parametresExclu.add(new GameCVAR("chest_content", "", "", "arena", false, false));
        }
        this.parametres = new LinkedList();
        if (loadDefaultSettings) {
            for (GameCVAR parametre : GameSettings.getParametresParDefaut()) {
                this.parametres.add(new GameCVAR(parametre.getCommand(), parametre.getValeur(), parametre.getDescription(), parametre.getType(), parametre.canBeReloaded(), parametre.isNumber()));
            }
        }
        this.fichierConfiguration = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_game.toString());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)this.fichierConfiguration);
        if (configuration != null) {
            for (GameCVAR parametre : this.parametres) {
                if (configuration.contains("config." + parametre.getType() + "." + parametre.getCommand())) continue;
                this.saveCVAR(parametre);
            }
        }
        this.groupe = g;
    }

    private static boolean isCvarExcluded(GameCVAR cvar) {
        for (GameCVAR paramtreExclu : parametresExclu) {
            if (!paramtreExclu.getCommand().equalsIgnoreCase(cvar.getCommand())) continue;
            return true;
        }
        return false;
    }

    public LinkedList<GameCVAR> getParametres() {
        return this.parametres;
    }

    public GameCVAR getCVAR(String cvar) {
        for (GameCVAR parametre : this.parametres) {
            if (!parametre.getCommand().equalsIgnoreCase(cvar)) continue;
            return parametre;
        }
        Bukkit.getLogger().severe(mineralcontest.prefixErreur + "Invalid cvar name: \"" + cvar + "\"");
        return null;
    }

    public void setCVARValeur(String cvar, String valeur) throws Exception {
        GameCVAR gameCVAR = null;
        for (GameCVAR parametre : this.parametres) {
            if (!parametre.getCommand().equalsIgnoreCase(cvar)) continue;
            gameCVAR = parametre;
            gameCVAR.setValeur(valeur);
            if (parametre.getCommand().equalsIgnoreCase("mp_enable_old_pvp")) {
                for (Player player : this.groupe.getPlayers()) {
                    if (parametre.getValeurNumerique() == 1) {
                        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0);
                        continue;
                    }
                    player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
                }
            }
            if (parametre.getCommand().equalsIgnoreCase("enable_kits")) {
                if (parametre.getValeurNumerique() == 1) {
                    this.groupe.getKitManager().setKitsEnabled(true);
                } else {
                    this.groupe.getKitManager().setKitsEnabled(false);
                }
            }
            if (!parametre.getCommand().equalsIgnoreCase("enable_shop")) break;
            if (parametre.getValeurNumerique() == 1) {
                this.groupe.getGame().getShopManager().enableShop();
                break;
            }
            this.groupe.getGame().getShopManager().disableShop();
            break;
        }
        if (gameCVAR == null) {
            throw new Exception("Impossible d'appliquer la valeur " + valeur + " \u00e0 " + cvar + " car ce param\u00e8tre est inconnu");
        }
    }

    public boolean doesCvarExists(GameCVAR cvar) {
        for (GameCVAR parametre : this.parametres) {
            if (!parametre.getCommand().equalsIgnoreCase(cvar.getCommand())) continue;
            return true;
        }
        return false;
    }

    public void saveToFile(String nomDeFichier, boolean saveDefaultPluginSetting) {
        GameLogger.addLog(new Log("game_cvar", "About to save current configuration into a file named " + nomDeFichier + ".yml", "GameSettings: saveToFile"));
        File dossierConfiguration = null;
        dossierConfiguration = saveDefaultPluginSetting ? mineralcontest.plugin.getDataFolder() : new File(mineralcontest.plugin.getDataFolder() + File.separator + "saved-configs");
        if (!dossierConfiguration.exists()) {
            dossierConfiguration.mkdir();
        }
        File nouveauFichierConfig = new File(dossierConfiguration, nomDeFichier + ".yml");
        GameLogger.addLog(new Log("game_cvar", "Created file " + nomDeFichier + ".yml in folder " + dossierConfiguration.getAbsolutePath(), "GameSettings: saveToFile"));
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)nouveauFichierConfig);
        ConfigurationSection sectionConfig = yamlConfiguration.createSection("config");
        for (GameCVAR parametre : this.parametres) {
            sectionConfig.set(parametre.getType() + "." + parametre.getCommand(), (Object)parametre.getValeur());
        }
        try {
            yamlConfiguration.save(nouveauFichierConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCVAR(GameCVAR cvar) {
        File fichierConfig = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_game.toString());
        if (!fichierConfig.exists()) {
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierConfig);
        String file_cvar = "config." + cvar.getType() + "." + cvar.getCommand();
        yamlConfiguration.set(file_cvar, (Object)cvar.getValeur());
        try {
            yamlConfiguration.save(fichierConfig);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void loadFromFile(File fichierDeConfiguration) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierDeConfiguration);
        ConfigurationSection config = yamlConfiguration.createSection("config");
        Bukkit.getLogger().info(mineralcontest.prefix + "Loading CVARS from file " + fichierDeConfiguration.getAbsolutePath());
        this.parametres.clear();
        for (String typeParametre : config.getKeys(false)) {
            for (String nomParamtre : config.getConfigurationSection(typeParametre).getKeys(false)) {
                boolean isNumber = false;
                String valeur = (String)config.get(typeParametre + "." + nomParamtre);
                GameCVAR cvar = new GameCVAR(nomParamtre, valeur, "", typeParametre, true, isNumber = StringUtils.isNumeric(valeur));
                if (this.doesCvarExists(cvar) || GameSettings.isCvarExcluded(cvar)) continue;
                this.parametres.add(cvar);
                GameLogger.addLog(new Log("game_cvar", "Loaded " + typeParametre + "." + nomParamtre + " (value: " + valeur + ") from configuration file " + fichierDeConfiguration.getAbsolutePath(), "GameSettings: loadConfigFromFile"));
                Bukkit.getLogger().info(mineralcontest.prefix + "Loaded CVAR " + typeParametre + "." + nomParamtre + " with value: " + valeur);
            }
        }
        GameLogger.addLog(new Log("game_cvar", "Loading game cvar ended with success", "GameSettings: loadConfigFromFile"));
        Bukkit.getLogger().info(mineralcontest.prefix + "Loading CVARS from file " + fichierDeConfiguration.getAbsolutePath() + " done !");
    }

    public static LinkedList<GameCVAR> getParametresParDefaut() {
        if (parametresParDefaut == null) {
            parametresParDefaut = new LinkedList();
        }
        if (parametresParDefaut.isEmpty()) {
            GameLogger.addLog(new Log("game_cvar", "Adding default game cvars ...", "GameSettings: getParametresParDefaut"));
            parametresParDefaut.add(new GameCVAR("mp_randomize_team", "0", "Permet d'activer ou non les \u00e9quipes al\u00e9atoires", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("mp_enable_item_drop", "2", "Permet d'activer ou non le drop d'item \u00e0 la mort. 0 pour aucun, 1 pour les minerais uniquement, 2 pour tout", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("SCORE_IRON", "10", "Permet de d\u00e9finir le score pour un lingot de fer", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("SCORE_GOLD", "50", "Permet de d\u00e9finir le score pour un lingot d'or", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("SCORE_DIAMOND", "150", "Permet de d\u00e9finir le score pour un diamant", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("SCORE_EMERALD", "300", "Permet de d\u00e9finir le score pour un \u00e9meraude", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("SCORE_REDSTONE", "-3", "Permet de d\u00e9finir le score pour une redstone", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("mp_set_playzone_radius", "1000", "Permet de d\u00e9finir le rayon de la zone jouable en nombre de bloc", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("mp_enable_friendly_fire", "0", "Permet d'activer ou non les d\u00e9gats entre alli\u00e9s", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("mp_enable_old_pvp", "1", "Permet d'activer ou non l'ancien syst\u00e8me de pvp", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("mp_enable_block_adding", "0", "Permet d'activer ou non la pose de bloc autour de l'ar\u00e8ne", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("game_time", "60", "Permet de d\u00e9finir le temps d'une partie", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("pre_game_timer", "10", "Permet de d\u00e9finir le temps d'attente avant de d\u00e9marrer la partie", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("end_game_timer", "60", "Permet de d\u00e9finir le temps avant de quitter le monde \u00e0 la fin d'une partie", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("chest_opening_cooldown", "5", "Permet de d\u00e9finir le temps d'attente avant d'ouvrir un coffre d'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("max_time_between_chests", "15", "Permet de d\u00e9finir le temps maximum avant l'apparition d'un coffre d'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("min_time_between_chests", "10", "Permet de d\u00e9finir le temps minimum avant l'apparition d'un coffre d'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("max_teleport_time", "15", "Permet de d\u00e9finir le temps maximum afin de pouvoir se t\u00e9l\u00e9porter apr\u00e8s l'apparition d'un coffre", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("max_item_in_chest", "20", "Permet de d\u00e9finir le nombre maximum d'objet dans un coffre d'\u00e9quipe", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("min_item_in_chest", "10", "Permet de d\u00e9finir le nombre minimum d'objet dans un coffre d'\u00e9quipe", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("death_time", "10", "Permet de d\u00e9finir le temps de r\u00e9apparition", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_time", "5", "Permet de d\u00e9finir le temps restant necessaire en minute avant de faire apparaitre les poulets dans l'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_interval", "15", "Permet de d\u00e9finir le temps en seconde necessaire avant de pouvoir faire apparaitre une vague de poulet", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_min_count", "2", "Permet de d\u00e9finir le nombre minimum de poulet dans une vague d'apparition", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_max_count", "5", "Permet de d\u00e9finir le nombre minimum de poulet dans une vague d'apparition", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_min_item_count", "1", "Permet de d\u00e9finir le nombre minimum de d'item qu'un poulet va drop dans une vague d'apparition", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("chicken_spawn_max_item_count", "3", "Permet de d\u00e9finir le nombre maximum de d'item qu'un poulet va drop dans une vague d'apparition", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("protected_zone_area_radius", "55", "Permet de d\u00e9finir le rayon en bloc de la zone prot\u00e9g\u00e9, o\u00f9 les blocs ne peuvent pas \u00eatre cass\u00e9", "settings", false, true));
            parametresParDefaut.add(new GameCVAR("enable_monster_in_protected_zone", "0", "Permet d'activer ou non l'apparition de monstre dans la zone prot\u00e9g\u00e9e", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("arena_safezone_radius", "5", "Permet de modifier le rayon de safezone de la zone de t\u00e9l\u00e9portation de l'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("arena_warn_chest_time", "10", "Permet de d\u00e9finir le temps restant en seconde avant de mettre un message dans le chat annon\u00e7ant l'arriv\u00e9e du coffre d'ar\u00e8ne", "arena", true, true));
            parametresParDefaut.add(new GameCVAR("max_time_between_drop", "25", "Permet de d\u00e9finir le temps maximum entre chaque largage", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("min_time_between_drop", "20", "Permet de d\u00e9finir le temps minimum entre chaque largage", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("max_distance_from_arena", "300", "Permet de d\u00e9finir la distance maximale entre la g\u00e9n\u00e9ration de position du largage et le centre de l'ar\u00e8ne", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("min_distance_from_arena", "150", "Permet de d\u00e9finir la distance minimale entre la g\u00e9n\u00e9ration de position du largage et le centre de l'ar\u00e8ne", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("normal_falling_speed", "40", "Permet de d\u00e9finir la vitesse de chute lorsque le parachute est pr\u00e9sent (en nombre de ticks, 20 ticks environ \u00e9gale \u00e0 1 sec)", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("free_falling_speed", "2", "Permet de d\u00e9finir la vitesse de chute lorsque le parachute est cass\u00e9 (en nombre de ticks, 20 ticks environ \u00e9gale \u00e0 1 sec)", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("max_item_in_drop", "40", "Permet de d\u00e9finir le nombre d'item minimum pr\u00e9sent dans le coffre", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("min_item_in_drop", "30", "Permet de d\u00e9finir le nombre d'item minimum pr\u00e9sent dans le coffre", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("drop_opening_time", "10", "Permet de d\u00e9finir le nombre d\u00e9finir le temps d'ouverture du coffre du largage", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("drop_display_time", "5", "Permet de d\u00e9finir combien de temps en seconde le message doit s'afficher", "airdrop", true, true));
            parametresParDefaut.add(new GameCVAR("enable_shop", "1", "Permet d'activer ou non le shop", "shop", true, true));
            parametresParDefaut.add(new GameCVAR("enable_kits", "1", "Permet d'activer ou non les kits", "kits", true, true));
            parametresParDefaut.add(new GameCVAR("drop_chest_on_death", "1", "Permet d'activer ou non l'apparition de coffre \u00e0 la mort d'un joueur contenant tout son inventaire", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("drop_chest_on_death_time", "60", "Permet de d\u00e9finir le temps de vie d'un coffre, combien de temps il doit rester apr\u00e8s sa premi\u00e8re ouverture", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("enable_halloween_event", "0", "Permet d'activer ou non le mode halloween", "event", true, false));
            parametresParDefaut.add(new GameCVAR("points_per_kill", "0", "Permet de d\u00e9finir le nombre de points offert lors d'un kill de joueur", "cvar", true, true));
            parametresParDefaut.add(new GameCVAR("enable_chat_from_other_worlds", "1", "Permet d'activer ou non l'isolement du chat dans une partie. Par exemple, si l'option est activ\u00e9, tous les messages envoy\u00e9 par des joueurs seront affich\u00e9. Sinon, seuls les messages provenant de la partie seront affich\u00e9", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("enable_nether", "0", "Permet d'activer ou non l'acc\u00e8s au nether", "settings", true, true));
            parametresParDefaut.add(new GameCVAR("enable_hunger", "1", "Permet d'activer ou non la faim dans une partie", "game", true, true));
            GameLogger.addLog(new Log("game_cvar", "Successfully added default cvar", "GameSettings: getParametresParDefaut"));
        }
        return parametresParDefaut;
    }

    public static GameCVAR getValeurParDefaut(String commande) {
        for (GameCVAR cvar : GameSettings.getParametresParDefaut()) {
            if (!cvar.getCommand().equalsIgnoreCase(commande)) continue;
            return cvar;
        }
        return null;
    }
}

