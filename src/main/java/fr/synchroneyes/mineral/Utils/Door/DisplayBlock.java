package fr.synchroneyes.mineral.Utils.Door;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

public class DisplayBlock {
    private Block baseBlock;
    private Location position;
    private Material materiel;
    private MaterialData data;
    private BlockData blockData;

    public DisplayBlock(Block baseBlock) {
        try {
            this.baseBlock = baseBlock;
            this.position = baseBlock.getLocation();
            this.materiel = baseBlock.getState().getType();
            this.data = baseBlock.getState().getData();
            this.blockData = baseBlock.getBlockData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Block getBlock() {
        return this.baseBlock;
    }

    public Location getPosition() {
        return this.position;
    }

    public void display() {
        try {
            this.position.getBlock().setType(this.materiel);
            this.position.getBlock().setBlockData(this.baseBlock.getBlockData());
            this.position.getBlock().getState().setData(this.data);
            this.position.getBlock().setBlockData(this.blockData);
            this.position.getBlock().getState().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide() {
        this.position.getBlock().setType(Material.AIR);
    }
}

