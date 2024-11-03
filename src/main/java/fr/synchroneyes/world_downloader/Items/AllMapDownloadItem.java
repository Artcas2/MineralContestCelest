package fr.synchroneyes.world_downloader.Items;

import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.world_downloader.Items.ItemInterface;
import fr.synchroneyes.world_downloader.Items.MapDownloadItem;
import fr.synchroneyes.world_downloader.MapInfo;
import fr.synchroneyes.world_downloader.WorldDownloader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AllMapDownloadItem extends ItemInterface {
    private List<MapDownloadItem> mapsInfo = new ArrayList<MapDownloadItem>();
    private List<MapDownloadItem> mapsToDownload = new ArrayList<MapDownloadItem>();
    private BossBar downloadStatusBar;

    @Override
    public Material getItemMaterial() {
        return Material.DIAMOND;
    }

    @Override
    public String getNomInventaire() {
        return "ALL MAPS";
    }

    @Override
    public String getDescriptionInventaire() {
        return "Permet de t\u00e9l\u00e9charger toutes les cartes disponibles";
    }

    @Override
    public synchronized void performClick(Player joueur) {
        LinkedList<MapInfo> maps_available = WorldDownloader.getMaps(false);
        MapVote mapVote = new MapVote();
        ArrayList<String> maps_telecharger = mapVote.getMaps();
        for (MapInfo map : maps_available) {
            if (maps_telecharger.contains(map.map_folder_name)) continue;
            this.mapsToDownload.add(MapDownloadItem.fromMapInfo(map));
        }
        if (this.mapsToDownload.isEmpty()) {
            return;
        }
        joueur.closeInventory();
        mineralcontest.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)mineralcontest.plugin, () -> {
            double currentMapIndex = 1.0;
            double maxMapIndex = this.mapsToDownload.size() + 1;
            for (MapDownloadItem map : this.mapsToDownload) {
                this.updateDownloadBar(joueur, currentMapIndex, maxMapIndex, map.getMapName());
                try {
                    WorldDownloader.download(map, joueur);
                    currentMapIndex += 1.0;
                } catch (Exception e) {
                    joueur.sendMessage(e.getMessage());
                    e.printStackTrace();
                }
            }
            this.downloadStatusBar.removeAll();
        });
    }

    public void updateDownloadBar(Player downloader, double currentMapIndex, double maxIndex, String currentMapName) {
        if (this.downloadStatusBar == null) {
            this.downloadStatusBar = Bukkit.createBossBar((String)currentMapName, (BarColor)BarColor.BLUE, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);
        }
        this.downloadStatusBar.setTitle(currentMapName + " " + (int)currentMapIndex + "/" + (int)maxIndex);
        double status = currentMapIndex / maxIndex;
        this.downloadStatusBar.setProgress(status);
        this.downloadStatusBar.addPlayer(downloader);
    }
}

