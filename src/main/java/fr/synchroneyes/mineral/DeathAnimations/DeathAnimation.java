package fr.synchroneyes.mineral.DeathAnimations;

import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class DeathAnimation {
    public abstract String getAnimationName();

    public abstract Material getIcone();

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.getIcone());
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.getAnimationName());
        itemMeta.setLore(new LinkedList());
        item.setItemMeta(itemMeta);
        return item;
    }

    public abstract void playAnimation(LivingEntity var1);
}

