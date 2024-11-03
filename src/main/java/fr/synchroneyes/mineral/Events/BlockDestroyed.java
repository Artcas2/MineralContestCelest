package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Core.Game.BlockManager;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Kits.Classes.Mineur;
import fr.synchroneyes.mineral.Shop.Items.Permanent.AutoLingot;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Statistics.Class.MinerStat;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.Utils.RawToCooked;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BlockDestroyed implements Listener {
    @EventHandler
    public void onBlockDestroyed(BlockBreakEvent event) {
        if (MapBuilder.getInstance().isBuilderModeEnabled) {
            return;
        }
        Player joueur = event.getPlayer();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
        if (mineralcontest.isInMineralContestHub(joueur) && !mineralcontest.enable_lobby_block_protection) {
            return;
        }
        if (playerGroupe == null) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.cant_break_block_here.toString());
            event.setCancelled(true);
            return;
        }
        Game partie = playerGroupe.getGame();
        if (partie.isReferee(joueur)) {
            return;
        }
        if (!partie.isGameStarted() || partie.isPreGame() || partie.isGamePaused()) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.cant_break_block_here.toString());
            event.setCancelled(true);
            return;
        }
        if (partie.isGameStarted()) {
            MCPlayer mcPlayer;
            Material materialToDrop;
            if (event.getBlock().getType() == Material.HOPPER) {
                event.setCancelled(true);
                return;
            }
            int rayonZoneProtege = playerGroupe.getParametresPartie().getCVAR("protected_zone_area_radius").getValeurNumerique();
            Block blockDetruit = event.getBlock();
            Location centreArene = playerGroupe.getGame().getArene().getCoffre().getLocation();
            if (Radius.isBlockInRadius(centreArene, blockDetruit.getLocation(), rayonZoneProtege)) {
                if (this.canBlockBeDestroyed(event.getBlock())) {
                    event.setDropItems(false);
                    return;
                }
                BlockManager blockManager = BlockManager.getInstance();
                if (!blockManager.wasBlockAdded(blockDetruit)) {
                    event.setCancelled(true);
                    joueur.sendMessage(mineralcontest.prefixErreur + Lang.cant_break_block_here.toString());
                    return;
                }
            }
            if (PlayerBonus.getPlayerBonus(AutoLingot.class, joueur) != null && event.isDropItems() && (materialToDrop = RawToCooked.toCooked(blockDetruit.getType())) != null) {
                blockDetruit.setType(Material.AIR);
                blockDetruit.getWorld().dropItemNaturally(blockDetruit.getLocation(), new ItemStack(materialToDrop));
            }
            if (blockDetruit.getState() instanceof InventoryHolder && !partie.isThisBlockAGameChest(blockDetruit)) {
                ((InventoryHolder)blockDetruit.getState()).getInventory().clear();
                event.setDropItems(false);
            }
            if (blockDetruit.getType() == Material.IRON_ORE && !((mcPlayer = mineralcontest.plugin.getMCPlayer(joueur)).getKit() instanceof Mineur)) {
                event.setDropItems(false);
                blockDetruit.getWorld().dropItemNaturally(blockDetruit.getLocation(), new ItemStack(Material.IRON_ORE));
            }
            playerGroupe.getGame().addBlock(event.getBlock(), BlockSaver.Type.DESTROYED);
            playerGroupe.getGame().getStatsManager().register(MinerStat.class, event.getPlayer(), null);
        } else {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.cant_break_block_here.toString());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroyed(VehicleDestroyEvent event) {
        StorageMinecart vehicle;
        Bukkit.getLogger().info(event.getVehicle().getType().toString());
        if (event.getVehicle().getType() == EntityType.MINECART_CHEST && mineralcontest.isAMineralContestWorld((vehicle = (StorageMinecart)event.getVehicle()).getWorld())) {
            vehicle.getInventory().clear();
        }
    }

    private boolean canBlockBeDestroyed(Block b) {
        ArrayList<Material> allowedToBeDestroyed = new ArrayList<Material>();
        allowedToBeDestroyed.add(Material.GRASS);
        allowedToBeDestroyed.add(Material.WHEAT);
        allowedToBeDestroyed.add(Material.TALL_GRASS);
        allowedToBeDestroyed.add(Material.CHORUS_PLANT);
        allowedToBeDestroyed.add(Material.KELP_PLANT);
        allowedToBeDestroyed.add(Material.BROWN_MUSHROOM);
        allowedToBeDestroyed.add(Material.POTTED_BROWN_MUSHROOM);
        allowedToBeDestroyed.add(Material.RED_MUSHROOM_BLOCK);
        allowedToBeDestroyed.add(Material.RED_MUSHROOM);
        allowedToBeDestroyed.add(Material.MUSHROOM_STEW);
        allowedToBeDestroyed.add(Material.SUGAR_CANE);
        allowedToBeDestroyed.add(Material.FERN);
        allowedToBeDestroyed.add(Material.LARGE_FERN);
        allowedToBeDestroyed.add(Material.POTTED_FERN);
        allowedToBeDestroyed.add(Material.DEAD_BUSH);
        allowedToBeDestroyed.add(Material.POTTED_DEAD_BUSH);
        allowedToBeDestroyed.add(Material.ROSE_BUSH);
        allowedToBeDestroyed.add(Material.SWEET_BERRY_BUSH);
        allowedToBeDestroyed.add(Material.VINE);
        allowedToBeDestroyed.add(Material.SUNFLOWER);
        allowedToBeDestroyed.add(Material.CORNFLOWER);
        allowedToBeDestroyed.add(Material.POTTED_CORNFLOWER);
        allowedToBeDestroyed.add(Material.BEETROOT);
        allowedToBeDestroyed.add(Material.BEETROOTS);
        allowedToBeDestroyed.add(Material.SUNFLOWER);
        allowedToBeDestroyed.add(Material.OXEYE_DAISY);
        allowedToBeDestroyed.add(Material.POTTED_OXEYE_DAISY);
        return allowedToBeDestroyed.contains(b.getType());
    }
}

