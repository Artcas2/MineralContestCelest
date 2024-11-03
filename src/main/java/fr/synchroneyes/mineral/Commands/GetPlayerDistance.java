package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GetPlayerDistance extends CommandTemplate {
    public GetPlayerDistance() {
        this.addArgument("nom Joueur", true);
    }

    @Override
    public String getCommand() {
        return "GetPlayerDistance";
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player targetedPlayer = Bukkit.getPlayer((String)args[0]);
        Player joueur = (Player)commandSender;
        if (targetedPlayer == null) {
            joueur.sendMessage("Le joueur n'existe pas");
            return false;
        }
        Location targetedLocation = targetedPlayer.getLocation();
        int x1 = joueur.getLocation().getBlockX();
        int z1 = joueur.getLocation().getBlockZ();
        int x2 = targetedLocation.getBlockX();
        int z2 = targetedLocation.getBlockZ();
        double distance = Math.pow(x1 - x2, 2.0) + Math.pow(z1 - z2, 2.0);
        distance = Math.sqrt(distance);
        targetedLocation.setY((double)(targetedLocation.getBlockY() + 2));
        Location directionVersDestination = targetedLocation.subtract(joueur.getEyeLocation());
        Vector playerDirection = joueur.getEyeLocation().getDirection();
        double angle = directionVersDestination.getPitch();
        angle = Math.toDegrees(angle);
        String movedirection = "";
        if (angle < -135.0 || angle >= 135.0) {
            movedirection = "Front";
        }
        if (angle < 135.0 && angle >= 45.0) {
            movedirection = "right";
        }
        if (angle < 45.0 && angle >= -45.0) {
            movedirection = "back";
        }
        if (angle < -45.0 && angle >= -135.0) {
            movedirection = "left";
        }
        joueur.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(targetedPlayer.getDisplayName() + " " + Math.round(distance)));
        return false;
    }

    public static String getDirection(Location location) {
        double rotation = (location.getYaw() - 90.0f) % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 22.5) {
            return "NORTH";
        }
        if (22.5 <= rotation && rotation < 67.5) {
            return "NORTHEAST";
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return "EAST";
        }
        if (112.5 <= rotation && rotation < 157.5) {
            return "SOUTHEAST";
        }
        if (157.5 <= rotation && rotation < 202.5) {
            return "SOUTH";
        }
        if (202.5 <= rotation && rotation < 247.5) {
            return "SOUTHWEST";
        }
        if (247.5 <= rotation && rotation < 292.5) {
            return "WEST";
        }
        if (292.5 <= rotation && rotation < 337.5) {
            return "NORTHWEST";
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return "NORTH";
        }
        return "NORTH";
    }
}

