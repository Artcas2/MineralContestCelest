package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnDrop extends CommandTemplate {
    public SpawnDrop() {
        this.accessCommande.add(4);
        this.accessCommande.add(10);
        this.accessCommande.add(0);
        this.accessCommande.add(14);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
        playerGroupe.getGame().getParachuteManager().spawnNewParachute();
        return false;
    }

    @Override
    public String getCommand() {
        return "spawndrop";
    }

    @Override
    public String getDescription() {
        return "Spawn un coffre";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}

