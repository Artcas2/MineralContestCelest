package fr.synchroneyes.mineral.Core.Parachute;

import fr.synchroneyes.custom_events.MCAirDropSpawnEvent;
import fr.synchroneyes.custom_events.MCAirDropTickEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Parachute.Parachute;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ParachuteManager {
    private Groupe groupe;
    private List<Parachute> parachutes;
    private int timeleft_before_next_drop = Integer.MIN_VALUE;
    private Location nextDropLocation = null;
    private BukkitTask dropsHandler = null;

    public ParachuteManager(Groupe groupe) {
        this.groupe = groupe;
        this.parachutes = new LinkedList<Parachute>();
    }

    public Groupe getGroupe() {
        return this.groupe;
    }

    public void spawnNewParachute() {
        if (this.nextDropLocation == null) {
            this.generateRandomLocation();
        }
        GameLogger.addLog(new Log("parachute_spawn", "Parachute spawned @ " + this.nextDropLocation.getX() + ", " + this.nextDropLocation.getY() + ", " + this.nextDropLocation.getZ(), "parachute_time_reached"));
        Parachute parachute = new Parachute(6.0, this);
        parachute.spawnParachute(this.nextDropLocation);
        this.parachutes.add(parachute);
        String chestLocationText = Lang.airdrop_subtitle.toString();
        chestLocationText = chestLocationText.replace("%x", this.nextDropLocation.getBlockX() + "");
        chestLocationText = chestLocationText.replace("%z", this.nextDropLocation.getBlockZ() + "");
        int nombreSecondeAffichage = this.groupe.getParametresPartie().getCVAR("drop_display_time").getValeurNumerique();
        for (Player joueur : this.groupe.getPlayers()) {
            joueur.sendTitle(Lang.airdrop_title.toString(), chestLocationText, 20, 20 * nombreSecondeAffichage, 20);
            joueur.playSound(joueur.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }
        for (Player joueur : this.groupe.getGame().getReferees()) {
            joueur.sendMessage(mineralcontest.prefixPrive + "Drop: " + this.nextDropLocation.getBlockX() + " Y: " + this.nextDropLocation.getBlockY() + " Z: " + this.nextDropLocation.getBlockZ());
        }
        MCAirDropSpawnEvent event = new MCAirDropSpawnEvent(this.nextDropLocation, this.groupe.getGame());
        Bukkit.getPluginManager().callEvent((Event)event);
        this.generateRandomLocation();
        this.generateTimeleftBeforeNextDrop();
    }

    public void handleDrops() {
        this.dropsHandler = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
            MCAirDropTickEvent event = new MCAirDropTickEvent(this.timeleft_before_next_drop, this.groupe.getGame());
            Bukkit.getPluginManager().callEvent((Event)event);
            if (this.groupe.getGame().isPreGame() || this.groupe.getGame().isGamePaused() || !this.groupe.getGame().isGameStarted()) {
                return;
            }
            if (this.timeleft_before_next_drop == Integer.MIN_VALUE) {
                this.generateTimeleftBeforeNextDrop();
                this.generateRandomLocation();
            }
            if (this.timeleft_before_next_drop > 0) {
                --this.timeleft_before_next_drop;
            } else {
                this.spawnNewParachute();
            }
        }, 0L, 20L);
    }

    public void stopDropsHandler() {
        if (this.dropsHandler != null) {
            this.dropsHandler.cancel();
        }
    }

    private void generateTimeleftBeforeNextDrop() {
        int minTime = this.groupe.getParametresPartie().getCVAR("min_time_between_drop").getValeurNumerique() * 60;
        int maxTime = this.groupe.getParametresPartie().getCVAR("max_time_between_drop").getValeurNumerique() * 60;
        Random random = new Random();
        this.timeleft_before_next_drop = random.nextInt(maxTime - minTime + 1) + minTime;
    }

    private void generateRandomLocation() {
        Location randomLocation = this.groupe.getGame().getArene().getCoffre().getLocation().clone();
        int tentatives = 1;
        int max = this.groupe.getParametresPartie().getCVAR("max_distance_from_arena").getValeurNumerique();
        int min = this.groupe.getParametresPartie().getCVAR("min_distance_from_arena").getValeurNumerique();
        Random random = new Random();
        Location centreArene = this.groupe.getGame().getArene().getCoffre().getLocation();
        while (Radius.isBlockInRadius(centreArene, randomLocation, min)) {
            int nbGenere = random.nextInt(max);
            randomLocation.setX(nbGenere % 2 == 0 ? randomLocation.getX() - (double)nbGenere : randomLocation.getX() + (double)nbGenere);
            if (Radius.isBlockInRadius(centreArene, randomLocation, min)) {
                nbGenere = random.nextInt(max - min - 1) + min;
                randomLocation.setZ(nbGenere % 2 == 0 ? centreArene.getZ() - (double)nbGenere : centreArene.getZ() + (double)nbGenere);
            } else {
                nbGenere = random.nextInt(max);
                randomLocation.setZ(nbGenere % 2 == 0 ? randomLocation.getZ() - (double)nbGenere : randomLocation.getZ() + (double)nbGenere);
            }
            if (++tentatives <= 50) continue;
            randomLocation = centreArene.clone();
            break;
        }
        randomLocation.setY(130.0);
        Location groundLocation = randomLocation.clone();
        while (groundLocation.getBlock().getType() == Material.AIR) {
            groundLocation.setY(groundLocation.getY() - 1.0);
        }
        randomLocation.setY(groundLocation.getY() + 100.0);
        this.nextDropLocation = randomLocation;
    }

    public List<Parachute> getParachutes() {
        return this.parachutes;
    }

    public int getTimeleft_before_next_drop() {
        return this.timeleft_before_next_drop;
    }

    public Location getNextDropLocation() {
        return this.nextDropLocation;
    }
}

