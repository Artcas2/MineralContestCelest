package fr.synchroneyes.mapbuilder.Spawner;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Arene {
    private static mineralcontest plugin = mineralcontest.plugin;
    private static Stack<BlockSaver> blocksModified = new Stack();

    public static void addModifiedBlock(Block b, BlockSaver.Type type) {
        BlockSaver blockToAdd = new BlockSaver(b, type);
        if (!blocksModified.contains(blockToAdd)) {
            blocksModified.add(blockToAdd);
        }
    }

    public static void spawn(Player player) {
        BlockData blockData;
        Byte blockByte;
        Material blockMaterial;
        Location locTMP;
        int newZ;
        int newY;
        int newX;
        int index;
        String path = FileList.CustomMap_arena_scheme.toString();
        File houseFileToLoad = new File(plugin.getDataFolder(), path);
        if (!houseFileToLoad.exists()) {
            player.sendMessage(path + " n'existe pas.");
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)houseFileToLoad);
        ConfigurationSection blocks = yamlConfiguration.getConfigurationSection("arena.normal_blocks");
        if (blocks == null) {
            player.sendMessage("Impossible de r\u00e9cuperer les blocs du fichier");
            return;
        }
        Location loc = player.getLocation();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        int normalBlockCount = yamlConfiguration.getConfigurationSection("arena.normal_blocks").getKeys(false).size();
        int specialBlockCount = yamlConfiguration.getConfigurationSection("arena.special_block").getKeys(false).size();
        int max = normalBlockCount + specialBlockCount;
        for (index = 0; index < normalBlockCount; ++index) {
            newX = (int)(x + Double.parseDouble(yamlConfiguration.get("arena.normal_blocks." + index + ".location.x").toString()));
            newY = (int)(y + Double.parseDouble(yamlConfiguration.get("arena.normal_blocks." + index + ".location.y").toString()));
            newZ = (int)(z + Double.parseDouble(yamlConfiguration.get("arena.normal_blocks." + index + ".location.z").toString()));
            locTMP = new Location(loc.getWorld(), (double)newX, (double)newY, (double)newZ);
            blockMaterial = Material.valueOf((String)yamlConfiguration.get("arena.normal_blocks." + index + ".material").toString());
            blockByte = Byte.parseByte(yamlConfiguration.get("arena.normal_blocks." + index + ".blockByte").toString());
            blockData = mineralcontest.plugin.getServer().createBlockData(yamlConfiguration.get("arena.normal_blocks." + index + ".blockdata").toString());
            Arene.addModifiedBlock(locTMP.getBlock(), BlockSaver.Type.DESTROYED);
            locTMP.getBlock().setType(blockMaterial);
            locTMP.getBlock().setBlockData(blockData, false);
            locTMP.getBlock().getLocation().setX(locTMP.getX());
            locTMP.getBlock().getLocation().setY(locTMP.getY());
            locTMP.getBlock().getLocation().setZ(locTMP.getZ());
            locTMP.getBlock().getState().getData().setData(blockByte.byteValue());
        }
        Bukkit.getLogger().info(index + " < " + normalBlockCount);
        Bukkit.getLogger().info(index + " < " + specialBlockCount);
        while (index < max) {
            newX = (int)(x + Double.parseDouble(yamlConfiguration.get("arena.special_block." + index + ".location.x").toString()));
            newY = (int)(y + Double.parseDouble(yamlConfiguration.get("arena.special_block." + index + ".location.y").toString()));
            newZ = (int)(z + Double.parseDouble(yamlConfiguration.get("arena.special_block." + index + ".location.z").toString()));
            locTMP = new Location(loc.getWorld(), (double)newX, (double)newY, (double)newZ);
            Arene.addModifiedBlock(locTMP.getBlock(), BlockSaver.Type.DESTROYED);
            blockMaterial = Material.valueOf((String)yamlConfiguration.get("arena.special_block." + index + ".material").toString());
            blockByte = Byte.parseByte(yamlConfiguration.get("arena.special_block." + index + ".blockByte").toString());
            blockData = mineralcontest.plugin.getServer().createBlockData(yamlConfiguration.get("arena.special_block." + index + ".blockdata").toString());
            if (blockData instanceof Door || blockMaterial.toString().equalsIgnoreCase("DOOR")) {
                locTMP.getBlock().setBlockData(blockData, false);
                Door door = (Door)blockData;
                door.setHalf(Bisected.Half.BOTTOM);
                door.setOpen(false);
                door.setFacing(((Door)blockData).getFacing());
                if (!locTMP.getBlock().getRelative(BlockFace.DOWN, 1).getType().equals((Object)Material.AIR)) {
                    locTMP.getBlock().getRelative(BlockFace.DOWN, 1).setType(blockMaterial);
                    locTMP.getBlock().getRelative(BlockFace.DOWN, 1).setBlockData((BlockData)door, false);
                }
                door.setHalf(Bisected.Half.TOP);
                door.setOpen(false);
                locTMP.getBlock().setBlockData((BlockData)door, false);
            } else {
                locTMP.getBlock().setType(blockMaterial);
                locTMP.getBlock().setBlockData(blockData, false);
            }
            locTMP.getBlock().getLocation().setX(locTMP.getX());
            locTMP.getBlock().getLocation().setY(locTMP.getY());
            locTMP.getBlock().getLocation().setZ(locTMP.getZ());
            locTMP.getBlock().getState().getData().setData(blockByte.byteValue());
            ++index;
        }
        MapBuilder.modifications.push((Stack)blocksModified.clone());
        blocksModified.clear();
    }
}

