package fr.synchroneyes.mineral.Core.Referee.Inventory;

import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import java.util.ArrayList;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InventoryTemplate {
    protected LinkedList<RefereeItemTemplate> items = new LinkedList();
    protected Inventory inventaire = Bukkit.createInventory(null, (int)27, (String)this.getNomInventaire());

    public InventoryTemplate() {
        this.inventaire.setMaxStackSize(1);
    }

    public void registerItem(RefereeItemTemplate itemTemplate) {
        this.items.add(itemTemplate);
    }

    public abstract void setInventoryItems(Player var1);

    public void openInventory(Player arbitre) {
        this.items.clear();
        this.inventaire.clear();
        this.setInventoryItems(arbitre);
        for (RefereeItemTemplate item : this.items) {
            this.inventaire.addItem(new ItemStack[]{item.toItemStack()});
        }
        arbitre.closeInventory();
        arbitre.openInventory(this.inventaire);
    }

    public LinkedList<RefereeItemTemplate> getItems() {
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

    public LinkedList<RefereeItemTemplate> getObjets() {
        return this.items;
    }

    public void addSpaces(int number) {
        for (int i = 0; i < number; ++i) {
            this.inventaire.addItem(new ItemStack[]{new ItemStack(Material.AIR, 1)});
        }
    }
}

