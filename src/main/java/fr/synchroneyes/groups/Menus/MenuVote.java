package fr.synchroneyes.groups.Menus;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.groups.Menus.MapItem;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuVote extends InventoryTemplate {
    public MenuVote() {
        this.inventaire = Bukkit.createInventory(null, (int)54, (String)this.getNomInventaire());
    }

    @Override
    public void setInventoryItems(Player arbitre) {
        Groupe playerGroup = mineralcontest.getPlayerGroupe(arbitre);
        if (playerGroup == null) {
            return;
        }
        MapVote mapVote = playerGroup.getMapVote();
        if (mapVote == null) {
            mapVote = new MapVote();
        }
        ArrayList<String> liste_maps = mapVote.getMaps();
        for (String nomMap : liste_maps) {
            this.registerItem(new MapItem(nomMap, playerGroup, (InventoryTemplate)this));
        }
    }

    @Override
    public Material getItemMaterial() {
        return Material.PAPER;
    }

    @Override
    public String getNomInventaire() {
        return Lang.map_downloader_inventory_maps_list_name.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return "Vote pour un item !";
    }

    public Inventory getInventory() {
        return this.inventaire;
    }
}

