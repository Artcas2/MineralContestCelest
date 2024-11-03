package fr.synchroneyes.mineral.Core.Referee;

import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import fr.synchroneyes.mineral.Core.Referee.Referee;
import fr.synchroneyes.mineral.Core.Referee.RefereeInventory;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RefereeEvent implements Listener {
    @EventHandler
    public void OnPlayerRightClick(PlayerInteractEvent event) {
        Player joueur = event.getPlayer();
        if (mineralcontest.getPlayerGame(joueur) != null && mineralcontest.getPlayerGame(joueur).isReferee(joueur) && (event.getAction().equals((Object)Action.RIGHT_CLICK_AIR) || event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK))) {
            ItemStack itemEnMain = joueur.getInventory().getItemInMainHand();
            ItemMeta itemEnMainMeta = itemEnMain.getItemMeta();
            if (!itemEnMain.getType().equals((Object)Material.AIR) && itemEnMainMeta.getDisplayName().equals(Referee.getRefereeItem().getItemMeta().getDisplayName())) {
                joueur.openInventory(RefereeInventory.getInventory());
            }
        }
    }

    @EventHandler
    public void OnInventoryItemClicked(InventoryClickEvent event) {
        Player joueur = (Player)event.getWhoClicked();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        if (mineralcontest.getPlayerGame(joueur) != null && mineralcontest.getPlayerGame(joueur).isReferee(joueur)) {
            Inventory inventaireOuvert = event.getClickedInventory();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) {
                return;
            }
            if (inventaireOuvert.equals((Object)RefereeInventory.getInventory())) {
                for (InventoryTemplate inventaire : RefereeInventory.inventaires) {
                    if (!inventaire.isRepresentedItemStack(clickedItem)) continue;
                    inventaire.openInventory(joueur);
                    event.setCancelled(true);
                    return;
                }
                for (RefereeItemTemplate item : RefereeInventory.items) {
                    if (!item.toItemStack().equals((Object)clickedItem)) continue;
                    item.performClick(joueur);
                    event.setCancelled(true);
                    return;
                }
            }
            for (InventoryTemplate inventaireCustom : RefereeInventory.inventaires) {
                if (!inventaireCustom.isEqualsToInventory(inventaireOuvert)) continue;
                for (RefereeItemTemplate item : inventaireCustom.getObjets()) {
                    if (!item.toItemStack().equals((Object)clickedItem)) continue;
                    item.performClick(joueur);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private void sendActionUnavailable(Player p) {
        p.sendMessage(mineralcontest.prefixErreur + Lang.referee_action_not_available.toString());
    }
}

