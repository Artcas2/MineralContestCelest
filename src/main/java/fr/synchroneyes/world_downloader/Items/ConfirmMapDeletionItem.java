package fr.synchroneyes.world_downloader.Items;

import fr.synchroneyes.groups.Utils.FileManager.DirectoryManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.world_downloader.Items.ItemInterface;
import fr.synchroneyes.world_downloader.WorldDownloader;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmMapDeletionItem extends ItemInterface {
    private String map;

    public ConfirmMapDeletionItem(String mapItem) {
        this.map = mapItem;
    }

    @Override
    public Material getItemMaterial() {
        return Material.RED_CONCRETE;
    }

    @Override
    public String getNomInventaire() {
        return Lang.referee_item_confirm_game_stop_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return "";
    }

    @Override
    public void performClick(Player joueur) {
        File dossierMapsCustom = new File(mineralcontest.plugin.getDataFolder() + File.separator + "worlds");
        if (!dossierMapsCustom.exists()) {
            return;
        }
        File dossierMap = new File(dossierMapsCustom, this.map);
        if (dossierMap.isDirectory()) {
            DirectoryManager.deleteDirectory(dossierMap);
            joueur.sendMessage(mineralcontest.prefixPrive + "Map got deleted");
            joueur.closeInventory();
            Thread t = new Thread(() -> WorldDownloader.getMaps(true));
            t.start();
        }
    }
}

