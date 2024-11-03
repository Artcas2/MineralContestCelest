package fr.synchroneyes.special_events.halloween2024.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClonedInventory {
    private ItemStack[] items;
    private ItemStack[] armor;
    private ItemStack[] extra;
    private ItemStack[] storageContent;
    private PlayerInventory original;

    public ClonedInventory(PlayerInventory inventory) {
        this.items = inventory.getContents();
        this.armor = inventory.getArmorContents();
        this.extra = inventory.getExtraContents();
        this.storageContent = inventory.getStorageContents();
        this.original = inventory;
    }

    public PlayerInventory reset() {
        this.original.setContents(this.items);
        this.original.setArmorContents(this.armor);
        this.original.setExtraContents(this.extra);
        this.original.setStorageContents(this.storageContent);
        return this.original;
    }
}

