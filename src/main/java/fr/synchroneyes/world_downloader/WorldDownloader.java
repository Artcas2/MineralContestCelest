package fr.synchroneyes.world_downloader;

import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.mapbuilder.Core.Monde;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.UrlFetcher.Urls;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.world_downloader.Commands.mcdownload;
import fr.synchroneyes.world_downloader.Inventories.ConfirmationSuppressionInventory;
import fr.synchroneyes.world_downloader.Inventories.GestionMapsInventory;
import fr.synchroneyes.world_downloader.Inventories.InventoryInterface;
import fr.synchroneyes.world_downloader.Inventories.MapListInventory;
import fr.synchroneyes.world_downloader.InventoryEvent;
import fr.synchroneyes.world_downloader.Items.ItemInterface;
import fr.synchroneyes.world_downloader.Items.MapDownloadItem;
import fr.synchroneyes.world_downloader.MapInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class WorldDownloader {
    private mineralcontest plugin = mineralcontest.plugin;
    private static WorldDownloader instance;
    private CommandMap bukkitCommandMap;
    public LinkedList<MapInfo> maps;
    private Inventory inventaire;
    public LinkedList<InventoryInterface> inventaires;
    public LinkedList<ItemInterface> items;
    public boolean downloading = false;
    public static boolean areMapsLoaded;
    public static Monde monde;
    private BossBar status_telechargement;

    public WorldDownloader() {
        instance = this;
        this.maps = new LinkedList();
        this.printToConsole("Loading world downloader module ...");
        try {
            this.getPluginCommandMap();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        this.inventaire = Bukkit.createInventory(null, (int)9, (String)Lang.map_downloader_inventory_name.toString());
        this.inventaires = new LinkedList();
        this.items = new LinkedList();
        this.registerInventories();
        this.registerItems();
        this.registerEvents();
        this.registerCommands();
    }

    public void initMapLists() {
        this.printToConsole("Loading all map from workshop ...");
        Thread thead = new Thread(() -> {
            int tentatives = 0;
            try {
                for (tentatives = 0; !Urls.areAllUrlFetched && tentatives <= 5; ++tentatives) {
                    Bukkit.getLogger().info(mineralcontest.prefix + " All urls are not fetched yet.... Retrying in 2 sec");
                    Thread.sleep(2000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (tentatives >= 5) {
                areMapsLoaded = true;
            } else {
                WorldDownloader.getMaps(true);
            }
        });
        thead.start();
    }

    private void registerInventories() {
        this.inventaires.add(new MapListInventory(true));
        this.inventaires.add(new GestionMapsInventory(true));
        this.inventaires.add(new ConfirmationSuppressionInventory(null));
    }

    private void registerItems() {
    }

    public Inventory getInventory() {
        this.inventaire.clear();
        for (InventoryInterface inventory : this.inventaires) {
            if (!inventory.isDisplayInMainMenu()) continue;
            this.inventaire.addItem(new ItemStack[]{inventory.toItemStack()});
        }
        for (ItemInterface item : this.items) {
            this.inventaire.addItem(new ItemStack[]{item.toItemStack()});
        }
        return this.inventaire;
    }

    private void getPluginCommandMap() throws NoSuchFieldException, IllegalAccessException {
        Field cmdMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        cmdMapField.setAccessible(true);
        this.bukkitCommandMap = (CommandMap)cmdMapField.get(Bukkit.getPluginManager());
    }

    public static WorldDownloader getInstance() {
        if (instance == null) {
            return new WorldDownloader();
        }
        return instance;
    }

    public static LinkedList<MapInfo> getMaps(boolean download) {
        WorldDownloader worldDownloader = WorldDownloader.getInstance();
        MapVote mapVote = new MapVote();
        ArrayList<String> maps_existing = mapVote.getMaps();
        if (download) {
            areMapsLoaded = false;
            HttpGet request = new HttpGet(Urls.API_URL_WORKSHOP_LIST);
            CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String entityContents = EntityUtils.toString(entity);
                JSONArray maps = new JSONArray(entityContents);
                for (int i = 0; i < maps.length(); ++i) {
                    JSONObject map = maps.getJSONObject(i);
                    MapInfo mapInfo = MapInfo.fromJsonObject(map);
                    if (maps_existing.contains(mapInfo.map_folder_name)) continue;
                    worldDownloader.maps.add(mapInfo);
                }
                WorldDownloader.getInstance().printToConsole(worldDownloader.maps.size() + " maps are available to download from website");
                areMapsLoaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return WorldDownloader.instance.maps;
    }

    private void registerEvents() {
        this.printToConsole("Registering events");
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new InventoryEvent(), (Plugin)this.plugin);
    }

    private void registerCommands() {
        this.printToConsole("Registering commands");
        this.bukkitCommandMap.register("", (Command)new mcdownload());
    }

    public static synchronized void download(MapDownloadItem map, Player joueur) throws Exception {
        WorldDownloader worldDownloader = WorldDownloader.getInstance();
        if (worldDownloader.downloading) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_already_downloading_a_map.toString());
            return;
        }
        WorldDownloader.doDownload(map, joueur);
    }

    private static synchronized void doDownload(MapDownloadItem map, Player joueur) {
        WorldDownloader worldDownloader = WorldDownloader.getInstance();
        Thread thread = new Thread(() -> {
            try {
                worldDownloader.downloading = true;
                File dossierTelechargement = new File(mineralcontest.plugin.getDataFolder() + File.separator + "map_download");
                if (!dossierTelechargement.exists()) {
                    dossierTelechargement.mkdir();
                }
                File fichierTelecharge = new File(dossierTelechargement, map.getMapFileName());
                Bukkit.getLogger().info("Downloading: " + map.getMapUrl());
                FileUtils.copyURLToFile(new URL(map.getMapUrl()), fichierTelecharge);
                joueur.sendMessage(mineralcontest.prefixPrive + Lang.downloading_map_done_now_extracting.toString());
                WorldDownloader.extraireMapTelechargee(map, fichierTelecharge, joueur);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void extraireMapTelechargee(MapDownloadItem map, File fichierTelecharge, Player joueur) throws IOException {
        ZipFile fichierZip = new ZipFile(fichierTelecharge.getAbsoluteFile());
        Enumeration<? extends ZipEntry> enu = fichierZip.entries();
        Enumeration<? extends ZipEntry> enu_copy = fichierZip.entries();
        File dossierCustomMaps = new File(mineralcontest.plugin.getDataFolder() + File.separator + "worlds");
        if (!dossierCustomMaps.exists()) {
            dossierCustomMaps.mkdir();
        }
        while (enu.hasMoreElements()) {
            int length;
            ZipEntry zipEntry = enu.nextElement();
            String name = zipEntry.getName();
            File file = new File(dossierCustomMaps, name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            InputStream is = fichierZip.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            is.close();
            fos.close();
        }
        fichierZip.close();
        WorldDownloader.instance.downloading = false;
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.downloading_map_extracted.toString());
        fichierTelecharge.delete();
    }

    private void createProgressBar(MapDownloadItem map, Player joueur) {
        String downloadTitle = Lang.downloading_map_progress.getDefault();
        downloadTitle = downloadTitle.replace("%mapName%", map.getMapName());
        downloadTitle = downloadTitle.replace("%percentage%", "0");
        if (this.status_telechargement == null) {
            this.status_telechargement = Bukkit.createBossBar((String)downloadTitle, (BarColor)BarColor.BLUE, (BarStyle)BarStyle.SOLID, (BarFlag[])new BarFlag[0]);
        }
        this.status_telechargement.setProgress(0.0);
        this.status_telechargement.addPlayer(joueur);
    }

    public void removePlayerDownloadBar() {
        if (this.status_telechargement != null) {
            this.status_telechargement.removeAll();
        }
    }

    public void updateTeleportBar(double downloaded, MapDownloadItem map, Player joueur) {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfs);
        int mapSize = Integer.parseInt(map.getMapSize());
        double status = downloaded / (double)mapSize;
        String _status = decimalFormat.format(status);
        status = Double.parseDouble(_status);
        int pourcentage = (int)Math.round(status * 100.0);
        this.status_telechargement.setProgress(this.clamp(status, 0.0, 1.0));
        String downloadTitle = Lang.downloading_map_progress.getDefault();
        downloadTitle = downloadTitle.replace("%mapName%", map.getMapName());
        downloadTitle = downloadTitle.replace("%percentage%", pourcentage + "");
        this.status_telechargement.setTitle(downloadTitle);
        this.status_telechargement.removePlayer(joueur);
        this.status_telechargement.addPlayer(joueur);
    }

    protected void printToConsole(String text) {
        String prefix = "[MINERALC] [WORLD-DOWNLOADER] ";
        Bukkit.getLogger().info(prefix + text);
    }

    private double clamp(double valeur, double min, double max) {
        return valeur > max ? max : Math.max(valeur, min);
    }

    static {
        areMapsLoaded = false;
    }
}

