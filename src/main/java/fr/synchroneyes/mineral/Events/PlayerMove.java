package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Utils.Door.DisplayBlock;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private int houseRadius = 2;
    private static HashMap<Player, Integer> playerPushedTimer = new HashMap();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        World worldEvent = event.getPlayer().getWorld();
        if (mineralcontest.isAMineralContestWorld(worldEvent)) {
            House playerTeam;
            Game game = mineralcontest.getPlayerGame(event.getPlayer());
            if (game != null && game.isGameStarted() && game.isGameInitialized && (playerTeam = game.getPlayerHouse(event.getPlayer())) != null && !game.isReferee(event.getPlayer())) {
                for (House house : game.getHouses()) {
                    double y;
                    Location firstDoorBlock = house.getPorte().getPorte().getFirst().getPosition();
                    Location playerLocation = event.getPlayer().getLocation();
                    double x = Math.pow(firstDoorBlock.getX() - playerLocation.getX(), 2.0);
                    if (Math.sqrt(x + (y = Math.pow(firstDoorBlock.getZ() - playerLocation.getZ(), 2.0))) >= (double)(this.houseRadius + 3) || playerTeam == house && !PlayerUtils.isPlayerInDeathZone(event.getPlayer())) continue;
                    try {
                        for (DisplayBlock blockDePorte : house.getPorte().getPorte()) {
                            Location locationblock = blockDePorte.getBlock().getLocation();
                            if (!Radius.isBlockInRadiusWithDividedYAxis(locationblock, event.getTo(), this.houseRadius, 2)) continue;
                            Location to = event.getFrom();
                            to.setYaw(event.getTo().getYaw());
                            to.setPitch(event.getTo().getPitch());
                            event.getPlayer().teleport(to);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mineralcontest.getPlayerGame(event.getPlayer()) != null && (mineralcontest.getPlayerGame(event.getPlayer()).isGamePaused() || mineralcontest.getPlayerGame(event.getPlayer()).isPreGameAndGameStarted())) {
                if (event.getPlayer().getVelocity().getY() < 0.0783 && !event.getPlayer().isOnGround()) {
                    return;
                }
                Location to = event.getFrom();
                to.setPitch(event.getTo().getPitch());
                to.setYaw(event.getTo().getYaw());
                event.setTo(to);
            }
        }
    }
}

