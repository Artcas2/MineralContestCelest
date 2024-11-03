package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.RawToCooked;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Mineur extends KitAbstract {
    private double pourcentageReductionVitesse = 15.0;

    @Override
    public String getNom() {
        return Lang.kit_miner_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_miner_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_PICKAXE;
    }

    @EventHandler
    public void onKitSelected(PlayerKitSelectedEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        Player joueur = event.getPlayer();
        Groupe groupe = mineralcontest.getPlayerGroupe(joueur);
        joueur.getInventory().clear();
        for (int index = 9; index < 18; ++index) {
            joueur.getInventory().setItem(index, Mineur.getBarrierItem());
        }
        groupe.getPlayerBaseItem().giveItemsToPlayer(joueur);
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            for (int index = 9; index < 18; ++index) {
                joueur.getInventory().setItem(index, Mineur.getBarrierItem());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        if (!this.isPlayerUsingThisKit(event.getJoueur())) {
            return;
        }
        for (int i = 9; i < 18; ++i) {
            event.getJoueur().getInventory().setItem(i, Mineur.getBarrierItem());
        }
        this.setPlayerEffects(event.getJoueur());
    }

    @EventHandler
    public void onInventoryItemClick(InventoryClickEvent event) {
        if (!this.isPlayerUsingThisKit((Player)event.getWhoClicked())) {
            return;
        }
        if (event.getCursor() != null) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getWhoClicked() instanceof Player) {
                Player joueur = (Player)event.getWhoClicked();
                if (event.getClickedInventory() != null && event.getClickedInventory().equals((Object)joueur.getInventory()) && clickedItem != null && clickedItem.equals((Object)Mineur.getBarrierItem())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockDestroyed(BlockBreakEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        Block blockDetruit = event.getBlock();
        Material materialToDrop = RawToCooked.toCooked(blockDetruit.getType());
        if (materialToDrop != null) {
            int maxXP = 3;
            int minXP = 1;
            int nombreXpRandom = new Random().nextInt(maxXP - minXP) + 1 + minXP;
            event.getPlayer().giveExp(new Random().nextInt(5));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.0f);
            blockDetruit.setType(Material.AIR);
            blockDetruit.getWorld().dropItemNaturally(blockDetruit.getLocation(), new ItemStack(materialToDrop));
        }
    }

    public static ItemStack getBarrierItem() {
        ItemStack barriere = new ItemStack(Material.BARRIER);
        ItemMeta meta = barriere.getItemMeta();
        meta.setDisplayName(Lang.kit_miner_item_denied.toString());
        barriere.setItemMeta(meta);
        return barriere;
    }

    private void setPlayerEffects(Player joueur) {
        double currentSpeed = 0.2f;
        double newSpeed = currentSpeed - currentSpeed * this.pourcentageReductionVitesse / 100.0;
        joueur.setWalkSpeed((float)newSpeed);
        joueur.setHealth(joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }
}

