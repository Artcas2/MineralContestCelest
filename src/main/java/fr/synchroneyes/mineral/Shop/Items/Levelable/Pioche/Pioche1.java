package fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche;

import fr.synchroneyes.mineral.Shop.Items.Abstract.LevelableItem;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Pioche1 extends LevelableItem {
    public static String coloredItemName = ChatColor.GOLD + Lang.shopitem_pickaxelvl1_title.toString();

    @Override
    public Class getRequiredLevel() {
        return null;
    }

    @Override
    public String getNomItem() {
        return Lang.shopitem_pickaxelvl1_title.toString();
    }

    @Override
    public String[] getDescriptionItem() {
        return new String[]{Lang.shopitem_pickaxelvl1_desc.toString()};
    }

    @Override
    public Material getItemMaterial() {
        return Material.WOODEN_PICKAXE;
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
        for (ItemStack item : this.joueur.getInventory().getContents()) {
            if (item == null || !item.getType().toString().toLowerCase().contains("pickaxe")) continue;
            item.setAmount(0);
            break;
        }
        if ((meta = (pioche = new ItemStack(Material.IRON_PICKAXE)).getItemMeta()) != null) {
            meta.setDisplayName(coloredItemName);
        }
        pioche.setItemMeta(meta);
        pioche.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
        this.joueur.getInventory().addItem(new ItemStack[]{pioche});
    }

    @Override
    public int getPrice() {
        return ShopManager.getBonusPriceFromName("upgrade_pickaxe1");
    }
}

