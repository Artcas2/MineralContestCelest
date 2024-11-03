package fr.synchroneyes.mineral.Shop.Categories.Abstract;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Shop.Items.Abstract.LevelableItem;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Category {
    private LinkedHashMap<ShopItem, Integer> items = new LinkedHashMap();
    private Inventory inventory = Bukkit.createInventory(null, (int)54, (String)this.getNomCategorie());
    private BonusSeller npc;

    public Category(BonusSeller npc) {
        this.npc = npc;
    }

    public abstract String getNomCategorie();

    public abstract Material getItemMaterial();

    public abstract String[] getDescription();

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.getItemMaterial(), 1);
        if (item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(this.getNomCategorie());
            LinkedList<String> description = new LinkedList<String>();
            description.addAll(Arrays.asList(this.getDescription()));
            meta.setLore(description);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openMenuToPlayer(Player joueur) {
        this.inventory.clear();
        for (Category categorie : this.npc.getCategories_dispo()) {
            this.inventory.addItem(new ItemStack[]{categorie.toItemStack()});
        }
        int dernierePositionCategorie = this.npc.getCategories_dispo().size();
        int ligneActuelle = (int)Math.ceil((float)dernierePositionCategorie / 9.0f);
        int indexInventaire = 0;
        for (indexInventaire = ligneActuelle * 9; indexInventaire < (ligneActuelle + 1) * 9; ++indexInventaire) {
            this.inventory.setItem(indexInventaire, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        for (Map.Entry<ShopItem, Integer> item : this.items.entrySet()) {
            this.inventory.setItem(indexInventaire++, item.getKey().toItemStack(joueur));
        }
        joueur.openInventory(this.inventory);
    }

    public void addItemToInventory(ShopItem item, int position) {
        if (this.items.containsKey(item)) {
            this.items.replace(item, position);
        } else {
            this.items.put(item, position);
        }
    }

    public void onCategoryItemClick(InventoryClickEvent event) {
        Player joueur = (Player)event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (playerGroup == null) {
            return;
        }
        PlayerBonus playerBonusManager = playerGroup.getGame().getPlayerBonusManager();
        if (clickedItem == null) {
            return;
        }
        for (Category category : this.npc.getCategories_dispo()) {
            if (!clickedItem.equals((Object)category.toItemStack())) continue;
            category.openMenuToPlayer(joueur);
            return;
        }
        for (Map.Entry entry : this.items.entrySet()) {
            ItemStack _inventoryItem = ((ShopItem)entry.getKey()).toItemStack((Player)event.getWhoClicked());
            if (!clickedItem.equals((Object)_inventoryItem)) continue;
            if (playerBonusManager.canPlayerAffordItem((ShopItem)entry.getKey(), joueur)) {
                playerBonusManager.purchaseItem(joueur, (ShopItem)entry.getKey());
                joueur.openInventory(this.npc.getInventory());
            } else {
                Equipe playerTeam = playerGroup.getPlayerTeam(joueur);
                if (playerTeam == null) {
                    joueur.sendMessage(mineralcontest.prefixErreur + Lang.shopitem_player_with_no_team_cant_buy.toString());
                    return;
                }
                int score = playerTeam.getScore();
                if (score < ((ShopItem)entry.getKey()).getPrice()) {
                    joueur.sendMessage(mineralcontest.prefixErreur + Lang.shopitem_not_enought_credit.toString());
                    return;
                }
                if (!((ShopItem)entry.getKey()).isBonusCompatibleWithKits() && playerGroup.getKitManager().isKitsEnabled()) {
                    joueur.sendMessage(mineralcontest.prefixErreur + Lang.shopitem_not_compatible_with_kits.toString());
                    return;
                }
                if (entry.getKey() instanceof LevelableItem) {
                    try {
                        LevelableItem lvl_item = LevelableItem.fromClass(((LevelableItem)entry.getKey()).getRequiredLevel());
                        String bonus_to_buy_before = Lang.shopitem_bonus_required.toString();
                        bonus_to_buy_before = bonus_to_buy_before.replace("%bonus", lvl_item.getNomItem());
                        joueur.sendMessage(mineralcontest.prefixErreur + bonus_to_buy_before);
                        return;
                    } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                joueur.sendMessage(mineralcontest.prefixErreur + Lang.shopitem_not_enought_credit.toString());
            }
            return;
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}

