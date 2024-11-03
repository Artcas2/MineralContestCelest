package fr.synchroneyes.mineral.Core.Arena.ArenaChestContent;

import org.bukkit.Material;

public class ArenaChestItem {
    private Material itemMaterial;
    private int itemProbability;

    public Material getItemMaterial() {
        return this.itemMaterial;
    }

    public void setItemMaterial(String itemMaterial) {
        this.itemMaterial = Material.valueOf((String)itemMaterial);
    }

    public int getItemProbability() {
        return this.itemProbability;
    }

    public void setItemProbability(int itemProbability) {
        this.itemProbability = itemProbability;
    }
}

