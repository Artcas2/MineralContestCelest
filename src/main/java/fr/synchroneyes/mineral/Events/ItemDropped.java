package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDropped implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem;
        Player joueur = event.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur) && ShopManager.isAnShopItem(droppedItem = event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            return;
        }
    }
}

