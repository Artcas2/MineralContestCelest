package fr.synchroneyes.groups.Commands.Vote;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Vote extends CommandTemplate {
    public Vote() {
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(6);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Bukkit.getLogger().severe("Player voted");
        Player joueur = (Player)commandSender;
        Groupe playerGroupe = null;
        int mapVoter = -1;
        playerGroupe = mineralcontest.getPlayerGroupe(joueur);
        playerGroupe.getMapVote().getMenuVote().openInventory(joueur);
        return false;
    }

    @Override
    public String getCommand() {
        return "vote";
    }

    @Override
    public String getDescription() {
        return "Permet de voter pour un biome";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}

