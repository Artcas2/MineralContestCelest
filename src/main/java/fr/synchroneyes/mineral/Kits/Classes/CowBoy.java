package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CowBoy extends KitAbstract {
    private double reductionVitesse = 15.0;
    public HashMap<UUID, Horse> chevaux_joueurs = new HashMap();

    @Override
    public String getNom() {
        return Lang.kit_cowboy_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_cowboy_description.toString();
    }

    @EventHandler
    public void onKitSelected(PlayerKitSelectedEvent event) {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        if (this.chevaux_joueurs.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        this.chevaux_joueurs.put(event.getPlayer().getUniqueId(), null);
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.GOLDEN_HORSE_ARMOR;
    }

    @EventHandler
    public void OnGameStart(MCGameStartedEvent event) {
        Game partie = event.getGame();
        for (Player joueur : partie.groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> this.spawnHorseToPlayer(joueur), 5L);
            this.applyEffectToPlayer(joueur);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent entityEvent) {
        if (entityEvent.getDamager() instanceof Player) {
            Player joueur = (Player)entityEvent.getDamager();
            if (entityEvent.getCause() != EntityDamageEvent.DamageCause.PROJECTILE && joueur.getVehicle() != null && joueur.getVehicle() instanceof Horse) {
                joueur.playSound(joueur.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                joueur.sendMessage(mineralcontest.prefixErreur + "Malheureusement, vous ne pouvez pas attaquer avec cet item sur votre cheval. Utilisez un " + ChatColor.RED + "arc");
                entityEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRideHorse(VehicleEnterEvent event) {
        Horse cheval;
        if (event.getEntered() instanceof Player && event.getVehicle() instanceof Horse && (cheval = (Horse)event.getVehicle()).getOwner() != null && !cheval.getOwner().equals((Object)event.getEntered())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        Player joueur = event.getJoueur();
        if (!this.isPlayerUsingThisKit(joueur)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> this.spawnHorseToPlayer(joueur), 5L);
        this.applyEffectToPlayer(joueur);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathByPlayerEvent event) {
        Player victime = event.getPlayerDead();
        if (!this.isPlayerUsingThisKit(victime)) {
            return;
        }
        this.killPlayerHorse(victime);
    }

    private void applyEffectToPlayer(Player joueur) {
        double currentSpeed = 0.2f;
        double newSpeed = currentSpeed - currentSpeed * this.reductionVitesse / 100.0;
        joueur.setWalkSpeed((float)newSpeed);
        joueur.setHealth(joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    }

    public static ItemStack getItemCheval() {
        return new ItemStack(Material.HORSE_SPAWN_EGG);
    }

    private void spawnHorseToPlayer(Player joueur) {
        if (this.chevaux_joueurs.containsKey(joueur.getUniqueId())) {
            Horse ancienCheval = this.chevaux_joueurs.get(joueur.getUniqueId());
            this.killPlayerHorse(joueur);
        } else {
            this.chevaux_joueurs.put(joueur.getUniqueId(), null);
        }
        Horse cheval = (Horse)joueur.getWorld().spawn(joueur.getLocation(), Horse.class);
        cheval.setAdult();
        cheval.setStyle(Horse.Style.BLACK_DOTS);
        cheval.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        cheval.getInventory().setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR));
        cheval.setTamed(true);
        cheval.setOwner((AnimalTamer)joueur);
        cheval.addPassenger((Entity)joueur);
        this.chevaux_joueurs.replace(joueur.getUniqueId(), cheval);
    }

    private void killPlayerHorse(Player joueur) {
        if (this.chevaux_joueurs.get(joueur.getUniqueId()) == null) {
            return;
        }
        Horse cheval = this.chevaux_joueurs.get(joueur.getUniqueId());
        cheval.getInventory().clear();
        cheval.setOwner(null);
        cheval.getInventory().setSaddle(null);
        cheval.setTamed(false);
        cheval.remove();
    }
}

