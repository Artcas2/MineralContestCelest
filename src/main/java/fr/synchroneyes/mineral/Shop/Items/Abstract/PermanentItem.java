package fr.synchroneyes.mineral.Shop.Items.Abstract;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PermanentItem extends ShopItem {
    @Override
    public int getNombreUtilisations() {
        return Integer.MAX_VALUE;
    }

    public int getNombreUtilisationsRestantes() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEnabledOnRespawn() {
        return true;
    }

    @Override
    public boolean isEnabledOnPurchase() {
        return true;
    }

    @Override
    public boolean isEnabledOnReconnect() {
        return true;
    }

    @Override
    public void onPlayerBonusAdded() {
        this.onItemUse();
    }

    @Override
    public String getPurchaseText() {
        return "Vous avez achet\u00e9 le bonus permanent: " + this.getNomItem();
    }

    @Override
    public boolean isEnabledOnDeathByAnotherPlayer() {
        return false;
    }

    @Override
    public boolean isEnabledOnDeath() {
        return false;
    }

    @Override
    public ItemStack toItemStack(Player joueur) {
        ItemStack defaultItem = this.defaultItemStack(joueur);
        if (mineralcontest.getPlayerGame(joueur) != null && mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur) != null) {
            Game partie = mineralcontest.getPlayerGame(joueur);
            PlayerBonus bonusManager = partie.getPlayerBonusManager();
            if (bonusManager == null) {
                return defaultItem;
            }
            if (bonusManager.doesPlayerHaveThisBonus(this.getClass(), joueur)) {
                ItemMeta meta = defaultItem.getItemMeta();
                meta.setDisplayName(ChatColor.STRIKETHROUGH + "" + ChatColor.BLUE + Lang.translate(this.getNomItem()));
                defaultItem.setItemMeta(meta);
                return defaultItem;
            }
            return defaultItem;
        }
        return defaultItem;
    }

    private ItemStack defaultItemStack(Player joueur) {
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
}

