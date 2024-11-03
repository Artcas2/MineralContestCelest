package fr.synchroneyes.world_downloader.Inventories;

import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.world_downloader.Inventories.InventoryInterface;
import fr.synchroneyes.world_downloader.Items.AllMapDownloadItem;
import fr.synchroneyes.world_downloader.Items.MapDownloadItem;
import fr.synchroneyes.world_downloader.MapInfo;
import fr.synchroneyes.world_downloader.WorldDownloader;
import java.util.ArrayList;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MapListInventory extends InventoryInterface {
    public MapListInventory(boolean displayInMainMenu) {
        super(displayInMainMenu);
    }

    @Override
    public void setInventoryItems(Player arbitre) {
        LinkedList<MapInfo> maps_available = WorldDownloader.getMaps(false);
        MapVote mapVote = new MapVote();
        ArrayList<String> maps_telecharger = mapVote.getMaps();
        for (MapInfo map : maps_available) {
            if (maps_telecharger.contains(map.map_folder_name)) continue;
            this.registerItem(MapDownloadItem.fromMapInfo(map));
        }
        this.registerItem(new AllMapDownloadItem());
    }

    @Override
    public Material getItemMaterial() {
        return Material.MAP;
    }

    @Override
    public String getNomInventaire() {
        return Lang.map_downloader_inventory_name.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.map_downloader_inventory_name.toString();
    }
}

