package fr.synchroneyes.groups.Commands.Groupe;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickPlayerFromGroup extends CommandTemplate {
    public KickPlayerFromGroup() {
        this.addArgument("Nom du joueur", true);
        this.constructArguments();
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
        this.accessCommande.add(9);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
        Player joueurAKick = Bukkit.getPlayer((String)args[0]);
        if (joueurAKick == null) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_no_player_with_this_name.toString());
            return false;
        }
        if (joueurAKick.equals((Object)joueur)) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_you_cant_kick_yourself_from_group.toString());
            return false;
        }
        if (!playerGroupe.containsPlayer(joueurAKick)) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_no_player_with_this_name.toString());
            return false;
        }
        if (playerGroupe.isAdmin(joueurAKick)) {
            if (playerGroupe.isGroupeCreateur(joueur)) {
                playerGroupe.kickPlayer(joueurAKick);
                return false;
            }
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_you_cant_kick_this_player_from_the_group.toString());
            return false;
        }
        if (playerGroupe.isAdmin(joueur)) {
            playerGroupe.kickPlayer(joueurAKick);
            return false;
        }
        return false;
    }

    @Override
    public String getCommand() {
        return "kickplayer";
    }

    @Override
    public String getDescription() {
        return "Exclure un joueur du groupe";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}

