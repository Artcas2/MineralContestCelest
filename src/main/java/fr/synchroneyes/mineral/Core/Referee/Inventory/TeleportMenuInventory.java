package fr.synchroneyes.mineral.Core.Referee.Inventory;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.TeleportToHouseItem;
import fr.synchroneyes.mineral.Core.Referee.Items.TeleportToPlayerItem;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ChatColorString;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeleportMenuInventory extends InventoryTemplate {
    @Override
    public void setInventoryItems(Player arbitre) {
        LinkedList<TeleportToHouseItem> maisons = new LinkedList<TeleportToHouseItem>();
        Groupe playerGroupe = mineralcontest.getPlayerGroupe(arbitre);
        for (House house : playerGroupe.getGame().getHouses()) {
            for (Player joueur : house.getTeam().getJoueurs()) {
                String nomItem = ChatColorString.toStringEN(house.getTeam().getCouleur()) + "_CONCRETE";
                Material materialItem = null;
                try {
                    materialItem = Material.valueOf((String)nomItem);
                } catch (IllegalArgumentException iae) {
                    materialItem = Material.WHITE_WOOL;
                }
                ItemStack itemJoueur = new ItemStack(materialItem, 1);
                ItemMeta itemMeta = itemJoueur.getItemMeta();
                ArrayList<String> description = new ArrayList<String>();
                description.add(Lang.referee_item_teleport_to_player_description.toString() + house.getTeam().getCouleur() + joueur.getDisplayName());
                itemMeta.setLore(description);
                itemMeta.setDisplayName(Lang.referee_item_teleport_to_player_title.toString() + joueur.getDisplayName());
                itemJoueur.setItemMeta(itemMeta);
                this.registerItem(new TeleportToPlayerItem(joueur, this));
            }
            if (house.getTeam().getJoueurs().isEmpty()) continue;
            maisons.add(new TeleportToHouseItem(house.getTeam(), this));
        }
        this.addSpaces(2);
        for (TeleportToHouseItem teleportToHouseItem : maisons) {
            this.registerItem(teleportToHouseItem);
        }
    }

    @Override
    public Material getItemMaterial() {
        return Material.ENDER_EYE;
    }

    @Override
    public String getNomInventaire() {
        return Lang.referee_item_teleport_inventory_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.referee_inventory_teleport_description.toString();
    }
}

