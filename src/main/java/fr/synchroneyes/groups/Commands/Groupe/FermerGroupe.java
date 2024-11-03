package fr.synchroneyes.groups.Commands.Groupe;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FermerGroupe extends CommandTemplate {
    public FermerGroupe() {
        this.accessCommande.add(4);
        this.accessCommande.add(9);
        this.accessCommande.add(0);
        this.accessCommande.add(12);
        this.accessCommande.add(2);
    }

    @Override
    public String getCommand() {
        return "mc_fermergroupe";
    }

    @Override
    public String getDescription() {
        return "Commande permettant de fermer le groupe";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        Groupe groupe = mcPlayer.getGroupe();
        if (groupe.isGroupLocked()) {
            joueur.sendMessage(mineralcontest.prefixErreur + "Le groupe est d\u00e9j\u00e0 ferm\u00e9!");
            return true;
        }
        groupe.setGroupLocked(true);
        joueur.sendMessage(mineralcontest.prefixPrive + "Le groupe est d\u00e9sormais ferm\u00e9.");
        return false;
    }
}

