package fr.synchroneyes.mapbuilder.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mapbuilder.Core.Monde;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mcteam extends CommandTemplate {
    private LinkedList<String> actionsPossible;
    private static HashMap<House, LinkedList<Block>> porteEquipe;
    private static HashMap<House, Player> attributionEquipeJoueur;

    public mcteam() {
        if (porteEquipe == null) {
            porteEquipe = new HashMap();
        }
        if (attributionEquipeJoueur == null) {
            attributionEquipeJoueur = new HashMap();
        }
        this.actionsPossible = new LinkedList();
        this.actionsPossible.add("creer");
        this.actionsPossible.add("supprimer");
        this.actionsPossible.add("setSpawn");
        this.actionsPossible.add("addPorte");
        this.actionsPossible.add("setCoffre");
        this.actionsPossible.add("addNPCShop");
        this.addArgument("action", true);
        this.addArgument("nom equipe", true);
        this.addArgument("couleur", false);
        this.accessCommande.add(4);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Monde monde = MapBuilder.monde;
        Player joueur = (Player)commandSender;
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (args[0].equalsIgnoreCase("creer")) {
            if (args.length == 3) {
                return this.creerEquipeHandler(commandSender, args);
            }
            commandSender.sendMessage(mineralcontest.prefixErreur + this.getUsage());
            return false;
        }
        if (args[0].equalsIgnoreCase("supprimer")) {
            if (args.length == 2) {
                return this.supprimerEquipeHandler(commandSender, args);
            }
            commandSender.sendMessage(mineralcontest.prefixErreur + this.getUsage());
            return false;
        }
        if (args[0].equalsIgnoreCase("setSpawn")) {
            if (args.length == 2) {
                try {
                    return this.setSpawnHandler(commandSender, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                commandSender.sendMessage(mineralcontest.prefixErreur + this.getUsage());
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("setCoffre")) {
            if (args.length == 2) {
                try {
                    return this.setCoffreHandler(commandSender, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                commandSender.sendMessage(mineralcontest.prefixErreur + this.getUsage());
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("addNPCShop")) {
            if (playerGroup == null) {
                return false;
            }
            BonusSeller bonusSeller = new BonusSeller(joueur.getLocation());
            playerGroup.getGame().getShopManager().ajouterVendeur(bonusSeller);
            bonusSeller.spawn();
            return false;
        }
        if (args[0].equalsIgnoreCase("addPorte")) {
            if (args.length == 2) {
                try {
                    return this.addPorteHandler(commandSender, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                commandSender.sendMessage(mineralcontest.prefixErreur + this.getUsage());
                return false;
            }
        }
        return false;
    }

    @Override
    public String getCommand() {
        return "mcteam";
    }

    public static House getPlayerAllocatedHouse(Player p) {
        for (Map.Entry<House, Player> couple : attributionEquipeJoueur.entrySet()) {
            if (!couple.getValue().equals((Object)p)) continue;
            return couple.getKey();
        }
        return null;
    }

    public static Map.Entry<House, LinkedList<Block>> getPorteMaison(House house) {
        for (Map.Entry<House, LinkedList<Block>> couple : porteEquipe.entrySet()) {
            if (!couple.getKey().equals(house)) continue;
            return couple;
        }
        return null;
    }

    private void creerPorteMaison(String nomEquipe, Player p) {
        Monde monde = MapBuilder.monde;
        House maison = monde.getHouseFromNom(nomEquipe);
        if (maison == null) {
            return;
        }
        if (mcteam.getPorteMaison(maison) == null) {
            porteEquipe.put(maison, new LinkedList());
            attributionEquipeJoueur.put(maison, p);
        }
    }

    private boolean addPorteHandler(CommandSender commandSender, String[] args) {
        Monde monde = MapBuilder.monde;
        Player joueur = (Player)commandSender;
        House playerHouse = mcteam.getPlayerAllocatedHouse(joueur);
        if (playerHouse != null) {
            Map.Entry<House, LinkedList<Block>> couple = mcteam.getPorteMaison(playerHouse);
            LinkedList<Block> blocks = couple.getValue();
            for (Block block : blocks) {
                playerHouse.getPorte().addToDoor(block);
            }
            joueur.sendMessage(mineralcontest.prefixPrive + "La porte de l'\u00e9quipe " + playerHouse.getTeam().getCouleur() + playerHouse.getTeam().getNomEquipe() + ChatColor.WHITE + " a bien \u00e9t\u00e9 enregistr\u00e9e");
            attributionEquipeJoueur.remove(playerHouse);
            porteEquipe.remove(playerHouse);
            return false;
        }
        String nomMaison = args[1];
        this.creerPorteMaison(nomMaison, joueur);
        playerHouse = monde.getHouseFromNom(nomMaison);
        joueur.sendMessage(mineralcontest.prefixPrive + "Vous allez d\u00e9sormais d\u00e9finir les blocks de la porte de l'\u00e9quipe " + playerHouse.getTeam().getCouleur() + playerHouse.getTeam().getNomEquipe());
        joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez cliquer sur les blocks \u00e0 ajouter");
        return false;
    }

    private boolean supprimerEquipeHandler(CommandSender commandSender, String[] args) {
        Monde monde = MapBuilder.monde;
        String nomEquipe = args[1];
        House equipe = monde.getHouseFromNom(nomEquipe);
        if (equipe == null) {
            commandSender.sendMessage(mineralcontest.prefixErreur + "Cette \u00e9quipe n'existe pas");
            return false;
        }
        monde.supprimerEquipe(nomEquipe);
        commandSender.sendMessage(mineralcontest.prefixPrive + "L'\u00e9quipe " + equipe.getTeam().getNomEquipe() + " a bien \u00e9t\u00e9 supprim\u00e9e");
        return false;
    }

    private boolean setSpawnHandler(CommandSender commandSender, String[] args) throws Exception {
        Monde monde = MapBuilder.monde;
        String nomEquipe = args[1];
        House equipe = monde.getHouseFromNom(nomEquipe);
        if (equipe == null) {
            commandSender.sendMessage(mineralcontest.prefixErreur + "Cette \u00e9quipe n'existe pas");
            return false;
        }
        Player p = (Player)commandSender;
        equipe.setHouseLocation(p.getLocation().getBlock().getLocation());
        p.sendMessage(mineralcontest.prefixPrive + "Le spawn de l'\u00e9quipe " + equipe.getTeam().getCouleur() + equipe.getTeam().getNomEquipe() + ChatColor.WHITE + " a \u00e9t\u00e9 d\u00e9fini en " + equipe.getHouseLocation().toVector().toString());
        return false;
    }

    private boolean setCoffreHandler(CommandSender commandSender, String[] args) throws Exception {
        Monde monde = MapBuilder.monde;
        String nomEquipe = args[1];
        House equipe = monde.getHouseFromNom(nomEquipe);
        if (equipe == null) {
            commandSender.sendMessage(mineralcontest.prefixErreur + "Cette \u00e9quipe n'existe pas");
            return false;
        }
        Player p = (Player)commandSender;
        equipe.setCoffreEquipe(p.getLocation().getBlock().getLocation());
        p.sendMessage(mineralcontest.prefixPrive + "Le coffre de l'\u00e9quipe " + equipe.getTeam().getCouleur() + equipe.getTeam().getNomEquipe() + ChatColor.WHITE + " a \u00e9t\u00e9 d\u00e9fini en " + equipe.getCoffre().getPosition().toVector().toString());
        return false;
    }

    private boolean creerEquipeHandler(CommandSender commandSender, String[] args) {
        Monde monde = MapBuilder.monde;
        String nomEquipe = args[1];
        try {
            ChatColor couleur = ChatColor.valueOf((String)args[2].toUpperCase());
            monde.ajouterEquipe(nomEquipe, couleur);
            return false;
        } catch (IllegalArgumentException i) {
            StringBuilder couleursDispo = new StringBuilder();
            String couleurDispoText = "BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE";
            commandSender.sendMessage(mineralcontest.prefixErreur + "Couleur dispo: " + couleurDispoText);
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Gestion des \u00e9quipes du monde";
    }

    @Override
    public String getPermissionRequise() {
        return "admin";
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] arguments) throws IllegalArgumentException {
        if (sender instanceof Player) {
            Monde monde = MapBuilder.monde;
            Player joueur = (Player)sender;
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return null;
            }
            if (arguments.length == 1) {
                String argument = arguments[0];
                ArrayList<String> available_cvar = new ArrayList<String>();
                for (String action : this.actionsPossible) {
                    if (!action.equalsIgnoreCase(argument) && !action.toLowerCase().contains(argument.toLowerCase())) continue;
                    available_cvar.add(action);
                }
                if (available_cvar.isEmpty()) {
                    available_cvar.add("No results");
                }
                return available_cvar;
            }
            if (arguments.length == 2 && !arguments[0].equalsIgnoreCase("creer")) {
                String nomEquipe = arguments[1].toLowerCase();
                ArrayList<String> equipes = new ArrayList<String>();
                for (House maison : monde.getHouses()) {
                    if (!maison.getTeam().getNomEquipe().toLowerCase().equalsIgnoreCase(nomEquipe) && !maison.getTeam().getNomEquipe().toLowerCase().contains(nomEquipe)) continue;
                    equipes.add(maison.getTeam().getNomEquipe());
                }
                return equipes;
            }
        }
        return null;
    }
}

