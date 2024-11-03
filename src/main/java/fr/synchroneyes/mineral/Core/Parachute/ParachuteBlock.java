package fr.synchroneyes.mineral.Core.Parachute;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ParachuteBlock {
    private Location location;
    private Material material;

    public ParachuteBlock(Location location, Material material) {
        this.location = location;
        this.material = material;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public static ParachuteBlock fromBlock(Block b) {
        return new ParachuteBlock(b.getLocation(), b.getType());
    }

    public void remove() {
        this.location.getBlock().setType(Material.AIR);
    }
}

