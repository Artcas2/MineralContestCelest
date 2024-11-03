package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Robuste extends KitAbstract {
    private double nombreCoeur = 15.0;
    private double pourcentageReductionDegats = 15.0;
    private double pourcentageReductionVitesse = 15.0;

    @Override
    public String getNom() {
        return Lang.kit_toughguy_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_toughguy_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_CHESTPLATE;
    }

    @EventHandler
    public void onKitSelected(PlayerKitSelectedEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        this.setPlayerEffects(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        if (!this.isPlayerUsingThisKit(event.getJoueur())) {
            return;
        }
        this.setPlayerEffects(event.getJoueur());
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            this.setPlayerEffects(joueur);
        }
    }

    private void setPlayerEffects(Player joueur) {
        double currentSpeed = 0.2f;
        double currentDamage = 1.0;
        joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.nombreCoeur * 2.0);
        double newSpeed = currentSpeed - currentSpeed * this.pourcentageReductionVitesse / 100.0;
        double newDamage = currentDamage - currentDamage * this.pourcentageReductionDegats / 100.0;
        joueur.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(newDamage);
        joueur.setWalkSpeed((float)newSpeed);
        joueur.setHealth(joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }
}

