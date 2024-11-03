package fr.synchroneyes.mapbuilder.Blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class SaveableBlock {
    private int posX;
    private int posY;
    private int posZ;
    private Material material;
    private Byte blockByte;
    private World world;
    private Location location;
    private BlockData blockData;

    public SaveableBlock(Block b) {
        this.posX = (int)b.getLocation().getX();
        this.posY = (int)b.getLocation().getY();
        this.posZ = (int)b.getLocation().getZ();
        this.world = b.getLocation().getWorld();
        this.location = b.getLocation();
        this.material = b.getType();
        this.blockByte = b.getState().getData().getData();
        this.blockData = b.getBlockData();
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getPosZ() {
        return this.posZ;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Byte getBlockByte() {
        return this.blockByte;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setBlock() {
        Location location = new Location(this.world, (double)this.posX, (double)this.posY, (double)this.posZ);
        Block block = location.getBlock();
        block.setType(this.material);
        block.setBlockData(this.blockData);
        block.getState().getData().setData(this.blockByte.byteValue());
        block.getState().update();
    }
}

