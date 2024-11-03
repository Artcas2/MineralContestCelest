package fr.synchroneyes.mineral.Core.Spectators;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.CircularList;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spectator {
    private Player joueur;
    private CircularList<Player> joueurs_a_spectate;
    private Player current_spectated_player;
    private int current_spectated_player_index = 0;

    public Spectator(Player joueur) {
        this.joueur = joueur;
        this.joueurs_a_spectate = new CircularList();
        this.current_spectated_player = null;
    }

    public void fillSpectatablePlayerList(Equipe equipe) {
        this.clear();
        for (Player membre_equipe : equipe.getJoueurs()) {
            if (membre_equipe.equals((Object)this.joueur)) continue;
            this.joueurs_a_spectate.add(membre_equipe);
        }
    }

    public void fillSpectatablePlayerList(Game partie) {
        this.clear();
        for (Player membre_partie : partie.groupe.getPlayers()) {
            if (membre_partie.equals((Object)this.joueur)) continue;
            this.joueurs_a_spectate.add(membre_partie);
        }
    }

    public void fillSpectatablePlayerList(Groupe groupe) {
        this.clear();
        for (Player membre_partie : groupe.getPlayers()) {
            if (membre_partie.equals((Object)this.joueur)) continue;
            this.joueurs_a_spectate.add(membre_partie);
        }
    }

    public void clear() {
        this.joueurs_a_spectate.clear();
    }

    public void spectateNextPlayer() {
        if (this.joueurs_a_spectate.isEmpty()) {
            return;
        }
        if (this.current_spectated_player != null) {
            this.joueur.showPlayer((Plugin)mineralcontest.plugin, this.current_spectated_player);
            this.current_spectated_player.showPlayer((Plugin)mineralcontest.plugin, this.joueur);
        }
        this.joueur.setGameMode(GameMode.SPECTATOR);
        int _current_index = this.current_spectated_player_index + 1;
        this.current_spectated_player_index = this.mod(_current_index, this.joueurs_a_spectate.size());
        this.current_spectated_player = this.joueurs_a_spectate.get(this.current_spectated_player_index);
    }

    public void teleportPlayer() {
        if (this.current_spectated_player == null) {
            if (this.joueurs_a_spectate.isEmpty()) {
                this.joueur.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1));
                return;
            }
            this.spectateNextPlayer();
        }
        this.current_spectated_player.hidePlayer((Plugin)mineralcontest.plugin, this.joueur);
        this.joueur.hidePlayer((Plugin)mineralcontest.plugin, this.current_spectated_player);
        Location teleportLocation = new Location(this.joueur.getWorld(), 0.0, 0.0, 0.0, this.current_spectated_player.getLocation().getYaw(), this.current_spectated_player.getLocation().getPitch());
        teleportLocation.setX(this.current_spectated_player.getLocation().getX());
        teleportLocation.setY(this.current_spectated_player.getLocation().getY());
        teleportLocation.setZ(this.current_spectated_player.getLocation().getZ());
        this.joueur.teleport(teleportLocation);
    }

    public int getSpectatablePlayerCount() {
        return this.joueurs_a_spectate.size();
    }

    public void spectatePreviousPlayer() {
        if (this.joueurs_a_spectate.isEmpty()) {
            return;
        }
        if (this.current_spectated_player != null) {
            this.joueur.showPlayer((Plugin)mineralcontest.plugin, this.current_spectated_player);
            this.current_spectated_player.showPlayer((Plugin)mineralcontest.plugin, this.joueur);
        }
        this.joueur.setGameMode(GameMode.SPECTATOR);
        int _current_index = this.current_spectated_player_index - 1;
        this.current_spectated_player_index = this.mod(_current_index, this.joueurs_a_spectate.size());
        this.current_spectated_player = this.joueurs_a_spectate.get(this.current_spectated_player_index);
    }

    private int mod(int valeur, int diviseur) {
        int resultat = 0;
        resultat = valeur < 0 ? (Math.abs(valeur) % diviseur != 0 ? diviseur - Math.abs(valeur) % diviseur : Math.abs(valeur) % diviseur) : valeur % diviseur;
        return resultat;
    }

    public Player getJoueur() {
        return this.joueur;
    }

    public CircularList<Player> getJoueurs_a_spectate() {
        return this.joueurs_a_spectate;
    }

    public Player getCurrent_spectated_player() {
        return this.current_spectated_player;
    }

    public void setCurrent_spectated_player(Player current_spectated_player) {
        this.current_spectated_player = current_spectated_player;
    }

    public int getCurrent_spectated_player_index() {
        return this.current_spectated_player_index;
    }

    public void setCurrent_spectated_player_index(int current_spectated_player_index) {
        this.current_spectated_player_index = current_spectated_player_index;
    }
}

