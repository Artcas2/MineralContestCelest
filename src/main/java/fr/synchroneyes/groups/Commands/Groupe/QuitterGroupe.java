package fr.synchroneyes.groups.Commands.Groupe;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuitterGroupe extends CommandTemplate {
    public QuitterGroupe() {
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(9);
        this.accessCommande.add(12);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        playerGroup.retirerJoueur(joueur);
        return false;
    }

    @Override
    public String getCommand() {
        return "quittergroupe";
    }

    @Override
    public String getDescription() {
        return "Permet de quitter son groupe";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}

