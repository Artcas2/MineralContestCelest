package fr.synchroneyes.mineral.Core.Coffre;

import fr.synchroneyes.custom_events.MCPlayerOpenChestEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class AutomatedChestAnimation {
    protected boolean isChestContentGenerated = false;
    protected Player openingPlayer = null;
    protected Player playerOpenedChest = null;
    protected Inventory inventaireCoffre;
    private Location chestLocation;
    protected boolean isAnimationOver = false;
    private int tailleInventaire;
    private BukkitTask tacheOuverture = null;
    private boolean isChestSpawned = false;
    private AutomatedChestManager manager;

    public AutomatedChestAnimation(int tailleInventaire, AutomatedChestManager manager) {
        this.tailleInventaire = tailleInventaire;
        this.inventaireCoffre = Bukkit.createInventory(null, (int)tailleInventaire, (String)this.getOpeningChestTitle());
        this.manager = manager;
    }

    public void setChestLocation(Location chestLocation) {
        Location loc;
        this.chestLocation = loc = new Location(chestLocation.getWorld(), (double)chestLocation.getBlockX(), (double)chestLocation.getBlockY(), (double)chestLocation.getBlockZ());
        this.updateManager();
    }

    public void remove() {
        this.chestLocation.getBlock().setType(Material.AIR);
        this.closeInventory();
    }

    public Player getOpeningPlayer() {
        return this.openingPlayer;
    }

    public Inventory getInventory() {
        return this.inventaireCoffre;
    }

    public boolean isBeingOpened() {
        return this.openingPlayer != null;
    }

    public boolean isAnimationOver() {
        return this.isAnimationOver;
    }

    public abstract int playNoteOnTick();

    public abstract int playNoteOnEnd();

    public abstract void actionToPerformBeforeSpawn();

    public abstract void actionToPerformAfterAnimationOver();

    public abstract boolean displayWaitingItems();

    public abstract String getOpeningChestTitle();

    public abstract String getOpenedChestTitle();

    public Location getLocation() {
        return this.chestLocation;
    }

    public abstract ItemStack getWaitingItemMaterial();

    public abstract ItemStack getUsedItemMaterial();

    public abstract LinkedList<Integer> getOpeningSequence();

    public abstract Material getChestMaterial();

    public abstract int getAnimationTime();

    public abstract boolean canChestBeOpenedByMultiplePlayers();

    public abstract List<ItemStack> genererContenuCoffre();

    public abstract boolean automaticallyGiveItemsToPlayer();

    public void performAnimation() {
        if (this.isAnimationOver) {
            return;
        }
        this.inventaireCoffre.clear();
        final int nombreItemSequence = this.getOpeningSequence().size();
        double tempsExecution = this.getAnimationTime();
        double tempsPauseEntreChaqueTour = tempsExecution * 1000.0 / (double)nombreItemSequence;
        double intervalTimer = tempsPauseEntreChaqueTour * 20.0 / 1000.0;
        if (this.displayWaitingItems()) {
            Iterator iterator = this.getOpeningSequence().iterator();
            while (iterator.hasNext()) {
                int slot = (Integer)iterator.next();
                this.inventaireCoffre.setItem(slot, this.getWaitingItemMaterial());
            }
        }
        final AtomicInteger indexSequence = new AtomicInteger(0);
        final AutomatedChestAnimation instance = this;
        this.tacheOuverture = new BukkitRunnable(){

            public void run() {
                if (AutomatedChestAnimation.this.openingPlayer == null) {
                    this.cancel();
                    return;
                }
                if (indexSequence.get() > nombreItemSequence - 1) {
                    if (AutomatedChestAnimation.this.playNoteOnEnd() > 0) {
                        AutomatedChestAnimation.this.openingPlayer.playNote(AutomatedChestAnimation.this.openingPlayer.getLocation(), Instrument.PIANO, new Note(AutomatedChestAnimation.this.playNoteOnEnd()));
                    }
                    AutomatedChestAnimation.this.inventaireCoffre.clear();
                    AutomatedChestAnimation.this.inventaireCoffre = Bukkit.createInventory(null, (int)AutomatedChestAnimation.this.tailleInventaire, (String)AutomatedChestAnimation.this.getOpenedChestTitle());
                    List<ItemStack> itemsGenere = AutomatedChestAnimation.this.genererContenuCoffre();
                    for (ItemStack item : itemsGenere) {
                        AutomatedChestAnimation.this.inventaireCoffre.addItem(new ItemStack[]{item});
                    }
                    AutomatedChestAnimation.this.isChestContentGenerated = true;
                    AutomatedChestAnimation.this.playerOpenedChest = AutomatedChestAnimation.this.openingPlayer;
                    AutomatedChestAnimation.this.isAnimationOver = true;
                    AutomatedChestAnimation.this.openInventoryToPlayer(AutomatedChestAnimation.this.openingPlayer);
                    AutomatedChestAnimation.this.actionToPerformAfterAnimationOver();
                    MCPlayerOpenChestEvent playerOpenChestEvent = new MCPlayerOpenChestEvent(instance, AutomatedChestAnimation.this.playerOpenedChest);
                    Bukkit.getPluginManager().callEvent((Event)playerOpenChestEvent);
                    AutomatedChestAnimation.this.closeInventory();
                    this.cancel();
                    return;
                }
                int slot = AutomatedChestAnimation.this.getOpeningSequence().get(indexSequence.get());
                AutomatedChestAnimation.this.inventaireCoffre.setItem(slot, AutomatedChestAnimation.this.getUsedItemMaterial());
                indexSequence.incrementAndGet();
                if (AutomatedChestAnimation.this.playNoteOnTick() > 0) {
                    AutomatedChestAnimation.this.openingPlayer.playNote(AutomatedChestAnimation.this.openingPlayer.getLocation(), Instrument.PIANO, new Note(AutomatedChestAnimation.this.playNoteOnTick()));
                }
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, (long)intervalTimer);
    }

    public void setOpeningPlayer(Player p) {
        this.openingPlayer = p;
        this.performAnimation();
        p.openInventory(this.inventaireCoffre);
    }

    public void closeInventory() {
        this.openingPlayer = null;
        if (this.tacheOuverture != null) {
            this.tacheOuverture.cancel();
        }
    }

    public void spawn() {
        this.actionToPerformBeforeSpawn();
        this.isChestSpawned = true;
        this.isAnimationOver = false;
        this.isChestContentGenerated = false;
        this.inventaireCoffre = Bukkit.createInventory(null, (int)this.tailleInventaire, (String)this.getOpeningChestTitle());
        this.getLocation().getBlock().setType(this.getChestMaterial());
    }

    public void openInventoryToPlayer(Player p) {
        if (this.isChestContentGenerated) {
            if (this.automaticallyGiveItemsToPlayer()) {
                for (ItemStack item : this.inventaireCoffre.getContents()) {
                    if (item == null) continue;
                    p.getInventory().addItem(new ItemStack[]{item});
                }
                this.inventaireCoffre.clear();
                this.getLocation().getBlock().setType(Material.AIR);
                this.openingPlayer.closeInventory();
            } else {
                p.openInventory(this.inventaireCoffre);
            }
        }
    }

    public boolean isChestSpawned() {
        return this.isChestSpawned;
    }

    public void updateManager() {
        this.manager.replace(this.getClass(), this);
    }

    public Player getPlayerOpenedChest() {
        return this.playerOpenedChest;
    }
}

