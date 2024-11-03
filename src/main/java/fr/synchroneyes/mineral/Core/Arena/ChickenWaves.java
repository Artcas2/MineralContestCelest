package fr.synchroneyes.mineral.Core.Arena;

import fr.synchroneyes.mineral.Core.Arena.Arene;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ChickenWaves {
    private Location spawnCoffre;
    private Arene arene;
    private World monde;
    private boolean started = false;
    private boolean enabled = false;
    private BukkitTask loop;
    private LinkedList<LivingEntity> pouletsEnVie;
    private int nextWaveChickenCount = 0;
    private int tempsRestantAvantProchaineVague = 0;

    public ChickenWaves(Arene arene) {
        this.arene = arene;
        this.pouletsEnVie = new LinkedList();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!enabled) {
            for (LivingEntity poulet : this.pouletsEnVie) {
                poulet.remove();
            }
        }
        this.enabled = enabled;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void start() {
        if (this.started) {
            return;
        }
        if (this.arene.groupe.getGame().isGameEnded()) {
            return;
        }
        this.started = true;
        this.enabled = true;
        this.arene.groupe.sendToEveryone(ChatColor.GOLD + "----------------------");
        this.arene.groupe.sendToEveryone(mineralcontest.prefixGlobal + "Les vagues d'apparition de poulet dans l'ar\u00e8ne ont d\u00e9but\u00e9 !");
        this.arene.groupe.sendToEveryone(ChatColor.GOLD + "----------------------");
        this.genererProchaineVague();
        this.handleChickenWaveTimer();
    }

    public void stop() {
        if (this.loop != null) {
            this.loop.cancel();
        }
    }

    private void handleChickenWaveTimer() {
        this.loop = new BukkitRunnable(){

            public void run() {
                if (ChickenWaves.this.enabled) {
                    ChickenWaves.this.tempsRestantAvantProchaineVague--;
                    if (ChickenWaves.this.tempsRestantAvantProchaineVague <= 0) {
                        ChickenWaves.this.apparitionPoulets();
                        ChickenWaves.this.genererProchaineVague();
                        try {
                            ChickenWaves.this.tempsRestantAvantProchaineVague = ((ChickenWaves)ChickenWaves.this).arene.groupe.getParametresPartie().getCVAR("chicken_spawn_interval").getValeurNumerique();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 20L);
    }

    public void apparitionPoulets() {
        if (!this.enabled) {
            return;
        }
        if (this.arene.groupe.getGame().isGameEnded()) {
            return;
        }
        if (!this.arene.groupe.getGame().isGameStarted()) {
            this.stop();
            return;
        }
        this.monde = this.arene.groupe.getMonde();
        try {
            this.spawnCoffre = this.arene.getCoffre().getLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GameSettings parametres = this.arene.groupe.getParametresPartie();
        int chicken_spawn_min_count = 1;
        int chicken_spawn_max_count = 2;
        try {
            chicken_spawn_min_count = parametres.getCVAR("chicken_spawn_min_count").getValeurNumerique();
            chicken_spawn_max_count = parametres.getCVAR("chicken_spawn_max_count").getValeurNumerique();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isHalloweenEnabled = parametres.getCVAR("enable_halloween_event").getValeurNumerique() == 1;
        isHalloweenEnabled = false;
        Random random = new Random();
        int nombreDePouletASpawn = random.nextInt(chicken_spawn_max_count - chicken_spawn_min_count - 1) + chicken_spawn_min_count;
        for (int i = 0; i < nombreDePouletASpawn; ++i) {
            LivingEntity entity = null;
            if (!isHalloweenEnabled) {
                this.pouletsEnVie.add((LivingEntity)((Chicken)this.monde.spawnEntity(this.spawnCoffre, EntityType.CHICKEN)));
            } else {
                this.pouletsEnVie.add((LivingEntity)((Zombie)this.monde.spawnEntity(this.spawnCoffre, EntityType.ZOMBIE_VILLAGER)));
            }
            entity = this.pouletsEnVie.getLast();
            double currentSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
            entity.setCustomName(Lang.custom_chicken_name.toString());
            if (isHalloweenEnabled) {
                ((Zombie)entity).setAdult();
            }
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(currentSpeed * 1.5);
            entity.setCanPickupItems(false);
            entity.setCustomNameVisible(false);
        }
    }

    public void genererProchaineVague() {
        GameSettings parametres = this.arene.groupe.getParametresPartie();
        try {
            int maxPoulet = parametres.getCVAR("chicken_spawn_max_count").getValeurNumerique();
            int minPoulet = parametres.getCVAR("chicken_spawn_min_count").getValeurNumerique();
            Random random = new Random();
            this.nextWaveChickenCount = random.nextInt(maxPoulet - minPoulet + 1) + minPoulet;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFromChickenWave(LivingEntity e) {
        return this.pouletsEnVie.contains(e);
    }
}

