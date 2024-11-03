package fr.synchroneyes.mineral.Shop.Items.Abstract;

import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ShopItem {
    private int nombreUtilisationRestante = this.getNombreUtilisations();
    protected Player joueur;

    public abstract void onPlayerBonusAdded();

    public abstract String getNomItem();

    public abstract String[] getDescriptionItem();

    public abstract Material getItemMaterial();

    public boolean isBonusCompatibleWithKits() {
        return true;
    }

    public ItemStack toItemStack(Player joueur) {
        ItemStack item = new ItemStack(this.getItemMaterial(), 1);
        if (item.getItemMeta() != null) {
            LinkedList<String> description = new LinkedList<String>();
            ItemMeta itemMeta = item.getItemMeta();
            if (mineralcontest.getPlayerGame(joueur) != null && mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur) != null) {
                Equipe playerTeam = mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur);
                if (playerTeam.getScore() >= this.getPrice()) {
                    itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + Lang.translate(this.getNomItem()));
                    for (String ligne : this.getDescriptionItem()) {
                        description.add(ChatColor.RESET + "" + ChatColor.GREEN + Lang.translate(ligne));
                    }
                    description.add(ChatColor.RESET + "" + ChatColor.GREEN + "" + this.getPrice() + " points");
                } else {
                    itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.RED + Lang.translate(this.getNomItem()));
                    for (String ligne : this.getDescriptionItem()) {
                        description.add(ChatColor.RESET + "" + ChatColor.RED + Lang.translate(ligne));
                    }
                    description.add(ChatColor.RESET + "" + ChatColor.RED + "" + this.getPrice() + " points");
                }
            } else {
                for (String ligne : this.getDescriptionItem()) {
                    description.add(ChatColor.RESET + "" + ChatColor.RED + Lang.translate(ligne));
                }
                description.add(ChatColor.RESET + "" + this.getPrice() + " points");
            }
            itemMeta.setLore(description);
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public abstract boolean isEnabledOnRespawn();

    public abstract boolean isEnabledOnPurchase();

    public abstract boolean isEnabledOnDeathByAnotherPlayer();

    public abstract boolean isEnabledOnDeath();

    public abstract boolean isEnabledOnReconnect();

    public abstract int getNombreUtilisations();

    public abstract void onItemUse();

    public abstract String getPurchaseText();

    public abstract int getPrice();

    public void setJoueur(Player joueur) {
        this.joueur = joueur;
    }
}

