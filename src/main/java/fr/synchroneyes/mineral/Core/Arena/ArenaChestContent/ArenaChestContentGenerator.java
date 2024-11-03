package fr.synchroneyes.mineral.Core.Arena.ArenaChestContent;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Arena.ArenaChestContent.ArenaChestItem;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Range;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArenaChestContentGenerator {
    private ArrayList<ArenaChestItem> items = new ArrayList();
    private boolean initialized = false;
    private Groupe groupe;

    public ArenaChestContentGenerator(Groupe groupe) {
        this.groupe = groupe;
    }

    public void initialize(File fichier) throws Exception {
        if (fichier == null) {
            this.initializeFromDefaultFile();
        } else {
            this.initializeFromFile(fichier);
        }
    }

    public Inventory generateInventory() throws Exception {
        if (!this.initialized) {
            this.initializeFromDefaultFile();
        }
        GameSettings settings = this.groupe.getParametresPartie();
        int minItem = 1;
        int maxItem = 10;
        try {
            minItem = settings.getCVAR("min_item_in_chest").getValeurNumerique();
            maxItem = settings.getCVAR("max_item_in_chest").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int tailleTableau = this.items.size();
        int minProba = 0;
        Range[] tableauProba = new Range[tailleTableau];
        for (int i = 0; i < tailleTableau; ++i) {
            ArenaChestItem item = this.items.get(i);
            tableauProba[i] = new Range(item.getItemMaterial(), minProba, minProba + item.getItemProbability());
            minProba += item.getItemProbability();
        }
        Inventory inventaire = Bukkit.createInventory(null, (int)27, (String)Lang.arena_chest_title.toString());
        Random random = new Random();
        int numeroGenere = random.nextInt(maxItem - minItem - 1) + minItem;
        for (int i = 0; i < numeroGenere; ++i) {
            int probabiliteGenere = random.nextInt(minProba - 1);
            Material itemMaterial = Range.getInsideRange(tableauProba, probabiliteGenere);
            ItemStack item = new ItemStack(itemMaterial, 1);
            inventaire.addItem(new ItemStack[]{item});
        }
        return inventaire;
    }

    public Inventory generateAirDropInventory(int minItem, int maxItem) throws Exception {
        if (!this.initialized) {
            this.initializeFromDefaultFile();
        }
        int tailleTableau = this.items.size();
        int minProba = 0;
        Range[] tableauProba = new Range[tailleTableau];
        for (int i = 0; i < tailleTableau; ++i) {
            ArenaChestItem item = this.items.get(i);
            tableauProba[i] = new Range(item.getItemMaterial(), minProba, minProba + item.getItemProbability());
            minProba += item.getItemProbability();
        }
        Inventory inventaire = Bukkit.createInventory(null, (int)27, (String)Lang.arena_chest_title.toString());
        Random random = new Random();
        int numeroGenere = random.nextInt(maxItem - minItem - 1) + minItem;
        for (int i = 0; i < numeroGenere; ++i) {
            int probabiliteGenere = random.nextInt(minProba - 1);
            Material itemMaterial = Range.getInsideRange(tableauProba, probabiliteGenere);
            ItemStack item = new ItemStack(itemMaterial, 1);
            inventaire.addItem(new ItemStack[]{item});
        }
        return inventaire;
    }

    private void initializeFromFile(File fichier) throws Exception {
        if (fichier == null || !fichier.exists()) {
            this.initializeFromDefaultFile();
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichier);
        this.initializeFromSection(yamlConfiguration.getConfigurationSection("chest_content"));
    }

    private void initializeFromSection(ConfigurationSection section) throws Exception {
        if (section == null) {
            throw new Exception("La section \"chest_content\" n'existe pas dans le fichier de configuration donn\u00e9");
        }
        for (String item_id : section.getKeys(false)) {
            ArenaChestItem item = new ArenaChestItem();
            item.setItemMaterial((String)section.get(item_id + ".name"));
            item.setItemProbability(Integer.parseInt(section.get(item_id + ".probability").toString()));
            this.items.add(item);
            if (!mineralcontest.debug) continue;
            Bukkit.getLogger().severe("ArenaChestContentGenerator: Added " + item.getItemMaterial().toString() + " with=> " + item.getItemProbability());
        }
        this.initialized = true;
    }

    private void initializeFromDefaultFile() throws Exception {
        File fichier = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_arena_chest.toString());
        if (!fichier.exists()) {
         