package fr.synchroneyes.mineral.Core.Coffre.Coffres;

import fr.synchroneyes.mineral.Core.Coffre.Animations;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Coffre.TimeChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.TimeChestOpening;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Kits.Classes.Mineur;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CoffreMortJoueur extends TimeChestAnimation {
    private Player joueurMort;
    private int tailleInventaire;
    private MCPlayer mcPlayer;
    private List<ItemStack> playerInventory;

    public CoffreMortJoueur(int tailleInventaire, AutomatedChestManager manager, Player joueur) {
        super(tailleInventaire, manager);
        this.joueurMort = joueur;
        this.tailleInventaire = tailleInventaire;
        this.mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        this.playerInventory = new ArrayList<ItemStack>();
        for (ItemStack item : this.joueurMort.getInventory()) {
            if (item == null || item.equals((Object)Mineur.getBarrierItem()) || ShopManager.isAnShopItem(item) || item.getType() == Material.POTION) continue;
            this.playerInventory.add(new ItemStack(item.getType(), item.getAmount()));
        }
    }

    @Override
    public int playNoteOnTick() {
        return 0;
    }

    @Override
    public int playNoteOnEnd() {
        return 0;
    }

    @Override
    public void actionToPerformBeforeSpawn() {
    }

    @Override
    public boolean displayWaitingItems() {
        return true;
    }

    @Override
    public String getOpeningChestTitle() {
        return this.joueurMort == null ? "Chargement du titre ..." : Lang.translate(Lang.death_inventory_player_title.toString(), this.joueurMort);
    }

    @Override
    public String getOpenedChestTitle() {
        return this.getOpeningChestTitle();
    }

    @Override
    public ItemStack getWaitingItemMaterial() {
        return new ItemStack(Material.RED_STAINED_GLASS_PANE);
    }

    @Override
    public ItemStack getUsedItemMaterial() {
        return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    }

    @Override
    public LinkedList<Integer> getOpeningSequence() {
        return Animations.FIVE_LINES_HEART.toList();
    }

    @Override
    public Material getChestMaterial() {
        return Material.ENDER_CHEST;
    }

    @Override
    public int getAnimationTime() {
        return 2;
    }

    @Override
    public boolean canChestBeOpenedByMultiplePlayers() {
        return false;
    }

    @Override
    public List<ItemStack> genererContenuCoffre() {
        return this.playerInventory;
    }

    @Override
    public boolean automaticallyGiveItemsToPlayer() {
        return false;
    }

    @Override
    public int getChestAliveTime() {
        MCPlayer player = mineralcontest.plugin.getMCPlayer(this.joueurMort);
        if (player == null) {
            return 60;
        }
        return player.getGroupe().getParametresPartie().getCVAR("drop_chest_on_death_time").getValeurNumerique();
    }

    @Override
    public TimeChestOpening getTimeTriggerAction() {
        return TimeChestOpening.AFTER_OPENING_ANIMATION;
    }
}

