package fr.synchroneyes.mapbuilder.Spawner;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mapbuilder.Blocks.BlocksColorChanger;
import fr.synchroneyes.mapbuilder.Blocks.BlocksDataColor;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mapbuilder.Util;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class House {
    private static mineralcontest plugin = mineralcontest.plugin;
    private static Stack<BlockSaver> blocksModified = new Stack();

    public static void addModifiedBlock(Block b, BlockSaver.Type type) {
        BlockSaver blockToAdd = new BlockSaver(b, type);
        if (!blocksModified.contains(blockToAdd)) {
            blocksModified.add(blockToAdd);
        }
    }

    public static void spawn(BlocksDataColor color, Player player) {
        Byte blockByte;
        Material blockMaterial;
        Location locTMP;
        int newZ;
        int newY;
        int newX;
        int i;
        String houseName;
        String houseToLoad = houseName = House.getHouseDirectionBasedOnPlayer(player);
        switch (houseName) {
            case "east": {
                houseToLoad = FileList.CustomMap_east_house_scheme.toString();
                break;
            }
            case "north": {
                houseToLoad = FileList.CustomMap_north_house_scheme.toString();
                break;
            }
            case "south": {
                houseToLoad = FileList.CustomMap_south_house_scheme.toString();
                break;
            }
            case "west": {
                houseToLoad = FileList.CustomMap_west_house_scheme.toString();
            }
        }
        Bukkit.getLogger().info(houseToLoad);
        File houseFileToLoad = new File(plugin.getDataFolder(), houseToLoad);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)houseFileToLoad);
        ConfigurationSection blocks = yamlConfiguration.getConfigurationSection("house.blocks");
        if (blocks == null) {
            player.sendMessage("Impossible de r\u00e9cuperer les blocs du fichier");
            return;
        }
        Location loc = player.getLocation();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        int blockCount = Integer.parseInt(yamlConfiguration.get("house.blocks.count").toString());
        int doorCount = Integer.parseInt(yamlConfiguration.get("house.door.count").toString());
        for (i = 0; i < blockCount; ++i) {
            newX = (int)(x + Double.parseDouble(yamlConfiguration.get("house.blocks." + i + ".block.location.x").toString()));
            newY = (int)(y + Double.parseDouble(yamlConfiguration.get("house.blocks." + i + ".block.location.y").toString()));
            newZ = (int)(z + Double.parseDouble(yamlConfiguration.get("house.blocks." + i + ".block.location.z").toString()));
            locTMP = new Location(loc.getWorld(), (double)newX, (double)newY, (double)newZ);
            blockMaterial = Material.valueOf((String)yamlConfiguration.get("house.blocks." + i + ".block.type").toString());
            blockByte = Byte.parseByte(yamlConfiguration.get("house.blocks." + i + ".block.data").toString());
            House.addModifiedBlock(locTMP.getBlock(), BlockSaver.Type.DESTROYED);
            locTMP.getBlock().setType(blockMaterial);
            locTMP.getBlock().getLocation().setX(locTMP.getX());
            locTMP.getBlock().getLocation().setY(locTMP.getY());
            locTMP.getBlock().getLocation().setZ(locTMP.getZ());
            locTMP.getBlock().getState().getData().setData(blockByte.byteValue());
            BlocksColorChanger.changeBlockColor(locTMP.getBlock(), color);
        }
        for (i = 0; i < doorCount; ++i) {
            newX = (int)(x + Double.parseDouble(yamlConfiguration.get("house.door.blocks." + i + ".block.location.x").toString()));
            newY = (int)(y + Double.parseDouble(yamlConfiguration.get("house.door.blocks." + i + ".block.location.y").toString()));
            newZ = (int)(z + Double.parseDouble(yamlConfiguration.get("house.door.blocks." + i + ".block.location.z").toString()));
            locTMP = new Location(loc.getWorld(), (double)newX, (double)newY, (double)newZ);
            BlocksColorChanger.changeBlockColor(locTMP.getBlock(), color);
            blockMaterial = Material.valueOf((String)yamlConfiguration.get("house.door.blocks." + i + ".block.type").toString());
            blockByte = Byte.parseByte(yamlConfiguration.get("house.door.blocks." + i + ".block.data").toString());
            House.addModifiedBlock(locTMP.getBlock(), BlockSaver.Type.DESTROYED);
            locTMP.getBlock().setType(blockMaterial);
            locTMP.getBlock().getLocation().setX(locTMP.getX());
            locTMP.getBlock().getLocation().setY(locTMP.getY());
            locTMP.getBlock().getLocation().setZ(locTMP.getZ());
            locTMP.getBlock().getState().getData().setData(blockByte.byteValue());
            BlocksColorChanger.changeBlockColor(locTMP.getBlock(), color);
        }
        MapBuilder.modifications.push((Stack)blocksModified.clone());
        blocksModified.clear();
    }

    private static String getHouseDirectionBasedOnPlayer(Player p) {
        String playerLookingDirection;
        switch (playerLookingDirection = Util.getLookingDirection(p)) {
            case "NE": 
            case "NW": 
            case "N": {
                playerLookingDirection = "north";
                break;
            }
            case "SE": 
            case "SW": 
            case "S": {
                playerLookingDirection = "south";
                break;
            }
            case "E": {
                playerLookingDirection = "east";
                break;
            }
            case "W": {
                playerLookingDirection = "west";
            }
        }
        return playerLookingDirection;
    }
}

