package fr.synchroneyes.mineral.Shop.Items.Abstract;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Potion;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public abstract class PotionItem extends ShopItem {
    @Override
    public ItemStack toItemStack(Player joueur) {
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (partie != null) {
            Equipe playerTeam = mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur);
            String nomItem = "";
            nomItem = playerTeam == null ? this.getNomItem() : (playerTeam.getScore() >= this.getPrice() ? ChatColor.RESET + "" + ChatColor.GREEN + this.getNomItem() : ChatColor.RESET + "" + ChatColor.RED + this.getNomItem());
            ItemStack potion = Potion.createPotion(this.getPotionType(), this.getPotionLevel(), this.getPotionDuration(), nomItem);
            ItemMeta potionMeta = potion.getItemMeta();
            LinkedList<String> description = new LinkedList<String>();
            for (String ligne : this.getDescriptionItem()) {
                if (playerTeam == null) {
                    description.add(ChatColor.RESET + Lang.translate(ligne));
                    continue;
                }
                if (playerTeam.getScore() >= this.getPrice()) {
                    description.add(ChatColor.RESET + "" + ChatColor.GREEN + Lang.translate(ligne));
                    continue;
                }
                description.add(ChatColor.RESET + "" + ChatColor.RED + Lang.translate(ligne));
            }
            if (playerTeam == null) {
                description.add("Price: " + this.getPrice() + " points");
            } else if (playerTeam.getScore() >= this.getPrice()) {
                description.add(ChatColor.RESET + "" + ChatColor.GREEN + "Price: " + this.getPrice() + " points");
            } else {
                description.add(ChatColor.RESET + "" + ChatColor.RED + "Price: " + this.getPrice() + " points");
            }
            potionMeta.setLore(description);
            potion.setItemMeta(potionMeta);
            return potion;
        }
        ItemStack potion = Potion.createPotion(this.getPotionType(), this.getPotionLevel(), this.getPotionDuration(), this.getNomItem());
        ItemMeta potionMeta = potion.getItemMeta();
        LinkedList<String> description = new LinkedList<String>();
        for (String ligne : this.getDescriptionItem()) {
            description.add(ChatColor.RESET + Lang.translate(ligne));
        }
        description.add("Price: " + this.getPrice() + " points");
        potionMeta.setLore(description);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    @Override
    public void onPlayerBonusAdded() {
        this.onItemUse();
    }

    public abstract PotionEffectType getPotionType();

    public abstract int getPotionLevel();

    public abstract int getPotionDuration();

    @Override
    public Material getItemMaterial() {
        return Material.POTION;
    }

    @Override
    public boolean isEnabledOnRespawn() {
        return false;
    }

    @Override
    public boolean isEnabledOnPurchase() {
        return true;
    }

    @Override
    public boolean isEnabledOnReconnect() {
        return false;
    }

    @Override
    public int getNombreUtilisations() {
        return 1;
    }

    @Override
    public void onItemUse() {
        ItemStack potion = this.toItemStack(this.joueur);
        ItemMeta meta = potion.getItemMeta();
        meta.setLore(null);
        potion.setItemMeta(meta);
        this.joueur.getInventory().addItem(new ItemStack[]{potion});
    }

    @Override
    public String getPurchaseText() {
        return "Vous avez achet\u00e9 une potion: " + this.getNomItem();
    }

    @Override
    public boolean isEnabledOnDeathByAnotherPlayer() {
        return false;
    }

    @Override
    public boolean isEnabledOnDeath() {
        return false;
    }
}

