package fr.synchroneyes.mineral.Core.Parachute;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreParachute;
import fr.synchroneyes.mineral.Core.Parachute.ParachuteBlock;
import fr.synchroneyes.mineral.Core.Parachute.ParachuteManager;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Utils.LocationRange;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Parachute implements Listener {
    private Map<String, ParachuteBlock> blocksParachute;
    private double health;
    private boolean isParachuteOnGround = false;
    private boolean isParachuteBroken = false;
    private int normalFallingSpeed = 40;
    private int freeFallingSpeed = 2;
    private int max_items_in_chest = 100;
    private int min_items_in_chest = 35;
    private int currentFallingSpeed = this.normalFallingSpeed;
    private Location chestLocation = null;
    private String chestId;
    private AutomatedChestAnimation coffre;
    private ParachuteManager parachuteManager;
    private BukkitTask parachuteLoop;
    private Location fallingStartLocation;
    private ArmorStand armorStand;
    private Vector armorStandVelocity;

    public Parachute(double health, ParachuteManager manager) {
        this.health = health;
        this.blocksParachute = new LinkedHashMap<String, ParachuteBlock>();
        this.loadParachuteFromFile();
        this.parachuteManager = manager;
        this.coffre = new CoffreParachute(manager.getGroupe().getAutomatedChestManager());
        GameSettings parametres = manager.getGroupe().getParametresPartie();
        if (parametres != null) {
            this.normalFallingSpeed = manager.getGroupe().getParametresPartie().getCVAR("normal_falling_speed").getValeurNumerique();
            this.freeFallingSpeed = manager.getGroupe().getParametresPartie().getCVAR("free_falling_speed").getValeurNumerique();
            this.max_items_in_chest = manager.getGroupe().getParametresPartie().getCVAR("max_item_in_drop").getValeurNumerique();
            this.min_items_in_chest = manager.getGroupe().getParametresPartie().getCVAR("min_item_in_drop").getValeurNumerique();
            ((CoffreParachute)this.coffre).setMinItems(this.min_items_in_chest);
            ((CoffreParachute)this.coffre).setMaxItems(this.max_items_in_chest);
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    public boolean isParachuteHit(Projectile fleche) {
        if (this.isParachuteOnGround || this.isParachuteBroken) {
            return false;
        }
        return this.isParachuteHit(fleche.getLocation().getBlock().getLocation());
    }

    public boolean isParachuteHit(Location loc) {
        if (this.isParachuteOnGround || this.isParachuteBroken) {
            return false;
        }
        for (Map.Entry<String, ParachuteBlock> blockDeParachute : this.getParachute().entrySet()) {
            if (!LocationRange.isLocationBetween(loc, blockDeParachute.getValue().getLocation(), 2, 2)) continue;
            return true;
        }
        return false;
    }

    public void receiveDamage(Double damage, Location hitLocation) {
        if (this.health <= damage) {
            this.setFalling(true);
            return;
        }
        this.health -= damage.doubleValue();
        if (hitLocation != null) {
            World monde = hitLocation.getWorld();
            if (monde != null) {
                monde.playEffect(hitLocation, Effect.END_GATEWAY_SPAWN, 1);
            }
        } else {
            this.playEffectOnChest(Effect.END_GATEWAY_SPAWN);
        }
        this.playSoundOnChest(Sound.ENTITY_GENERIC_EXPLODE);
    }

    public Map<String, ParachuteBlock> getParachute() {
        return this.blocksParachute;
    }

    private void makeParachuteGoDown(boolean checkUnderBefore) {
        for (Map.Entry<String, ParachuteBlock> blocks : this.blocksParachute.entrySet()) {
            ParachuteBlock parachuteBlock = blocks.getValue();
            Block block = parachuteBlock.getLocation().getBlock();
            if (checkUnderBefore && block.getRelative(BlockFace.DOWN, 1).getType() != Material.AIR && !this.isThisBlockAParachute(block.getRelative(BlockFace.DOWN, 1))) {
                this.breakParachute();
                this.setFalling(true);
                return;
            }
            ParachuteBlock nouveauBlock = null;
            Location nouvelleLocation = block.getLocation();
            nouvelleLocation.setY(nouvelleLocation.getY() - 1.0);
            nouveauBlock = new ParachuteBlock(nouvelleLocation, block.getType());
            parachuteBlock.remove();
            this.blocksParachute.replace(blocks.getKey(), nouveauBlock);
            nouvelleLocation.getBlock().setType(parachuteBlock.getMaterial());
            if (nouveauBlock.getMaterial() != Material.CHEST) continue;
            this.chestLocation = nouvelleLocation;
        }
    }

    private boolean isParachuteHittingABlock() {
        for (Map.Entry<String, ParachuteBlock> blocks : this.blocksParachute.entrySet()) {
            if (blocks.getValue().getLocation().getBlock().getRelative(BlockFace.DOWN, 1).getType() == Material.AIR) continue;
            return true;
        }
        return false;
    }

    private boolean isThisBlockAParachute(Block b) {
        for (Map.Entry<String, ParachuteBlock> blocks : this.blocksParachute.entrySet()) {
            if (!blocks.getValue().getLocation().equals((Object)b.getLocation())) continue;
            return true;
        }
        return false;
    }

    private void setFalling(boolean falling) {
        if (falling) {
            this.currentFallingSpeed = this.freeFallingSpeed;
            this.breakParachute();
        }
    }

    private void breakParachute() {
        World currentWorld = null;
        Location dropLocation = null;
        BlockData chestData = null;
        for (Map.Entry<String, ParachuteBlock> block : this.blocksParachute.entrySet()) {
            if (block.getValue().getLocation().getBlock().getType() != Material.CHEST) {
                block.getValue().remove();
                continue;
            }
            currentWorld = block.getValue().getLocation().getWorld();
            dropLocation = block.getValue().getLocation();
            chestData = dropLocation.getBlock().getBlockData();
        }
        if (currentWorld != null) {
            dropLocation.getBlock().setType(Material.AIR);
            this.parachuteLoop.cancel();
            ArmorStand armorStand = (ArmorStand)currentWorld.spawn(dropLocation, ArmorStand.class);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
            armorStand.setVelocity(new Vector(0, -1, 0));
            armorStand.setInvulnerable(true);
            armorStand.setVisible(false);
            armorStand.setBasePlate(false);
            armorStand.setSmall(true);
            armorStand.setArms(false);
            this.fallingStartLocation = dropLocation;
            this.armorStand = armorStand;
            this.isParachuteBroken = true;
            this.parachuteLoop = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, this::doChestFallingTickWhenParachuteIsBroken, 0L, 1L);
        }
    }

    private ParachuteBlock getChest() {
        for (Map.Entry<String, ParachuteBlock> blocks : this.blocksParachute.entrySet()) {
            if (blocks.getValue().getMaterial() != Material.CHEST) continue;
            return blocks.getValue();
        }
        return null;
    }

    private void makeChestGoDown() {
        if (this.isParachuteBroken) {
            ParachuteBlock coffre = this.getChest();
            Block blockCoffre = coffre.getLocation().getBlock();
            Block blockEnDessous = blockCoffre.getRelative(BlockFace.DOWN, 1);
            if (blockEnDessous.getType() == Material.AIR) {
                Location positionActuelle = blockCoffre.getLocation();
                positionActuelle.getWorld().spawnParticle(Particle.REDSTONE, positionActuelle, 10, 0.0, 0.0, 0.0, 0.0, (Object)new Particle.DustOptions(Color.GREEN, 10.0f));
                Location nouvellePosition = blockEnDessous.getLocation();
                coffre.remove();
                nouvellePosition.getBlock().setType(coffre.getMaterial());
                this.blocksParachute.replace(this.chestId + "", new ParachuteBlock(nouvellePosition, coffre.getMaterial()));
            } else {
                this.isParachuteOnGround = true;
                this.coffre.setChestLocation(blockCoffre.getLocation());
                this.playEffectOnChest(Effect.END_GATEWAY_SPAWN);
                this.playSoundOnChest(Sound.ENTITY_GENERIC_EXPLODE);
            }
        }
    }

    private void loadParachuteFromFile() {
        File fichierParachute = new File(mineralcontest.plugin.getDataFolder(), FileList.AirDrop_model.toString());
        if (!fichierParachute.exists()) {
            Bukkit.getLogger().severe(mineralcontest.prefix + " Unable to load parachute file (" + fichierParachute.getAbsolutePath() + ")");
            return;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierParachute);
        for (String id : yamlConfiguration.getKeys(false)) {
            ConfigurationSection infos = yamlConfiguration.getConfigurationSection(id);
            if (infos == null) continue;
            Material blockMaterial = null;
            double posX = Double.parseDouble(infos.get("x").toString());
            double posY = Double.parseDouble(infos.get("y").toString());
            double posZ = Double.parseDouble(infos.get("z").toString());
            blockMaterial = Material.valueOf((String)infos.get("material").toString());
            Location blockLocation = new Location(null, posX, posY, posZ);
            if (blockMaterial == Material.CHEST) {
                this.chestLocation = blockLocation;
                this.chestId = id;
            }
            this.blocksParachute.put(id, new ParachuteBlock(blockLocation, blockMaterial));
        }
    }

    public void spawnParachute(Location spawnLocation) {
        for (Map.Entry<String, ParachuteBlock> block : this.blocksParachute.entrySet()) {
            ParachuteBlock parachuteBlock = block.getValue();
            Location blockLocation = parachuteBlock.getLocation();
            blockLocation.setWorld(spawnLocation.getWorld());
            blockLocation.setX(blockLocation.getX() + spawnLocation.getX());
            blockLocation.setY(blockLocation.getY() + spawnLocation.getY());
            blockLocation.setZ(blockLocation.getZ() + spawnLocation.getZ());
            blockLocation.getBlock().setType(parachuteBlock.getMaterial());
            this.blocksParachute.replace(block.getKey(), new ParachuteBlock(blockLocation, parachuteBlock.getMaterial()));
        }
        this.coffre.setChestLocation(spawnLocation);
        this.handleParachute();
    }

    private void handleParachute() {
        final AtomicInteger ticks = new AtomicInteger();
        if (this.parachuteLoop != null) {
            this.parachuteLoop.cancel();
        }
        this.parachuteLoop = new BukkitRunnable(){

            public void run() {
                if (Parachute.this.isParachuteOnGround) {
                    this.cancel();
                    Parachute.this.getChest().remove();
                    Parachute.this.coffre.spawn();
                    Parachute.this.parachuteManager.getGroupe().getAutomatedChestManager().replace(Parachute.this.coffre.getClass(), Parachute.this.coffre);
                    return;
                }
                int tickActuel = ticks.incrementAndGet();
                if (tickActuel % Parachute.this.currentFallingSpeed == 0) {
                    if (Parachute.this.isParachuteBroken) {
                        Parachute.this.makeChestGoDown();
                    } else {
                        Parachute.this.makeParachuteGoDown(true);
                    }
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 1L);
    }

    private void playEffectOnChest(Effect effect) {
        World monde;
        ParachuteBlock coffre = this.getChest();
        if (coffre != null && (monde = coffre.getLocation().getWorld()) != null) {
            monde.playEffect(coffre.getLocation(), effect, 1);
        }
    }

    private void playSoundOnChest(Sound effect) {
        World monde;
        ParachuteBlock coffre = this.getChest();
        if (coffre != null && (monde = coffre.getLocation().getWorld()) != null) {
            monde.playSound(coffre.getLocation(), effect, 1.0f, 0.0f);
        }
    }

    private void doChestFallingTickWhenParachuteIsBroken() {
        if (this.armorStandVelocity == null) {
            this.armorStandVelocity = this.armorStand.getVelocity();
            return;
        }
        if (this.armorStand == null) {
            Bukkit.getLogger().info("ArmorStand is null!");
            this.parachuteLoop.cancel();
            this.parachuteLoop = null;
            return;
        }
        if (this.armorStandVelocity.equals((Object)this.armorStand.getVelocity())) {
            this.parachuteLoop.cancel();
            this.parachuteLoop = null;
            Location feltLocation = this.armorStand.getLocation();
            while (feltLocation.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                int y = feltLocation.getBlockY();
                feltLocation.setY((double)(--y));
            }
            this.armorStand.remove();
            this.coffre.setChestLocation(feltLocation);
            this.coffre.spawn();
            this.parachuteManager.getGroupe().getAutomatedChestManager().addChest(this.coffre);
            return;
        }
        this.armorStandVelocity = this.armorStand.getVelocity();
        Location particleLocation = this.armorStand.getLocation().clone();
        particleLocation.setY(particleLocation.getY() + 5.0);
        this.armorStand.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, this.armorStand.getLocation(), 10, 0.0, 0.0, 0.0, 0.0);
    }
}

