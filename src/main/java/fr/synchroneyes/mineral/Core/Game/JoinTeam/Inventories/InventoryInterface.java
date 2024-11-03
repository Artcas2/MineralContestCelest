package fr.synchroneyes.mineral.Core.Game.JoinTeam.Inventories;

import fr.synchroneyes.mineral.Core.Game.JoinTeam.Items.ItemInterface;
import java.util.ArrayList;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InventoryInterface {
    protected LinkedList<ItemInterface> items = new LinkedList();
    protected Inventory inventaire;
    protected boolean displayInMainMenu = true;

    public InventoryInterface(boolean displayInMainMenu) {
        this.inventaire = Bukkit.createInventory(null, (int)54, (String)this.getNomInventaire());
        this.inventaire.setMaxStackSize(1);
        this.displayInMainMenu = displayInMainMenu;
    }

    public InventoryInterface(boolean displayInMainMenu, int inventorySize) {
        this.inventaire = Bukkit.createInventory(null, (int)inventorySize, (String)this.getNomInventaire());
        this.inventaire.setMaxStackSize(1);
        this.displayInMainMenu = displayInMainMenu;
    }

    public void registerItem(ItemInterface itemTemplate) {
        this.items.add(itemTemplate);
    }

    public void clearItems() {
        this.items.clear();
    }

    public boolean isDisplayInMainMenu() {
        return this.displayInMainMenu;
    }

    public abstract void setInventoryItems();

    public void openInventory(Player admin) {
        this.inventaire.clear();
        this.clearItems();
        this.setInventoryItems();
        admin.closeInventory();
        admin.openInventory(this.inventaire);
    }

    public Inventory getInventory() {
        this.inventaire.clear();
        this.clearItems();
        this.setInventoryItems();
        return this.inventaire;
    }

    public LinkedList<ItemInterface> getItems() {
        this.setInventoryItems();
        return this.items;
    }

    public abstract Material getItemMaterial();

    public abstract String getNomInventaire();

    public abstract String getDescriptionInventaire();

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.getItemMaterial(), 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.getNomInventaire());
        ArrayList<String> description = new ArrayList<String>();
        description.add(this.getDescriptionInventaire());
        itemMeta.setLore(description);
        item.setItemMeta(itemMeta);
        return item;
    }

    public boolean isRepresentedItemStack(ItemStack item) {
        return item.equals((Object)this.toItemStack());
    }

    public boolean isEqualsToInventory(Inventory i) {
        return i.equals((Object)this.inventaire);
    }
}

