package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RefereeCommand extends CommandTemplate {
    @Override
    public String getCommand() {
        return "referee";
    }

    @Override
    public String getDescription() {
        return "Permet de devenir arbitre";
    }

    @Override
    public String[] setCommands() {
        return new String[]{"arbitre"};
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    public RefereeCommand() {
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
        this.addArgument("joueurCible", false);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        if (args.length == 1 && (joueur = Bukkit.getPlayer((String)args[0])) == null) {
            commandSender.sendMessage(mineralcontest.prefixErreur + "Joueur inconnu.");
            return false;
        }
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        try {
            if (playerGroup.getGame().isReferee(joueur)) {
                commandSender.sendMessage(mineralcontest.prefixAdmin + joueur.getDisplayName() + " n'est plus arbitre");
                playerGroup.getGame().removeReferee(joueur, true);
            } else {
                playerGroup.getGame().addReferee(joueur);
                commandSender.sendMessage(mineralcontest.prefixAdmin + joueur.getDisplayName() + " est d\u00e9sormais arbitre");
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return false;
    }
}

