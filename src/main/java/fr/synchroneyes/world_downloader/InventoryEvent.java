package fr.synchroneyes.world_downloader;

import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.world_downloader.Inventories.InventoryInterface;
import fr.synchroneyes.world_downloader.Items.ItemInterface;
import fr.synchroneyes.world_downloader.WorldDownloader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryEvent implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player joueur = (Player)event.getWhoClicked();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        if (mineralcontest.getPlayerGame(joueur) != null) {
            Inventory inventaireOuvert = event.getClickedInventory();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) {
                return;
            }
            WorldDownloader worldDownloader = WorldDownloader.getInstance();
            if (inventaireOuvert.equals((Object)worldDownloader.getInventory())) {
                for (InventoryInterface inventaire : worldDownloader.inventaires) {
                    if (!inventaire.isRepresentedItemStack(clickedItem)) continue;
                    inventaire.openInventory(joueur);
                    event.setCancelled(true);
                    return;
                }
                for (ItemInterface item : worldDownloader.items) {
                    if (!item.toItemStack().equals((Object)clickedItem)) continue;
                    item.performClick(joueur);
                    event.setCancelled(true);
                    return;
                }
            }
            for (InventoryInterface inventaireCustom : worldDownloader.inventaires) {
                if (!inventaireCustom.isEqualsToInventory(inventaireOuvert)) continue;
                for (ItemInterface item : inventaireCustom.getObjets()) {
                    if (!item.toItemStack().equals((Object)clickedItem)) continue;
                    item.performClick(joueur);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}

