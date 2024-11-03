package fr.synchroneyes.groups.Commands.Groupe;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreerGroupe extends CommandTemplate {
    public CreerGroupe() {
        this.addArgument("nom", true);
        this.constructArguments();
        this.accessCommande.add(4);
        this.accessCommande.add(9);
        this.accessCommande.add(1);
        this.accessCommande.add(12);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        if (!joueur.getWorld().equals((Object)mineralcontest.plugin.pluginWorld)) {
            commandSender.sendMessage(mineralcontest.prefixErreur + Lang.error_command_can_only_be_used_hub_world.toString());
            return false;
        }
        Groupe nouveauGroupe = new Groupe();
        nouveauGroupe.setNom(args[0]);
        nouveauGroupe.addJoueur(joueur);
        nouveauGroupe.addAdmin(joueur);
        mineralcontest.plugin.creerNouveauGroupe(nouveauGroupe);
        return false;
    }

    @Override
    public String getCommand() {
        return "creergroupe";
    }

    @Override
    public String getDescription() {
        return "Permet de cr\u00e9er un groupe";
    }

    @Override
    public String getPermissionRequise() {
        return "";
    }
}

