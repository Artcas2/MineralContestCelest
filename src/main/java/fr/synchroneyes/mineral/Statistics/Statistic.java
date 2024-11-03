package fr.synchroneyes.mineral.Statistics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Statistic {
    public abstract void perform(Player var1, Object var2);

    public abstract Player getHighestPlayer();

    public abstract Player getLowestPlayer();

    public abstract String getHighestPlayerTitle();

    public abstract String getLowerPlayerTitle();

    public abstract String getHighestItemSubTitle();

    public abstract String getLowestItemSubTitle();

    public abstract Material getHighestPlayerIcon();

    public abstract Material getLowestPlayerIcon();

    public abstract int getHighestPlayerValue();

    public abstract int getLowerPlayerValue();

    public abstract boolean isLowestValueRequired();

    public abstract boolean isStatUsable();

    private ItemStack getLowestItemStack() {
        ItemStack item = new ItemStack(this.getLowestPlayerIcon());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.getLowerPlayerTitle());
            LinkedList<String> description = new LinkedList<String>();
            description.add("> " + this.getLowestPlayer().getDisplayName());
            if (this.getLowestItemSubTitle() != null) {
                description.add(this.getLowestItemSubTitle());
            }
            itemMeta.setLore(description);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private ItemStack getHighestItemStack() {
        ItemStack item = new ItemStack(this.getHighestPlayerIcon());
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.getHighestPlayerTitle());
            LinkedList<String> description = new LinkedList<String>();
            if (this.getHighestPlayer() != null) {
                description.add("> " + this.getHighestPlayer().getDisplayName());
            }
            if (this.getHighestPlayer() != null && this.getHighestItemSubTitle() != null) {
                description.add(this.getHighestItemSubTitle());
            }
            itemMeta.setLore(description);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public List<ItemStack> toItemStack() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (!this.isStatUsable()) {
            return items;
        }
        if (this.isLowestValueRequired() && !this.getLowestPlayer().equals((Object)this.getHighestPlayer())) {
            items.add(this.getLowestItemStack());
        }
        items.add(this.getHighestItemStack());
        return items;
    }
}

