package fr.synchroneyes.mapbuilder.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mapbuilder.Core.Monde;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mcarena extends CommandTemplate {
    private LinkedList<String> actionsPossible = new LinkedList();

    public mcarena() {
        this.actionsPossible.add("setCoffreLocation");
        this.actionsPossible.add("setTeleportLocation");
        this.actionsPossible.add("setSafezoneRadius");
        this.addArgument("action", true);
        this.addArgument("taille", false);
        this.accessCommande.add(4);
        this.constructArguments();
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Monde monde = MapBuilder.monde;
        Player joueur = (Player)commandSender;
        if (args[0].equalsIgnoreCase("setCoffreLocation")) {
            Location coffreLocation = joueur.getLocation().getBlock().getLocation();
            monde.getArene().setCoffre(coffreLocation);
            joueur.sendMessage(mineralcontest.prefixPrive + "La position du coffre a bien \u00e9t\u00e9 ajout\u00e9 en " + coffreLocation.toVector().toString());
            return false;
        }
        if (args[0].equalsIgnoreCase("setTeleportLocation")) {
            Location coffreLocation = joueur.getLocation().getBlock().getLocation();
            monde.getArene().setTeleportSpawn(coffreLocation);
            joueur.sendMessage(mineralcontest.prefixPrive + "La position de t\u00e9l\u00e9portation de /arene a bien \u00e9t\u00e9 ajout\u00e9e en " + coffreLocation.toVector().toString());
            return false;
        }
        if (args[0].equalsIgnoreCase("setSafezoneRadius")) {
            Location teleportLocation = monde.getArene().getTeleportSpawn();
            World _monde = teleportLocation.getWorld();
            _monde.getWorldBorder().setCenter(teleportLocation);
            _monde.getWorldBorder().setSize((double)(Integer.parseInt(args[1]) * 2));
            monde.setArena_safezone_radius(Integer.parseInt(args[1]));
        }
        return false;
    }

    @Override
    public String getCommand() {
        return "mcarena";
    }

    @Override
    public String getDescription() {
        return "Commandes relative \u00e0 la cr\u00e9ation d'une ar\u00e8ne";
    }

    @Override
    public String getPermissionRequise() {
        return "admin";
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] arguments) throws IllegalArgumentException {
        if (sender instanceof Player) {
            Player joueur = (Player)sender;
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return null;
            }
            if (arguments.length == 1) {
                String argument = arguments[0];
                ArrayList<String> available_cvar = new ArrayList<String>();
                for (String action : this.actionsPossible) {
                    if (!action.equalsIgnoreCase(argument) && !action.toLowerCase().contains(argument.toLowerCase())) continue;
                    available_cvar.add(action);
                }
                if (available_cvar.isEmpty()) {
                    available_cvar.add("No results");
                }
                return available_cvar;
            }
        }
        return null;
    }
}

