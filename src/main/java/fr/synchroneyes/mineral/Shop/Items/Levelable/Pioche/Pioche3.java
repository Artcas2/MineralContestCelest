package fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche;

import fr.synchroneyes.mineral.Shop.Items.Abstract.LevelableItem;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche2;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Pioche3 extends LevelableItem {
    public static String coloredItemName = ChatColor.GOLD + Lang.shopitem_pickaxelvl3_title.toString();

    @Override
    public Class getRequiredLevel() {
        return Pioche2.class;
    }

    @Override
    public String getNomItem() {
        return Lang.shopitem_pickaxelvl3_title.toString();
    }

    @Override
    public String[] getDescriptionItem() {
        return new String[]{Lang.shopitem_pickaxelvl3_desc.toString()};
    }

    @Override
    public Material getItemMaterial() {
        return Material.DIAMOND_PICKAXE;
    }

    @Override
    public boolean isEnabledOnRespawn() {
        return true;
    }

    @Override
    public boolean isEnabledOnDeathByAnotherPlayer() {
        return false;
    }

    @Override
    public boolean isEnabledOnDeath() {
        return false;
    }

    @Override
    public int getNombreUtilisations() {
        return 1;
    }

    @Override
    public void onItemUse() {
        ItemStack pioche;
        ItemMeta meta;
        ItemStack oldLevelPioche = new ItemStack(Material.DIAMOND_PICKAXE);
        oldLevelPioche.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
        for (ItemStack item : this.joueur.getInventory().getContents()) {
            if (item == null || !item.equals((Object)oldLevelPioche)) continue;
            item.setAmount(0);
            break;
        }
        if ((meta = (pioche = new ItemStack(Material.DIAMOND_PICKAXE)).getItemMeta()) != null) {
            meta.setDisplayName(coloredItemName);
        }
        pioche.setItemMeta(meta);
        pioche.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
        pioche.addEnchantment(Enchantment.DIG_SPEED, 2);
        this.joueur.getInventory().addItem(new ItemStack[]{pioche});
    }

    @Override
    public int getPrice() {
        return ShopManager.getBonusPriceFromName("upgrade_pickaxe3");
    }
}

