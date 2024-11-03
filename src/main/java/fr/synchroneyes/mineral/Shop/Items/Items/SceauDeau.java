package fr.synchroneyes.mineral.Shop.Items.Items;

import fr.synchroneyes.mineral.Shop.Items.Abstract.ConsumableItem;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SceauDeau extends ConsumableItem {
    @Override
    public String getNomItem() {
        return Lang.shopitem_waterbucket_title.toString();
    }

    @Override
    public String[] getDescriptionItem() {
        return new String[]{Lang.shopitem_waterbucket_desc.toString()};
    }

    @Override
    public Material getItemMaterial() {
        return Material.WATER_BUCKET;
    }

    @Override
    public boolean isEnabledOnRespawn() {
        return false;
    }

    @Override
    public boolean isEnabledOnPurchase() {
        return true;
    }

    @Override
    public int getNombreUtilisations() {
        return 1;
    }

    @Override
    public void onItemUse() {
        ItemStack item = new ItemStack(this.getItemMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Lang.translate(this.getNomItem()));
        item.setItemMeta(meta);
        this.joueur.getInventory().addItem(new ItemStack[]{item});
    }

    @Override
    public int getPrice() {
        return ShopManager.getBonusPriceFromName("water_bucket");
    }
}

