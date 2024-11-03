package fr.synchroneyes.mapbuilder.Core;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Arena.Arene;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class Monde {
    private LinkedList<House> equipes;
    private Arene arene;
    private Location spawnDepart;
    private String nom;
    private int arena_safezone_radius = 0;
    private int houses_playzone_radius;
    private Groupe groupe = mineralcontest.communityVersion ? new Groupe() : mineralcontest.plugin.getNonCommunityGroup();

    public Monde() {
        this.equipes = new LinkedList();
        this.arene = new Arene(this.groupe);
    }

    public Arene getArene() {
        return this.arene;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Location getSpawnDepart() {
        return this.spawnDepart;
    }

    public void setSpawnDepart(Location spawnDepart) {
        this.spawnDepart = spawnDepart;
    }

    public LinkedList<String> getNomsEquipe() {
        LinkedList<String> noms = new LinkedList<String>();
        for (House e : this.equipes) {
            noms.add(e.getTeam().getNomEquipe());
        }
        return noms;
    }

    public LinkedList<House> getHouses() {
        return this.equipes;
    }

    public void ajouterEquipe(String nom, ChatColor couleur) {
        if (this.isTeamCree(nom)) {
            Bukkit.broadcastMessage((String)(mineralcontest.prefixGlobal + "L'\u00e9quipe " + nom + " existe d\u00e9j\u00e0"));
            return;
        }
        this.equipes.add(new House(nom, couleur, this.groupe));
        Bukkit.broadcastMessage((String)(mineralcontest.prefixGlobal + "L'\u00e9quipe " + couleur + nom + ChatColor.WHITE + " a \u00e9t\u00e9 cr\u00e9e avec succ\u00e8s"));
    }

    public void supprimerEquipe(String nom) {
        House maison = this.getHouseFromNom(nom);
        if (maison == null) {
            return;
        }
        this.equipes.remove(maison);
    }

    public boolean isTeamCree(String nom) {
        return this.getHouseFromNom(nom) != null;
    }

    public House getHouseFromNom(String nom) {
        for (House equipe : this.equipes) {
            if (!equipe.getTeam().getNomEquipe().equalsIgnoreCase(nom)) continue;
            return equipe;
        }
        return null;
    }

    public int getArena_safezone_radius() {
        return this.arena_safezone_radius;
    }

    public void setArena_safezone_radius(int arena_safezone_radius) {
        this.arena_safezone_radius = arena_safezone_radius;
    }

    public int getHouses_playzone_radius() {
        return this.houses_playzone_radius;
    }

    public void setHouses_playzone_radius(int houses_playzone_radius) {
        this.houses_playzone_radius = houses_playzone_radius;
    }

    public Groupe getGroupe() {
        return this.groupe;
    }
}

