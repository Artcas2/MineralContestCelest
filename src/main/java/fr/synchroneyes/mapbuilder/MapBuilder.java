package fr.synchroneyes.mapbuilder;

import fr.synchroneyes.mapbuilder.Commands.mcarena;
import fr.synchroneyes.mapbuilder.Commands.mcbuild;
import fr.synchroneyes.mapbuilder.Commands.mcrevert;
import fr.synchroneyes.mapbuilder.Commands.mcteam;
import fr.synchroneyes.mapbuilder.Core.Monde;
import fr.synchroneyes.mapbuilder.Events.BlockPlaced;
import fr.synchroneyes.mapbuilder.Events.PlayerInteract;
import fr.synchroneyes.mineral.Scoreboard.ScoreboardUtil;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.mineralcontest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

public class MapBuilder {
    private mineralcontest plugin = mineralcontest.plugin;
    private static MapBuilder instance;
    public boolean isBuilderModeEnabled = false;
    private CommandMap bukkitCommandMap;
    public static Stack<Stack<BlockSaver>> modifications;
    public static Monde monde;

    private MapBuilder() {
        instance = this;
        modifications = new Stack();
        if (this.isBuilderModeEnabled) {
            MapBuilder.enableMapBuilder();
        }
        try {
            this.getPluginCommandMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.printToConsole("Loading custom maps module ...");
        this.registerEvents();
        this.registerCommands();
    }

    public static void enableMapBuilder() {
        MapBuilder.instance.isBuilderModeEnabled = true;
        instance.enableMapBuilding();
        monde = new Monde();
        Bukkit.broadcastMessage((String)"MapBuilder mode enabled!");
    }

    public static void disableMapBuilder() {
        MapBuilder.instance.isBuilderModeEnabled = false;
        instance.disableMapBuilding();
    }

    private void getPluginCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        cmdMapField.setAccessible(true);
        this.bukkitCommandMap = (CommandMap)cmdMapField.get(Bukkit.getPluginManager());
    }

    public static MapBuilder getInstance() {
        if (instance == null) {
            return new MapBuilder();
        }
        return instance;
    }

    private void registerEvents() {
        this.printToConsole("Registering events");
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new BlockPlaced(), (Plugin)this.plugin);
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new PlayerInteract(), (Plugin)this.plugin);
    }

    private void registerCommands() {
        this.printToConsole("Registering commands");
        this.bukkitCommandMap.register("", (Command)new mcteam());
        this.bukkitCommandMap.register("", (Command)new mcarena());
        this.bukkitCommandMap.register("", (Command)new mcbuild());
        this.bukkitCommandMap.register("", (Command)new mcrevert());
    }

    private void enableMapBuilding() {
        this.isBuilderModeEnabled = true;
        World game_world = mineralcontest.plugin.pluginWorld;
        int size = 1000000;
        if (game_world != null) {
            game_world.getWorldBorder().setCenter(mineralcontest.plugin.defaultSpawn);
            game_world.getWorldBorder().setSize((double)size);
            game_world.setDifficulty(Difficulty.PEACEFUL);
            for (Player p : game_world.getPlayers()) {
                p.setGameMode(GameMode.CREATIVE);
            }
        }
    }

    private void disableMapBuilding() {
        this.isBuilderModeEnabled = false;
        World game_world = mineralcontest.plugin.pluginWorld;
        int size = 1000000;
        if (game_world != null) {
            game_world.getWorldBorder().setCenter(mineralcontest.plugin.defaultSpawn);
            game_world.getWorldBorder().setSize((double)size);
            game_world.setDifficulty(Difficulty.NORMAL);
            for (Player p : game_world.getPlayers()) {
                p.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    private void printToConsole(String text) {
        String prefix = "[MineralContestCelest] [CUSTOM-MAPS] ";
        Bukkit.getLogger().info(prefix + text);
    }

    public void sendPlayersHUD() {
        if (!this.isBuilderModeEnabled) {
            return;
        }
        ArrayList<String> playerHudContents = new ArrayList<String>();
        playerHudContents.add("MapBuiler mode enabled");
        playerHudContents.add(" ");
        playerHudContents.add("Type /build to get the build item");
        String[] hudContentAsArray = new String[playerHudContents.size()];
        int index = 0;
        Iterator iterator = playerHudContents.iterator();
        while (iterator.hasNext()) {
            String hudcontent;
            hudContentAsArray[index] = hudcontent = (String)iterator.next();
            ++index;
        }
        for (Player player : this.plugin.pluginWorld.getPlayers()) {
            ScoreboardUtil.unrankedSidebarDisplay(player, hudContentAsArray);
        }
    }
}

