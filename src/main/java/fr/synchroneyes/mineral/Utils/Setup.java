package fr.synchroneyes.mineral.Utils;

import fr.synchroneyes.mineral.Utils.Door.AutomaticDoors;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Setup {
    public static boolean premierLancement = true;
    public static int etape = 0;
    public static int maxDoorsCount = 9;
    public static Setup instance;
    public static Location emplacementTemporaire;
    public static Player Joueur;
    public static boolean addDoors;
    public static LinkedList<Block> porteBleu;
    public static LinkedList<Block> porteJaune;
    public static LinkedList<Block> porteRouge;

    public Setup() {
        instance = this;
        porteBleu = new LinkedList();
        porteRouge = new LinkedList();
        porteJaune = new LinkedList();
    }

    public static void displayInfos(Player joueur) {
        switch (etape) {
            case 0: {
                mineralcontest.debug = true;
                Joueur = joueur;
                joueur.sendMessage("======================");
                joueur.sendMessage(mineralcontest.prefixPrive + "Bienvenue dans la mise en place du plugin.");
                joueur.sendMessage(mineralcontest.prefixPrive + "Vous allez \u00eatre guid\u00e9 pour mettre en place votre partie");
                joueur.sendMessage("======================");
                Setup.setEtape(1);
                Setup.displayInfos(joueur);
                break;
            }
            case 1: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre dans la base " + ChatColor.RED + "ROUGE");
                joueur.sendMessage(mineralcontest.prefixPrive + "Et effectuez un clic droit sur le bloc o\u00f9 les joueurs apparaitrons");
                break;
            }
            case 2: {
                joueur.sendMessage("======================");
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez selectionner l'emplacement du coffre de l'\u00e9quipe " + ChatColor.RED + "ROUGE");
                break;
            }
            case 3: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre dans la base " + ChatColor.YELLOW + "JAUNE");
                joueur.sendMessage(mineralcontest.prefixPrive + "Et effectuez un clic droit sur le bloc o\u00f9 les joueurs apparaitrons");
                break;
            }
            case 4: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez selectionner l'emplacement du coffre de l'\u00e9quipe " + ChatColor.YELLOW + "JAUNE");
                break;
            }
            case 5: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre dans la base " + ChatColor.BLUE + "BLEU");
                joueur.sendMessage(mineralcontest.prefixPrive + "Et effectuez un clic droit sur le bloc o\u00f9 les joueurs apparaitrons");
                break;
            }
            case 6: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez selectionner l'emplacement du coffre de l'\u00e9quipe " + ChatColor.BLUE + "BLEU");
                break;
            }
            case 7: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre dans l'ar\u00e8ne et cliquer o\u00f9 le coffre d'ar\u00e8ne doit apparaitre");
                break;
            }
            case 8: {
                mineralcontest.getPlayerGame(joueur).getArene().setCoffre(Setup.getEmplacementTemporaire());
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre dans l'ar\u00e8ne et cliquer o\u00f9 le /arene teleportera les gens");
                break;
            }
            case 9: {
                mineralcontest.getPlayerGame(joueur).getArene().setTeleportSpawn(Setup.getEmplacementTemporaire());
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez vous rendre o\u00f9 les joueurs apparaitront lorsque la map sera charg\u00e9e et cliquer o\u00f9 les gens doivent apparaitre.");
                break;
            }
            case 10: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Le setup est  presque termin\u00e9 !");
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez cliquer sur les blocs de la porte bleu");
                addDoors = true;
                break;
            }
            case 11: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Le setup est  presque termin\u00e9 !");
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez cliquer sur les blocs de la porte rouge");
                addDoors = true;
                break;
            }
            case 12: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Le setup est  presque termin\u00e9 !");
                joueur.sendMessage(mineralcontest.prefixPrive + "Veuillez cliquer sur les blocs de la porte jaune");
                addDoors = true;
                break;
            }
            case 13: {
                joueur.sendMessage(mineralcontest.prefixPrive + "Le setup est termin\u00e9 ! Vous pouvez faire /saveworld");
                mineralcontest.debug = false;
                addDoors = true;
            }
        }
    }

    public static void addBlockToPorte(String team, Block b) {
        AutomaticDoors porte = null;
        if (team.equalsIgnoreCase("bleu")) {
            // empty if block
        }
        if (team.equalsIgnoreCase("rouge")) {
            // empty if block
        }
        if (team.equalsIgnoreCase("jaune")) {
            // empty if block
        }
        if (porte == null) {
            return;
        }
        if (!porte.addToDoor(b)) {
            Joueur.sendMessage(mineralcontest.prefixPrive + "Les portes de l'\u00e9quipe " + team + " sont bien enregistr\u00e9es. Faites /valider pour valider votre choix.");
        }
    }

    public static void terminer() {
        premierLancement = false;
    }

    public static void setEtape(int e) {
        etape = e;
    }

    public static int getEtape() {
        return etape;
    }

    public static Location getEmplacementTemporaire() {
        return emplacementTemporaire;
    }

    public static void setEmplacementTemporaire(Location e) {
        emplacementTemporaire = e;
        Setup.sendDetailsToPlayer();
    }

    public static void sendDetailsToPlayer() {
        Joueur.sendMessage(mineralcontest.prefixPrive + "Vous avez selectionner les coordonn\u00e9es X:" + emplacementTemporaire.getX() + ", Y: " + emplacementTemporaire.getY() + ", Z: " + emplacementTemporaire.getZ());
        Joueur.sendMessage(mineralcontest.prefixPrive + "Faites /valider pour valider votre choix.");
    }

    public static void validerChoix() {
        if (emplacementTemporaire == null) {
            Joueur.sendMessage("Commande indisponible pour le moment, veuillez s\u00e9lectionner un block");
            return;
        }
        ++etape;
        Joueur.sendMessage(mineralcontest.prefixPrive + "Votre choix a \u00e9t\u00e9 valid\u00e9");
        emplacementTemporaire.setY(emplacementTemporaire.getY() + 1.0);
        Setup.displayInfos(Joueur);
    }

    static {
        addDoors = false;
    }
}

