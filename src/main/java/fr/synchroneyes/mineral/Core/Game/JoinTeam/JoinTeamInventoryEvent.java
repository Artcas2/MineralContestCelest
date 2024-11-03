package fr.synchroneyes.mineral.Core.Game.JoinTeam;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.Items.ItemInterface;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class JoinTeamInventoryEvent implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player joueur = (Player)event.getWhoClicked();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (playerGroup.getGame() == null || playerGroup.getMonde() == null || playerGroup.getEtatPartie() != Etats.ATTENTE_DEBUT_PARTIE) {
                return;
            }
            ItemStack clickedItem = event.getCurrentItem();
            Inventory clickedInventory = event.getInventory();
            if (playerGroup.getGame().getTeamSelectionMenu().getInventory() == null) {
                return;
            }
            if (clickedInventory.equals((Object)playerGroup.getGame().getTeamSelectionMenu().getInventory())) {
                for (ItemInterface item : playerGroup.getGame().getTeamSelectionMenu().getItems()) {
                    if (clickedItem == null || clickedItem.getItemMeta() == null || !item.toItemStack().equals((Object)clickedItem)) continue;
                    item.performClick(joueur);
                    event.setCancelled(true);
                    joueur.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        Player joueur = event.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (playerGroup.getGame() == null) {
                return;
            }
            ItemStack item = event.getItem();
            if (item != null && playerGroup.getEtatPartie() == Etats.ATTENTE_DEBUT_PARTIE && item.equals((Object)Game.getTeamSelectionItem())) {
                playerGroup.getGame().openTeamSelectionMenuToPlayer(joueur);
                event.setCancelled(true);
                return;
            }
        }
    }
}

