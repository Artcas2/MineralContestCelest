package fr.synchroneyes.groups.Commands.Groupe;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinGroupe extends CommandTemplate {
    public JoinGroupe() {
        this.addArgument("Nom du groupe", true);
        this.constructArguments();
        this.accessCommande.add(4);
        this.accessCommande.add(1);
        this.accessCommande.add(9);
        this.accessCommande.add(12);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        for (Groupe groupe : mineralcontest.plugin.groupes) {
            if (!groupe.getNom().equalsIgnoreCase(args[0])) continue;
            if (groupe.isGroupLocked()) {
                if (groupe.isPlayerInvited(joueur)) {
                    groupe.addJoueur(joueur);
                    return true;
                }
                joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_you_cant_join_this_group.toString());
                groupe.sendToadmin(mineralcontest.prefixPrive + "Le joueur " + joueur.getDisplayName() + " a tent\u00e9 de rejoindre le groupe sans invitation. Invitez le avec la commande /invitergroupe <nom>");
                return false;
            }
            groupe.addJoueur(joueur);
            return true;
        }
        joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_group_doesnt_exists.toString());
        return false;
    }

    @Override
    public String getCommand() {
        return "joingroupe";
    }

    @Override
    public String getDescription() {
        return "Permet de rejoindre un groupe";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}

