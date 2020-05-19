package fr.groups.Commands.Vote;

import fr.groups.Commands.CommandTemplate;
import fr.groups.Core.Groupe;
import fr.groups.Core.MapVote;
import fr.groups.Utils.Etats;
import fr.mineral.Core.Game.Game;
import fr.mineral.Translation.Lang;
import fr.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartVote extends CommandTemplate {

    public StartVote() {
        super();
        constructArguments();

        this.accessCommande.add(PLAYER_COMMAND);
        this.accessCommande.add(GROUP_REQUIRED);
        this.accessCommande.add(GROUP_ADMIN);
    }

    @Override
    public String getCommand() {
        return "startvote";
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player joueur = (Player) commandSender;
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);

            try {
                canPlayerUseCommand(joueur, args);
            } catch (Exception e) {
                joueur.sendMessage(mineralcontest.prefixErreur + e.getMessage());
                return false;
            }

            Game groupGame = playerGroup.getGame();
            if (groupGame.isGameStarted() || groupGame.isGamePaused() || groupGame.isPreGame()) {
                commandSender.sendMessage(mineralcontest.prefixErreur + Lang.game_already_started.toString());
                return false;
            }

            playerGroup.setEtat(Etats.VOTE_EN_COURS);
            playerGroup.initVoteMap();
            playerGroup.setGroupLocked(true);


            if (!joueur.getWorld().equals(mineralcontest.plugin.pluginWorld)) {
                commandSender.sendMessage(mineralcontest.prefixErreur + Lang.error_command_can_only_be_used_hub_world.toString());
                return false;
            }

        } else {
            commandSender.sendMessage(mineralcontest.prefixErreur + Lang.error_command_can_only_be_used_in_game.toString());
            return false;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Permet de lancer le vote";
    }

    @Override
    public String getPermissionRequise() {
        return "";
    }
}
