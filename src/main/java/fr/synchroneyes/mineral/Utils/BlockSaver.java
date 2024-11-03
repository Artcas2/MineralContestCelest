package fr.synchroneyes.mineral.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

public class BlockSaver {
    private Type method;
    private int posX;
    private int posY;
    private int posZ;
    private Material material;
    private Byte blockByte;
    private World world;
    private BlockData blockData;

    public BlockSaver(Block b, Type t) {
        this.method = t;
        this.posX = (int)b.getLocation().getX();
        this.posY = (int)b.getLocation().getY();
        this.posZ = (int)b.getLocation().getZ();
        this.world = b.getLocation().getWorld();
        this.material = b.getType();
        this.blockByte = b.getState().getData().getData();
        this.blockData = b.getBlockData();
    }

    public void applyMethod() {
        Location blockLocation = new Location(this.world, (double)this.posX, (double)this.posY, (double)this.posZ);
        Block block = blockLocation.getBlock();
        if (this.method == Type.PLACED) {
            block.setType(Material.AIR);
        } else {
            block.setType(this.material);
            if (this.blockData != null) {
                block.setBlockData(this.blockData, false);
                if (block.getBlockData() instanceof Door && block.getRelative(BlockFace.DOWN, 1).getType().equals((Object)Material.AIR)) {
                    block.setBlockData(this.blockData, false);
                    Door door = (Door)block.getBlockData();
                    door.setHalf(Bisected.Half.BOTTOM);
                    door.setOpen(false);
                    door.setFacing(((Door)this.blockData).getFacing());
                    block.getRelative(BlockFace.DOWN, 1).setType(this.material);
                    block.getRelative(BlockFace.DOWN, 1).setBlockData((BlockData)door, false);
                    door.setHalf(Bisected.Half.TOP);
                    door.setOpen(false);
                    block.setBlockData((BlockData)door, false);
                }
            }
            block.getState().getData().setData(this.blockByte.byteValue());
        }
    }

    public static enum Type {
        PLACED,
        DESTROYED;

    }
}

