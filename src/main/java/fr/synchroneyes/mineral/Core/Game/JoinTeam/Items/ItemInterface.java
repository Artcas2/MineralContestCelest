package fr.synchroneyes.mineral.Core.Game.JoinTeam.Items;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ItemInterface {
    public abstract Material getItemMaterial();

    public abstract String getNomInventaire();

    public abstract List<String> getDescriptionInventaire();

    public abstract void performClick(Player var1);

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.getItemMaterial(), 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.getNomInventaire());
        itemMeta.setLore(this.getDescriptionInventaire());
        item.setItemMeta(itemMeta);
        return item;
    }
}

