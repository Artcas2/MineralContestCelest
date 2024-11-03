package fr.synchroneyes.world_downloader.Inventories;

import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.world_downloader.Inventories.InventoryInterface;
import fr.synchroneyes.world_downloader.Items.ActionMapItem;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GestionMapsInventory extends InventoryInterface {
    public GestionMapsInventory(boolean displayInMainMenu) {
        super(displayInMainMenu);
    }

    @Override
    public void setInventoryItems(Player arbitre) {
        MapVote mapVote = new MapVote();
        ArrayList<String> maps = mapVote.getMaps();
        for (String nomMap : maps) {
            this.registerItem(new ActionMapItem(nomMap));
        }
        mapVote = null;
    }

    @Override
    public Material getItemMaterial() {
        return Material.RED_CONCRETE;
    }

    @Override
    public String getNomInventaire() {
        return Lang.map_downloader_delete_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.map_downloader_delete_title.toString();
    }
}

