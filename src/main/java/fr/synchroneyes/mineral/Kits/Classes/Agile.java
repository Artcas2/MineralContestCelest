package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class Agile extends KitAbstract {
    private double vitesseSupplementairePourcentage = 25.0;

    @Override
    public String getNom() {
        return Lang.kit_agile_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_agile_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_BOOTS;
    }

    @EventHandler
    public void onKitSelected(PlayerKitSelectedEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        this.addPlayerBonus(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        if (!this.isPlayerUsingThisKit(event.getJoueur())) {
            return;
        }
        this.addPlayerBonus(event.getJoueur());
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            this.addPlayerBonus(joueur);
        }
    }

    @EventHandler
    public void onPlayerReceiveFallDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            Player joueur = (Player)entityDamageEvent.getEntity();
            if (!this.isPlayerUsingThisKit(joueur)) {
                return;
            }
            if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
                entityDamageEvent.setCancelled(true);
            }
        }
    }

    private void addPlayerBonus(Player joueur) {
        float defaultSpeed = 0.2f;
        double nouvelleValeur = (double)defaultSpeed + (double)defaultSpeed * this.vitesseSupplementairePourcentage / 100.0;
        joueur.setWalkSpeed((float)nouvelleValeur);
    }
}

