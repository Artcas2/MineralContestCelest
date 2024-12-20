package fr.synchroneyes.mineral.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potion {
    public static ItemStack createPotion(PotionEffectType effectType, int niveau, int duree_minute, String nom) {
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta)potion.getItemMeta();
        meta.setDisplayName(nom);
        meta.addCustomEffect(new PotionEffect(effectType, 1200 * duree_minute, niveau - 1), true);
        potion.setItemMeta((ItemMeta)meta);
        return potion;
    }
}

