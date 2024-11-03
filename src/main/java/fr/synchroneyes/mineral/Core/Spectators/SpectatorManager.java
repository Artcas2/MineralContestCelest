package fr.synchroneyes.mineral.Core.Spectators;

import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.Spectators.Spectator;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SpectatorManager implements Listener {
    private Queue<Spectator> spectateurs = new LinkedBlockingQueue<Spectator>();
    private BukkitTask boucle;
    private Game partie;
    private int delayBoucle = 1;

    public SpectatorManager(Game partie) {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    public void ajouterSpectateur(Player joueur) {
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (partie == null) {
            return;
        }
        Spectator spectator = new Spectator(joueur);
        if (this.isPlayerSpectator(joueur)) {
            return;
        }
        Equipe playerTeam = partie.getPlayerTeam(joueur);
        if (playerTeam == null) {
            spectator.fillSpectatablePlayerList(partie);
        } else {
            spectator.fillSpectatablePlayerList(playerTeam);
        }
        this.spectateurs.add(spectator);
    }

    public void supprimerSpectateur(Player joueur) {
        Spectator playerSpectator = null;
        for (Spectator spectator : this.spectateurs) {
            if (!spectator.getJoueur().equals((Object)joueur)) continue;
            playerSpectator = spectator;
        }
        if (playerSpectator != null) {
            this.spectateurs.remove(playerSpectator);
        }
        if (this.spectateurs.isEmpty()) {
            if (this.boucle != null) {
                this.boucle.cancel();
            }
            if (this.boucle != null) {
                this.boucle = null;
            }
        }
    }

    public boolean isPlayerSpectator(Player player) {
        for (Spectator spectator : this.spectateurs) {
            if (!spectator.getJoueur().equals((Object)player) || spectator.getCurrent_spectated_player() == null) continue;
            return true;
        }
        return false;
    }

    public Spectator getSpectator(Player joueur) {
        if (!this.isPlayerSpectator(joueur)) {
            return null;
        }
        for (Spectator spectator : this.spectateurs) {
            if (!spectator.getJoueur().equals((Object)joueur)) continue;
            return spectator;
        }
        return null;
    }

    public void startSpectateLoop() {
        if (this.boucle != null) {
            this.boucle.cancel();
            this.boucle = null;
        }
        this.boucle = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, this::doSpectateTick, 0L, (long)this.delayBoucle);
    }

    private void doSpectateTick() {
        for (Spectator spectator : this.spectateurs) {
            spectator.teleportPlayer();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player joueur = event.getPlayer();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        if (!this.isPlayerSpectator(joueur)) {
            return;
        }
        event.setCancelled(true);
        Spectator spectator = this.getSpectator(joueur);
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            spectator.spectateNextPlayer();
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            spectator.spectatePreviousPlayer();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player joueur = event.getPlayer();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        if (!this.isPlayerSpectator(joueur)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDeath(PlayerDeathByPlayerEvent event) {
        Game playerGame;
        Player joueur = event.getPlayerDead();
        if (!mineralcontest.isInAMineralContestWorld(joueur)) {
            return;
        }
        if (this.isPlayerSpectator(joueur)) {
            return;
        }
        if (this.boucle == null) {
            this.startSpectateLoop();
        }
        if ((playerGame = mineralcontest.getPlayerGame(joueur)) == null || !playerGame.isGameStarted()) {
            return;
        }
        for (Player membre_partie : playerGame.groupe.getPlayers()) {
            membre_partie.hidePlayer((Plugin)mineralcontest.plugin, joueur);
        }
        this.ajouterSpectateur(joueur);
    }

    @EventHandler
    public void OnPlayerRespawn(MCPlayerRespawnEvent event) {
        Player joueur = event.getJoueur();
        this.supprimerSpectateur(joueur);
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (!partie.isReferee(joueur)) {
            for (Player membre_partie : partie.groupe.getPlayers()) {
                membre_partie.showPlayer((Plugin)mineralcontest.plugin, joueur);
                joueur.showPlayer((Plugin)mineralcontest.plugin, membre_partie);
            }
            Equipe playerTeam = partie.getPlayerTeam(joueur);
            if (playerTeam != null) {
                PlayerUtils.teleportPlayer(joueur, playerTeam.getMaison().getHouseLocation().getWorld(), playerTeam.getMaison().getHouseLocation());
            }
        }
        joueur.setGameMode(GameMode.SURVIVAL);
        if (this.spectateurs.isEmpty()) {
            if (this.boucle != null) {
                this.boucle.cancel();
            }
            this.boucle = null;
        }
    }
}

