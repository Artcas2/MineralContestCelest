package fr.synchroneyes.groups.Commands;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public abstract class CommandTemplate extends BukkitCommand {
    protected final int GROUP_REQUIRED = 0;
    protected final int NO_GROUP = 1;
    protected final int GROUP_ADMIN = 2;
    protected final int GROUP_CREATOR = 3;
    protected final int PLAYER_COMMAND = 4;
    protected final int CONSOLE_COMMAND = 5;
    protected final int GROUP_VOTE_STARTED = 6;
    protected final int REQUIRE_GROUP_UNLOCKED = 7;
    protected final int REQUIRE_GROUP_LOCKED = 8;
    protected final int REQUIRE_COMMUNITY_VERSION = 9;
    protected final int PLAYER_ADMIN = 10;
    protected final int PLAYER_IN_HUB = 11;
    protected final int GAME_NOT_STARTED = 12;
    protected final int VOTE_NOT_DONE = 13;
    protected final int GAME_STARTED = 14;
    protected final int GAME_ENDED = 15;
    protected LinkedHashMap<String, Boolean> arguments;
    protected LinkedList<Integer> accessCommande;

    public abstract String getCommand();

    public String[] setCommands() {
        return null;
    }

    public List<String> getAliases() {
        ArrayList<String> alias = new ArrayList<String>();
        if (this.setCommands() == null) {
            return alias;
        }
        alias.addAll(Arrays.asList(this.setCommands()));
        return alias;
    }

    public abstract String getDescription();

    public abstract String getPermissionRequise();

    public String getErrorMessage() {
        return "You do not have access to this command";
    }

    protected CommandTemplate() {
        super("");
        this.description = this.getDescription();
        this.setName(this.getCommand());
        if (this.getAliases().size() > 0) {
            this.setAliases(this.getAliases());
        }
        this.setPermission(this.getPermissionRequise());
        this.setPermissionMessage(this.getErrorMessage());
        this.setUsage("Usage: /" + this.getCommand() + " " + this.getArgumentsString());
        this.accessCommande = new LinkedList();
        this.arguments = new LinkedHashMap();
        this.constructArguments();
    }

    public void addArgument(String arg, boolean argIsRequired) {
        this.arguments.put(arg, argIsRequired);
        this.constructArguments();
    }

    protected void canPlayerUseCommand(CommandSender p, String[] receivedArgs) throws Exception {
        Groupe playerGroupe = null;
        Iterator iterator = this.accessCommande.iterator();
        while (iterator.hasNext()) {
            int condition = (Integer)iterator.next();
            if (condition == 4) {
                if (!(p instanceof Player)) {
                    throw new Exception(Lang.error_command_can_only_be_used_in_game.toString());
                }
                playerGroupe = mineralcontest.getPlayerGroupe((Player)p);
            }
            if (condition == 5 && p instanceof Player) {
                throw new Exception(Lang.error_command_can_only_be_used_in_game.toString());
            }
            if (condition == 0 && playerGroupe == null) {
                throw new Exception(Lang.error_you_must_be_in_a_group.toString());
            }
            if (!(condition != 2 || playerGroupe != null && playerGroupe.isAdmin((Player)p))) {
                throw new Exception(Lang.error_you_must_be_group_admin.toString());
            }
            if (condition == 1 && playerGroupe != null) {
                throw new Exception(Lang.error_you_already_have_a_group.toString());
            }
            if (condition == 3 && !playerGroupe.isGroupeCreateur((Player)p)) {
                throw new Exception(Lang.error_you_must_be_group_owner.toString());
            }
            if (condition == 6) {
                if (playerGroupe == null) {
                    throw new Exception(Lang.error_you_must_be_in_a_group.toString());
                }
                if (!playerGroupe.getEtatPartie().equals((Object)Etats.VOTE_EN_COURS)) {
                    throw new Exception(Lang.vote_not_enabled.toString());
                }
            }
            if (condition == 8) {
                if (playerGroupe == null) {
                    throw new Exception(Lang.error_you_must_be_in_a_group.toString());
                }
                if (!playerGroupe.isGroupLocked()) {
                    throw new Exception(Lang.error_group_is_not_locked.toString());
                }
            }
            if (condition == 7) {
                if (playerGroupe == null) {
                    throw new Exception(Lang.error_you_must_be_in_a_group.toString());
                }
                if (playerGroupe.isGroupLocked()) {
                    throw new Exception(Lang.error_group_is_locked.toString());
                }
            }
            if (condition == 9 && !mineralcontest.communityVersion) {
                throw new Exception(Lang.error_command_unavailable_in_this_version.toString());
            }
            if (condition == 10 && !p.isOp()) {
                throw new Exception(Lang.error_you_must_be_server_admin.toString());
            }
            if (condition == 11) {
                if (!(p instanceof Player)) {
                    throw new Exception(Lang.error_command_can_only_be_used_in_game.toString());
                }
                Player joueur = (Player)p;
                if (!joueur.getWorld().equals((Object)mineralcontest.plugin.pluginWorld)) {
                    throw new Exception(Lang.error_command_can_only_be_used_hub_world.toString());
                }
            }
            if (condition == 12 && playerGroupe != null && playerGroupe.getGame() != null && (playerGroupe.getGame().isGameStarted() || playerGroupe.getGame().isPreGame())) {
                throw new Exception("command can only be used when no game");
            }
            if (!(condition != 14 || playerGroupe != null && playerGroupe.getGame() != null && playerGroupe.getGame().isGameStarted())) {
                throw new Exception(Lang.game_not_started.toString());
            }
            if (condition != 15 || playerGroupe != null && playerGroupe.getGame() != null && playerGroupe.getGame().isGameEnded()) continue;
            throw new Exception("Game is not ended");
        }
        if (this.arguments.size() != receivedArgs.length) {
            int requiredArgSize = 0;
            for (Map.Entry<String, Boolean> argument : this.arguments.entrySet()) {
                if (!argument.getValue().booleanValue()) continue;
                ++requiredArgSize;
            }
            if (receivedArgs.length != requiredArgSize) {
                throw new Exception(this.getUsage());
            }
        }
    }

    public abstract boolean performCommand(CommandSender var1, String var2, String[] var3);

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            this.canPlayerUseCommand(commandSender, strings);
        } catch (Exception e) {
            commandSender.sendMessage(e.getMessage());
            return false;
        }
        return this.performCommand(commandSender, s, strings);
    }

    public String getArgumentsString() {
        StringBuilder sb = new StringBuilder();
        if (this.arguments == null) {
            this.arguments = new LinkedHashMap();
        }
        for (Map.Entry<String, Boolean> argument : this.arguments.entrySet()) {
            if (argument.getValue().booleanValue()) {
                sb.append(ChatColor.RED + "<" + argument.getKey() + "> " + ChatColor.WHITE);
                continue;
            }
            sb.append(ChatColor.YELLOW + "<" + argument.getKey() + "> " + ChatColor.WHITE);
        }
        return sb.toString();
    }

    public void constructArguments() {
        this.setUsage("Usage: /" + this.getCommand() + " " + this.getArgumentsString());
    }
}

