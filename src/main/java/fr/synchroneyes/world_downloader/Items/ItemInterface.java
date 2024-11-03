package fr.synchroneyes.world_downloader.Items;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ItemInterface {
    public abstract Material getItemMaterial();

    public abstract String getNomInventaire();

    public abstract String getDescriptionInventaire();

    public abstract void performClick(Player var1);

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
}

