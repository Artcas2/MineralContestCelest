package fr.synchroneyes.mineral.Core.Game;

import fr.synchroneyes.mineral.mineralcontest;
import java.util.Stack;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockManager {
    private static BlockManager instance;
    private Stack<Block> placedBlocks;
    private Material[] blockedMaterials = new Material[]{Material.AIR, Material.OBSIDIAN, Material.WATER, Material.LAVA, Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.BEDROCK};

    private BlockManager() {
        instance = this;
        this.placedBlocks = new Stack();
    }

    public Stack<Block> getPlacedBlocks() {
        return this.placedBlocks;
    }

    public void addBlock(Block b) {
        if (this.wasBlockAdded(b)) {
            return;
        }
        if (b.getType().equals((Object)Material.CHEST)) {
            mineralcontest.getPlayerGame((Player)b.getWorld().getPlayers().get(0)).addAChest(b);
        }
        this.placedBlocks.add(b);
    }

    public boolean wasBlockAdded(Block b) {
        return this.placedBlocks.contains(b);
    }

    public static BlockManager getInstance() {
        if (instance == null) {
            return new BlockManager();
        }
        return instance;
    }

    public boolean isBlockAllowedToBeAdded(Block b) {
        for (Material blockedType : this.blockedMaterials) {
            if (!blockedType.toString().toLowerCase().contains(b.getType().toString().toLowerCase())) continue;
            return false;
        }
        return true;
    }

    public boolean isBlockAllowedToBeAdded(Material b) {
        for (Material blockedType : this.blockedMaterials) {
            if (!blockedType.toString().toLowerCase().contains(b.toString().toLowerCase())) continue;
            return false;
        }
        return true;
    }
}

