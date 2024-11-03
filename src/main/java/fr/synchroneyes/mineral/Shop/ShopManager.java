package fr.synchroneyes.mineral.Shop;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche1;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche2;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche3;
import fr.synchroneyes.mineral.Shop.Items.Permanent.EpeeDiamant;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ShopManager {
    private Game partie;
    private List<BonusSeller> liste_pnj;

    public ShopManager(Game partie) {
        this.partie = partie;
        this.liste_pnj = new LinkedList<BonusSeller>();
    }

    public void ajouterVendeur(BonusSeller seller) {
        if (this.liste_pnj.contains(seller)) {
            return;
        }
        this.liste_pnj.add(seller);
    }

    public static BonusSeller creerVendeur(Location position) {
        return new BonusSeller(position);
    }

    public static int getBonusPriceFromName(String bonusName) {
        File fichierPrix = new File(mineralcontest.plugin.getDataFolder(), FileList.ShopItem_PriceList.toString());
        if (!fichierPrix.exists()) {
            return 1000;
        }
        return YamlConfiguration.loadConfiguration((File)fichierPrix).getInt(bonusName);
    }

    public static boolean isAnShopItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getType() == Material.DIAMOND_SWORD) {
            return item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(EpeeDiamant.itemNameColored);
        }
        if (item.getType() == Material.IRON_PICKAXE && item.getItemMeta() != null) {
            String itemName = item.getItemMeta().getDisplayName();
            return itemName.equals(Pioche1.coloredItemName);
        }
        if (item.getType() == Material.DIAMOND_PICKAXE && item.getItemMeta() != null) {
            String itemName = item.getItemMeta().getDisplayName();
            if (itemName.equals(Pioche2.coloredItemName)) {
                return true;
            }
            return itemName.equals(Pioche3.coloredItemName);
        }
        return false;
    }

    public void disableShop() {
        for (BonusSeller vendeur : this.liste_pnj) {
            vendeur.getEntity().remove();
        }
    }

    public void enableShop() {
        for (BonusSeller vendeur : this.liste_pnj) {
            vendeur.spawn();
        }
    }

    public Game getPartie() {
        return this.partie;
    }

    public List<BonusSeller> getListe_pnj() {
        return this.liste_pnj;
    }
}

