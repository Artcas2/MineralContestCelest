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

public class Guerrier extends KitAbstract {
    private double bonusPercentage = 25.0;
    private double pourcentageReductionVitesse = 15.0;
    private double vieEnMoins = 2.5;

    @Override
    public String getNom() {
        return Lang.kit_warrior_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_warrior_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_SWORD;
    }

    @EventHandler
    public void onRespawn(MCPlayerRespawnEvent event) {
        if (!this.isPlayerUsingThisKit(event.getJoueur())) {
            return;
        }
        this.setPlayerBonus(event.getJoueur());
    }

    @EventHandler
    public void onKitSelected(PlayerKitSelectedEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        this.setPlayerBonus(event.getPlayer());
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            this.setPlayerBonus(joueur);
        }
    }

    private void setPlayerBonus(Player joueur) {
        double valeurDegatsParDefaut = 1.0;
        double currentSpeed = 0.2f;
        double vieParDefaut = 20.0;
        double nouvelleVie = vieParDefaut - this.vieEnMoins * 2.0;
        double newSpeed = currentSpeed - currentSpeed * this.pourcentageReductionVitesse / 100.0;
        joueur.setWalkSpeed((float)newSpeed);
        double nouvelleValeur = valeurDegatsParDefaut + valeurDegatsParDefaut * this.bonusPercentage / 100.0;
        joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(nouvelleVie);
        joueur.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(nouvelleValeur);
    }
}

