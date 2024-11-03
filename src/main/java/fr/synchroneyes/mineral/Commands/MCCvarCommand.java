package fr.synchroneyes.mineral.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Settings.GameCVAR;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class MCCvarCommand extends CommandTemplate {
    public MCCvarCommand() {
        this.addArgument("parametre", true);
        this.addArgument("valeur", false);
        this.accessCommande.add(4);
        this.accessCommande.add(0);
        this.accessCommande.add(2);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player)commandSender;
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (args.length == 1) {
            for (GameCVAR cvar : playerGroup.getParametresPartie().getParametres()) {
                if (!cvar.getCommand().equalsIgnoreCase(args[0])) continue;
                joueur.sendMessage(ChatColor.GREEN + "-----------------");
                joueur.sendMessage(mineralcontest.prefixPrive + cvar.getCommand() + " => " + cvar.getValeur());
                joueur.sendMessage(mineralcontest.prefixPrive + cvar.getDescription());
                joueur.sendMessage(ChatColor.GREEN + "-----------------");
                return false;
            }
            joueur.sendMessage(mineralcontest.prefixErreur + "Param\u00e8tre " + args[0] + " non trouv\u00e9");
            return false;
        }
        if (args.length == 2) {
            for (GameCVAR cvar : playerGroup.getParametresPartie().getParametres()) {
                if (!cvar.getCommand().equalsIgnoreCase(args[0])) continue;
                if (cvar.isNumber() && !NumberUtils.isNumber(args[1])) {
                    joueur.sendMessage(mineralcontest.prefixErreur + cvar.getCommand() + " attend un nombre en param\u00e8tre");
                    return false;
                }
                cvar.setValeur(args[1]);
                try {
                    playerGroup.getParametresPartie().setCVARValeur(cvar.getCommand(), args[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playerGroup.getParametresPartie().saveCVAR(cvar);
                joueur.sendMessage(mineralcontest.prefixPrive + "Valeur mise \u00e0 jour, " + cvar.getCommand() + " => " + cvar.getValeur());
                if (cvar.getCommand().equalsIgnoreCase("enable_monster_in_protected_zone") && cvar.getValeurNumerique() == 0) {
                    for (Entity entite : joueur.getWorld().getEntities()) {
                        if (!(entite instanceof Monster)) continue;
                        entite.remove();
                    }
                }
                if (cvar.getCommand().equalsIgnoreCase("mp_enable_old_pvp")) {
                    for (Player online : mineralcontest.getPlayerGroupe(joueur).getPlayers()) {
                        if (cvar.getValeurNumerique() == 1) {
                            online.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0);
                            continue;
                        }
                        online.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
                    }
                }
                if (cvar.getCommand().equalsIgnoreCase("enable_kits")) {
                    if (cvar.getValeurNumerique() == 1) {
                        playerGroup.getKitManager().setKitsEnabled(true);
                    } else {
                        playerGroup.getKitManager().setKitsEnabled(false);
                    }
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public String getCommand() {
        return "mcvar";
    }

    @Override
    public String getDescription() {
        return "Permet de modifier un param\u00e8tre de partie";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    public List<String> tabComplete(CommandSender sender, String command, String[] arguments) {
        if (sender instanceof Player) {
            Player joueur = (Player)sender;
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return null;
            }
            if (arguments.length == 1) {
                String argument = arguments[0];
                ArrayList<String> available_cvar = new ArrayList<String>();
                for (GameCVAR cvar : playerGroup.getParametresPartie().getParametres()) {
                    String cvar_renamed = cvar.getCommand();
                    cvar_renamed = cvar_renamed.replace("_", "");
                    if (!cvar.getCommand().equalsIgnoreCase(argument) && !cvar.getCommand().toLowerCase().contains(argument.toLowerCase()) && !cvar_renamed.equalsIgnoreCase(argument) && !cvar_renamed.toLowerCase().contains(argument.toLowerCase())) continue;
                    available_cvar.add(cvar.getCommand());
                }
                if (available_cvar.isEmpty()) {
                    available_cvar.add("No results");
                }
                return available_cvar;
            }
        }
        return null;
    }
}

