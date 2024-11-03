package fr.synchroneyes.groups.Utils;

public enum Etats {
    EN_ATTENTE("En attente"),
    VOTE_EN_COURS("Vote en cours"),
    VOTE_TERMINE("Vote termin\u00e9"),
    PREGAME("D\u00e9marrage de la partie"),
    GAME_EN_COURS("Partie en cours"),
    GAME_TERMINE("Partie termin\u00e9e"),
    ATTENTE_DEBUT_PARTIE("En attente du d\u00e9marrage");

    private String nom;

    private Etats(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }
}

