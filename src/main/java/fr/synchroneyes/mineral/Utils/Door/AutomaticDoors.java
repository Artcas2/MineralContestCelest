package fr.synchroneyes.mineral.Utils.Door;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.Door.DisplayBlock;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AutomaticDoors {
    private Groupe groupe;
    private LinkedList<DisplayBlock> porte = new LinkedList();
    LinkedList<Player> playerNearDoor = new LinkedList();
    private boolean estOuvert = false;

    public AutomaticDoors(Equipe equipe, Groupe g) {
        this.groupe = g;
    }

    public void clear() {
        this.forceCloseDoor();
        this.porte.clear();
        this.playerNearDoor.clear();
    }

    public boolean addToDoor(Block b) {
        boolean ajouter = false;
        if (this.porte.size() == 0) {
            this.porte.add(new DisplayBlock(b));
            if (mineralcontest.debug) {
                mineralcontest.broadcastMessage(ChatColor.GREEN + "+ Le bloc selectionn\u00e9 a \u00e9t\u00e9 ajout\u00e9", this.groupe);
            }
        } else {
            for (DisplayBlock db : this.porte) {
                if (db.getBlock().equals((Object)b)) {
                    this.porte.remove(db);
                    if (!mineralcontest.debug) continue;
                    mineralcontest.broadcastMessage(ChatColor.YELLOW + "- Le bloc selectionn\u00e9 a \u00e9t\u00e9 supprim\u00e9", this.groupe);
                    continue;
                }
                ajouter = true;
            }
            if (ajouter) {
                this.porte.add(new DisplayBlock(b));
                if (mineralcontest.debug) {
                    mineralcontest.broadcastMessage(ChatColor.GREEN + "+ Le bloc selectionn\u00e9 a \u00e9t\u00e9 ajout\u00e9 \u00e0 la porte", this.groupe);
                }
                return true;
            }
            return true;
        }
        return true;
    }

    public void openDoor() {
        if (this.playerNearDoor.size() > 0) {
            for (DisplayBlock db : this.porte) {
                db.hide();
            }
            this.estOuvert = true;
        }
    }

    public void forceCloseDoor() {
        this.playerNearDoor.clear();
        this.closeDoor();
    }

    public void closeDoor() {
        if (this.playerNearDoor.size() == 0) {
            for (DisplayBlock db : this.porte) {
                db.display();
            }
            this.estOuvert = false;
        }
    }

    public LinkedList<DisplayBlock> getPorte() {
        return this.porte;
    }

    public void playerIsNearDoor(Player joueur) {
        if (!this.playerNearDoor.contains(joueur) && !mineralcontest.getPlayerGame(joueur).getArene().getDeathZone().isPlayerDead(joueur)) {
            this.playerNearDoor.add(joueur);
            this.openDoor();
        }
    }

    public void playerIsNotNearDoor(Player joueur) {
        if (this.playerNearDoor.contains(joueur)) {
            this.playerNearDoor.remove(joueur);
            this.closeDoor();
        }
    }
}

