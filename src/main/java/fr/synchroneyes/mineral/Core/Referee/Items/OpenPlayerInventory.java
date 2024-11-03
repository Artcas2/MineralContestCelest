package fr.synchroneyes.mineral.Core.Referee.Items;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ChatColorString;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class OpenPlayerInventory extends RefereeItemTemplate {
    public OpenPlayerInventory(Object target, InventoryTemplate inventaireSource) {
        super(target, inventaireSource);
    }

    @Override
    public void performClick(Player joueur) {
        if (this.target instanceof Player) {
            Inventory copiedInventory = Bukkit.createInventory(null, (int)54, (String)((Object)((Object)Lang.referee_item_inventory_of_player_title) + ((Player)this.target).getDisplayName()));
            PlayerInventory targetInventory = ((Player)this.target).getInventory();
            for (ItemStack item : targetInventory.getContents()) {
                if (item == null || item.getType().equals((Object)Material.AIR)) continue;
                copiedInventory.addItem(new ItemStack[]{new ItemStack(item.getType(), item.getAmount())});
            }
            for (int i = 0; i < 10; ++i) {
                copiedInventory.addItem(new ItemStack[]{new ItemStack(Material.AIR, 1)});
            }
            for (ItemStack armure : targetInventory.getStorageContents()) {
                if (armure == null || armure.getType().equals((Object)Material.AIR)) continue;
                copiedInventory.addItem(new ItemStack[]{new ItemStack(armure.getType(), armure.getAmount())});
            }
            joueur.closeInventory();
            joueur.openInventory(copiedInventory);
        }
    }

    @Override
    public String getNomItem() {
        return Lang.referee_item_inventory_of_player_title.toString() + ((Player)this.target).getDisplayName();
    }

    @Override
    public String getDescriptionItem() {
        return Lang.referee_item_inventory_of_player_description.toString() + ((Player)this.target).getDisplayName();
    }

    @Override
    public Material getItemMaterial() {
        Player playerToTeleportName = (Player)this.target;
        Game playerGame = mineralcontest.getPlayerGame(playerToTeleportName);
        if (playerGame == null || playerGame.getPlayerHouse(playerToTeleportName) == null) {
            return Material.WHITE_WOOL;
        }
        try {
            return Material.valueOf((String)(ChatColorString.toStringEN(playerGame.getPlayerTeam(playerToTeleportName).getCouleur()) + "_CONCRETE"));
        } catch (IllegalArgumentException iae) {
            return Material.WHITE_WOOL;
        }
    }
}

