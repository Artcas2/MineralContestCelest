package fr.synchroneyes.mineral.Shop.Items.Permanent;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Shop.Items.Abstract.PermanentItem;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EpeeDiamant extends PermanentItem {
    public static String itemNameColored = ChatColor.AQUA + Lang.shopitem_diamond_sword_title.toString();

    @Override
    public String getNomItem() {
        return Lang.shopitem_diamond_sword_title.toString();
    }

    @Override
    public String[] getDescriptionItem() {
        return new String[]{Lang.shopitem_diamond_sword_desc.toString()};
    }

    @Override
    public Material getItemMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public void onItemUse() {
        Groupe playerGroup = mineralcontest.getPlayerGroupe(this.joueur);
        if (playerGroup == null) {
            return;
        }
        ArrayList<ItemStack> items_de_base = playerGroup.getPlayerBaseItem().getItems();
        for (ItemStack item : items_de_base) {
            if (item == null || !item.getType().toString().toLowerCase().contains("sword")) continue;
            this.joueur.getInventory().remove(item);
        }
        ItemStack epee_diamant = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = epee_diamant.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(itemNameColored);
        }
        epee_diamant.setItemMeta(meta);
        this.joueur.getInventory().addItem(new ItemStack[]{epee_diamant});
    }

    @Override
    public int getPrice() {
        return ShopManager.getBonusPriceFromName("permanent_diamond_sword");
    }
}

