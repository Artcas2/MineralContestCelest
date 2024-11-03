package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Core.Game.BlockManager;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Statistics.Class.BuilderStat;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.BlockSaver;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaced implements Listener {
    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (MapBuilder.getInstance().isBuilderModeEnabled) {
            return;
        }
        World worldEvent = event.getPlayer().getWorld();
        Game game = mineralcontest.getPlayerGame(event.getPlayer());
        if (MapBuilder.getInstance().isBuilderModeEnabled) {
            return;
        }
        if (mineralcontest.isAMineralContestWorld(worldEvent)) {
            if (worldEvent.equals((Object)mineralcontest.plugin.pluginWorld)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(mineralcontest.prefixErreur + Lang.cant_interact_block_hub.toString());
                return;
            }
            if (mineralcontest.isInMineralContestHub(event.getPlayer())) {
                event.setCancelled(true);
                Bukkit.getLogger().severe("[MineralContest] Block got placed, but not inside a game ...");
                return;
            }
            if (game == null) {
                return;
            }
            if (game.isReferee(event.getPlayer())) {
                return;
            }
            if (game.isGameStarted() && !game.isGamePaused()) {
                if (event.getBlock().getType() == Material.HOPPER) {
                    event.setCancelled(true);
                    return;
                }
                try {
                    GameSettings settings = game.groupe.getParametresPartie();
                    if (Radius.isBlockInRadius(event.getBlock().getLocation(), game.getArene().getCoffre().getLocation(), settings.getCVAR("protected_zone_area_radius").getValeurNumerique())) {
                        if (settings.getCVAR("mp_enable_block_adding").getValeurNumerique() == 1) {
                            BlockManager blockManager = BlockManager.getInstance();
                            if (!blockManager.isBlockAllowedToBeAdded(event.getBlock())) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(mineralcontest.prefixErreur + Lang.block_not_allowed_to_be_placed.toString());
                            }
                            blockManager.addBlock(event.getBlock());
                            game.getStatsManager().register(BuilderStat.class, event.getPlayer(), null);
                        } else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(mineralcontest.prefixErreur + Lang.cant_interact_block_hub.toString());
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(mineralcontest.prefixErreur + Lang.cant_interact_block_hub.toString());
            }
            game.addAChest(event.getBlock());
            game.addBlock(event.getBlock(), BlockSaver.Type.PLACED);
            game.getStatsManager().register(BuilderStat.class, event.getPlayer(), null);
        }
    }
}

