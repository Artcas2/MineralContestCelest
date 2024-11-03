package fr.synchroneyes.mineral.Core.Player.BaseItem.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetDefaultItems extends CommandTemplate {
    public SetDefaultItems() {
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Groupe groupe = mineralcontest.getPlayerGroupe(joueur);
        groupe.getPlayerBaseItem().openInventory(joueur);
        return false;
    }

    @Override
    public String getCommand() {
        return "mcdefaultitems";
    }

    @Override
    public String getDescription() {
        return "Permet de d\u00e9finir les objets par d\u00e9faut";
    }

    @Override
    public String getPermissionRequise() {
        return "";
    }
}

