package fr.synchroneyes.mineral.Core.Referee.Inventory;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.LoadMapItem;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MapSelectorInventory extends InventoryTemplate {
    @Override
    public void setInventoryItems(Player arbitre) {
        Groupe groupe = mineralcontest.getPlayerGroupe(arbitre);
        if (groupe == null) {
            return;
        }
        MapVote mapVote = new MapVote();
        ArrayList<String> maps = mapVote.getMaps();
        for (String map : maps) {
            this.registerItem(new LoadMapItem(map, null, this));
        }
    }

    @Override
    public Material getItemMaterial() {
        return Material.COMPASS;
    }

    @Override
    public String getNomInventaire() {
        return Lang.referee_item_inventory_map_selector_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.referee_item_inventory_map_selector_description.toString();
    }
}

