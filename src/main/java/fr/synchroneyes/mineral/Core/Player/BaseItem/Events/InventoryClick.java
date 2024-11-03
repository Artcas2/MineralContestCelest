package fr.synchroneyes.mineral.Core.Player.BaseItem.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Player.BaseItem.PlayerBaseItem;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class InventoryClick implements Listener {
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player joueur = (Player)event.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Inventory baseItemInventory;
            Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroupe == null) {
                return;
            }
            PlayerBaseItem playerBaseItem = playerGroupe.getPlayerBaseItem();
            Inventory closedInventory = event.getInventory();
            if (closedInventory.equals((Object)(baseItemInventory = playerBaseItem.getInventory())) && playerBaseItem.isBeingEdited()) {
                mineralcontest.plugin.getServer().getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> joueur.openInventory(baseItemInventory), 1L);
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player joueur = (Player)event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (joueur != null && mineralcontest.isInAMineralContestWorld(joueur)) {
            ItemMeta meta;
            Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroupe == null) {
                return;
            }
            PlayerBaseItem playerBaseItem = playerGroupe.getPlayerBaseItem();
            if (clickedItem == null) {
                return;
            }
            Inventory inventaire = event.getClickedInventory();
            if (inventaire == null) {
                return;
            }
            if (clickedItem != null && clickedItem.getItemMeta() != null && inventaire.equals((Object)playerBaseItem.getInventory()) && (meta = clickedItem.getItemMeta()).getDisplayName().equals(Lang.player_base_item_close_inventory_item_title.toString())) {
                playerBaseItem.closeInventory(joueur);
                event.setCancelled(true);
            }
        }
    }
}

