package fr.synchroneyes.groups;

import fr.synchroneyes.groups.Commands.Admin.AjouterAdmin;
import fr.synchroneyes.groups.Commands.Admin.RetirerAdmin;
import fr.synchroneyes.groups.Commands.Groupe.CreerGroupe;
import fr.synchroneyes.groups.Commands.Groupe.FermerGroupe;
import fr.synchroneyes.groups.Commands.Groupe.InviterGroupe;
import fr.synchroneyes.groups.Commands.Groupe.JoinGroupe;
import fr.synchroneyes.groups.Commands.Groupe.KickPlayerFromGroup;
import fr.synchroneyes.groups.Commands.Groupe.OuvrirGroupe;
import fr.synchroneyes.groups.Commands.Groupe.QuitterGroupe;
import fr.synchroneyes.groups.Commands.Vote.StartVote;
import fr.synchroneyes.groups.Commands.Vote.Vote;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

public class GroupeExtension {
    private static GroupeExtension instance;
    private CommandMap bukkitCommandMap;
    public static boolean enabled;

    private GroupeExtension() {
        if (!enabled) {
            return;
        }
        instance = this;
        Bukkit.getLogger().info("Loading GroupeExtension ...");
        try {
            this.getPluginCommandMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.registerCommands();
        this.supprimerMapsExistantes();
        Bukkit.getLogger().info("GroupeExtension loaded");
    }

    private void supprimerMapsExistantes() {
        File dossierServer = new File(System.getProperty("user.dir"));
        File[] fichiers = dossierServer.listFiles((dir, name) -> name.toLowerCase().startsWith("mc_"));
        for (File fichier : fichiers) {
            try {
                if (!fichier.isDirectory()) continue;
                Bukkit.getServer().unloadWorld(fichier.getName(), false);
                FileUtils.deleteDirectory(fichier);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (mineralcontest.plugin.pluginWorld == null) {
            mineralcontest.plugin.pluginWorld = PlayerUtils.getPluginWorld();
        }
        for (World world : Bukkit.getWorlds()) {
            if (!world.getName().contains("mc_")) continue;
            world.setAutoSave(false);
            for (Player p : world.getPlayers()) {
                if (mineralcontest.plugin.defaultSpawn == null) {
                    p.setHealth(0.0);
                    continue;
                }
                p.sendMessage("Teleporting you to plugin hub");
                p.teleport(mineralcontest.plugin.defaultSpawn);
                PlayerUtils.clearPlayer(p, true);
            }
            Bukkit.unloadWorld((World)world, (boolean)false);
            Bukkit.getLogger().info("Successfully unloaded world " + world.getName());
            GameLogger.addLog(new Log("world_unload", "Successfully unloaded world " + world.getName(), "GroupeExtension: supprimerMapsExistantes"));
        }
    }

    public static GroupeExtension getInstance() {
        if (instance == null) {
            return new GroupeExtension();
        }
        return instance;
    }

    private void registerCommands() {
        if (!enabled) {
            return;
        }
        this.bukkitCommandMap.register("", (Command)new CreerGroupe());
        this.bukkitCommandMap.register("", (Command)new StartVote());
        this.bukkitCommandMap.register("", (Command)new InviterGroupe());
        this.bukkitCommandMap.register("", (Command)new JoinGroupe());
        this.bukkitCommandMap.register("", (Command)new KickPlayerFromGroup());
        this.bukkitCommandMap.register("", (Command)new QuitterGroupe());
        this.bukkitCommandMap.register("", (Command)new AjouterAdmin());
        this.bukkitCommandMap.register("", (Command)new RetirerAdmin());
        this.bukkitCommandMap.register("", (Command)new Vote());
        this.bukkitCommandMap.register("", (Command)new FermerGroupe());
        this.bukkitCommandMap.register("", (Command)new OuvrirGroupe());
    }

    private void getPluginCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        cmdMapField.setAccessible(true);
        this.bukkitCommandMap = (CommandMap)cmdMapField.get(Bukkit.getPluginManager());
    }

    static {
        enabled = true;
    }
}

