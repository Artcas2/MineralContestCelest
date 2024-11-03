package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisplayScoreCommand extends CommandTemplate {
    public DisplayScoreCommand() {
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
        this.accessCommande.add(14);
        this.addArgument("type", false);
    }

    @Override
    public String getCommand() {
        return "mc_displayscore";
    }

    @Override
    public String getDescription() {
        return "Permet d'afficher le score dans le chat";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        String[] arguments = new String[]{"name", "podium", "all"};
        Game partie = mineralcontest.getPlayerGame((Player)commandSender);
        if (args.length == 1) {
            String argument;
            boolean exists = false;
            for (String arg : arguments) {
                if (!arg.equals(args[0])) continue;
                exists = true;
            }
            if (!exists) {
                commandSender.sendMessage(ChatColor.RED + "Argument inconnu");
                return false;
            }
            switch (argument = args[0]) {
                case "all": {
                    this.displayScore(partie, true, true, false);
                    break;
                }
                case "name": {
                    this.displayScore(partie, false, true, false);
                    break;
                }
                case "podium": {
                    this.displayScore(partie, false, true, true);
                }
            }
            return true;
        }
        this.displayScore(partie, true, true, false);
        return false;
    }

    private void displayScore(Game partie, boolean displayScore, boolean displayPlace, boolean displayPodium) {
        List<House> maisons = (List<House>) partie.getHouses().clone();
        int max_score = Integer.MIN_VALUE;
        House best_house = null;
        LinkedList<House> ordered_Houses = new LinkedList<House>();
        maisons.removeIf(maison -> maison.getTeam().getJoueurs().isEmpty());
        while (!maisons.isEmpty()) {
            for (House maison2 : maisons) {
                if (maison2.getTeam().getScore() < max_score) continue;
                max_score = maison2.getTeam().getScore();
                best_house = maison2;
            }
            ordered_Houses.add(best_house);
            maisons.remove(best_house);
        }
        partie.groupe.sendToEveryone(ChatColor.GOLD + "===========");
        partie.groupe.sendToEveryone(ChatColor.RED + "" + ChatColor.BOLD + "Leaderboard");
        int indexMaison = 1;
        for (House maison3 : ordered_Houses) {
            String message_a_publier = "";
            if (displayPlace) {
                message_a_publier = message_a_publier + ChatColor.GOLD + "" + indexMaison + " - ";
            }
            message_a_publier = message_a_publier + maison3.getTeam().getCouleur() + maison3.getTeam().getNomEquipe() + ChatColor.RESET;
            if (displayScore) {
                message_a_publier = message_a_publier + " " + maison3.getTeam().getScore() + " point(s)";
            }
            if (displayPodium && indexMaison > 3) break;
            ++indexMaison;
            partie.groupe.sendToEveryone(message_a_publier);
        }
        partie.groupe.sendToEveryone(ChatColor.GOLD + "===========");
    }

    public List<String> tabComplete(CommandSender sender, String command, String[] arguments) {
        if (sender instanceof Player) {
            Player joueur = (Player)sender;
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return null;
            }
            if (arguments.length == 1) {
                String argument = arguments[0];
                ArrayList<String> available_args = new ArrayList<String>();
                available_args.add("all");
                available_args.add("podium");
                available_args.add("name");
                LinkedList<String> available_argument_complete = new LinkedList<String>();
                for (String arg : available_args) {
                    if (!arg.equalsIgnoreCase(argument) && !arg.toLowerCase().contains(argument.toLowerCase())) continue;
                    available_argument_complete.add(arg);
                }
                if (available_argument_complete.isEmpty()) {
                    available_argument_complete.add("No results");
                }
                return available_argument_complete;
            }
        }
        return null;
    }
}

