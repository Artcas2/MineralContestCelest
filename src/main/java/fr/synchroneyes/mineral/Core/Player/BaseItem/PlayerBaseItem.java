package fr.synchroneyes.mineral.Core.Player.BaseItem;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerBaseItem {
    private Inventory inventaire = Bukkit.createInventory(null, (InventoryType)InventoryType.CHEST, (String)Lang.player_base_item_inventory_title.toString());
    private Groupe groupe;
    private PlayerInventory playerInventory = null;
    private Player openingPlayer = null;
    private boolean beingEdited = false;
    private ArrayList<ItemStack> items;

    public PlayerBaseItem(Groupe g) {
        this.groupe = g;
        this.items = new ArrayList();
        this.genererInventaireParDefaut();
    }

    private void genererInventaireParDefaut() {
        File fichierParDefaut = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_player_base_item.toString());
        if (!fichierParDefaut.exists()) {
            Bukkit.getLogger().severe(mineralcontest.prefix + FileList.Config_default_player_base_item.toString() + " doesnt exists");
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierParDefaut);
        ConfigurationSection section = yamlConfiguration.getConfigurationSection("items");
        if (section != null) {
            for (String item_type : section.getKeys(false)) {
                ItemStack item = new ItemStack(Material.valueOf((String)item_type), Integer.parseInt(section.get(item_type).toString()));
                this.inventaire.addItem(new ItemStack[]{item});
                this.items.add(item);
            }
        }
        ItemStack item = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Lang.player_base_item_close_inventory_item_title.toString());
        item.setItemMeta(meta);
        this.inventaire.setItem(this.inventaire.getSize() - 1, item);
    }

    public void openInventory(Player joueur) {
        if (this.beingEdited) {
            return;
        }
        this.playerInventory = joueur.getInventory();
        joueur.closeInventory();
        joueur.openInventory(this.inventaire);
        this.beingEdited = true;
        this.openingPlayer = joueur;
    }

    public void closeInventory(Player joueur) {
        if (!joueur.equals((Object)this.openingPlayer)) {
            return;
        }
        this.openingPlayer = null;
        this.beingEdited = false;
        joueur.closeInventory();
        joueur.getInventory().setContents(this.playerInventory.getContents());
        joueur.getInventory().setArmorContents(this.playerInventory.getArmorContents());
        joueur.getInventory().setItemInMainHand(this.playerInventory.getItemInMainHand());
        joueur.getInventory().setItemInOffHand(this.playerInventory.getItemInOffHand());
        this.items.clear();
        for (ItemStack item : this.inventaire.getContents()) {
            if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName().equals(Lang.player_base_item_close_inventory_item_title.toString())) continue;
            if (item.getItemMeta().getDisplayName().equals(Lang.referee_item_name.toString())) {
                this.inventaire.remove(item);
                continue;
            }
            this.items.add(item);
        }
    }

    public Inventory getInventory() {
        return this.inventaire;
    }

    public boolean isBeingEdited() {
        return this.beingEdited;
    }

    public void giveItemsToPlayer(Player p) {
        if (this.groupe.getGame().isReferee(p)) {
            return;
        }
        for (ItemStack item : this.items) {
            if (item.getType().toString().toUpperCase().contains("BOOTS")) {
                p.getInventory().setBoots(item);
                continue;
            }
            if (item.getType().toString().toUpperCase().contains("CHESTPLATE")) {
                p.getInventory().setChestplate(item);
                continue;
            }
            if (item.getType().toString().toUpperCase().contains("HELMET")) {
                p.getInventory().setHelmet(item);
                continue;
            }
            if (item.getType().toString().toUpperCase().contains("LEGGINGS")) {
                p.getInventory().setLeggings(item);
                continue;
            }
            if (item.getType().toString().toUpperCase().contains("SHIELD")) {
                p.getInventory().setItemInOffHand(item);
                continue;
            }
            p.getInventory().addItem(new ItemStack[]{item});
        }
    }

    public ArrayList<ItemStack> getItems() {
        return this.items;
    }
}

