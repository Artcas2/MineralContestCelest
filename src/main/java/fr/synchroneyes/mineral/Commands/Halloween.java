package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Halloween extends CommandTemplate {
    private static int compteur = 1;

    public Halloween() {
        this.accessCommande.add(4);
        this.accessCommande.add(14);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
    }

    @Override
    public String getCommand() {
        return "halloween";
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getPermissionRequise() {
        return "null";
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Game playerGame = mineralcontest.getPlayerGame(joueur);
        return false;
    }
}

