package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Soutien extends KitAbstract {
    private double nombreCoeur = 10.0;
    private double pourcentageReductionVitesse = 15.0;
    private double radiusHeal = 5.0;
    private double healToGive = 1.0;

    @Override
    public String getNom() {
        return Lang.kit_support_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_support_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_APPLE;
    }

    public void healAroundPlayer(Player joueur) {
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (partie == null) {
            return;
        }
        if (partie.isReferee(joueur)) {
            return;
        }
        Equipe playerTeam = partie.getPlayerTeam(joueur);
        if (playerTeam == null) {
            return;
        }
        List<Entity> entites = joueur.getNearbyEntities(this.radiusHeal, this.radiusHeal, this.radiusHeal);
        for (Entity entite : entites) {
            Player otherPlayer;
            Equipe otherPlayerTeam;
            if (!(entite instanceof Player) || !playerTeam.equals(otherPlayerTeam = partie.getPlayerTeam(otherPlayer = (Player)entite))) continue;
            double maxPlayerHealth = otherPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (otherPlayer.getHealth() >= maxPlayerHealth) continue;
            double currentPlayerHealth = otherPlayer.getHealth();
            double newPlayerHealth = Math.min(this.healToGive + currentPlayerHealth, maxPlayerHealth);
            otherPlayer.setHealth(newPlayerHealth);
            Location playerLocation = joueur.getLocation().clone();
            Location otherPlayerLocation = otherPlayer.getLocation().clone();
            playerLocation.setY(playerLocation.getY() + 3.0);
            otherPlayerLocation.setY(otherPlayerLocation.getY() + 3.0);
            World currentWorld = joueur.getWorld();
            currentWorld.spawnParticle(Particle.VILLAGER_HAPPY, playerLocation, 20);
            currentWorld.spawnParticle(Particle.VILLAGER_HAPPY, otherPlayerLocation, 20);
        }
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            this.setPlayerEffects(joueur);
        }
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        Player joueur = event.getJoueur();
        if (!this.isPlayerUsingThisKit(joueur)) {
            return;
        }
        this.setPlayerEffects(joueur);
    }

    private void setPlayerEffects(Player joueur) {
        double currentSpeed = 0.2f;
        joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.nombreCoeur * 2.0);
        double newSpeed = currentSpeed - currentSpeed * this.pourcentageReductionVitesse / 100.0;
        joueur.setWalkSpeed((float)newSpeed);
    }
}

