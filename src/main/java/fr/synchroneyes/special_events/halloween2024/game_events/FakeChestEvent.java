package fr.synchroneyes.special_events.halloween2024.game_events;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.special_events.halloween2024.chests.FakeChest;
import fr.synchroneyes.special_events.halloween2024.game_events.HalloweenEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FakeChestEvent extends HalloweenEvent {
    private Location chestLocation;
    private FakeChest fakeChest;

    public FakeChestEvent(Game partie) {
        super(partie);
    }

    private Location generateRandomLocation(int minDistance, int maxDistance) {
        Location arenaCenter = this.getPartie().getArene().getCoffre().getLocation();
        int x = (int)(Math.random() * (double)maxDistance) + minDistance;
        int z = (int)(Math.random() * (double)maxDistance) + minDistance;
        return new Location(arenaCenter.getWorld(), arenaCenter.getX() + (double)x, 222.0, arenaCenter.getZ() + (double)z);
    }

    @Override
    public String getEventName() {
        return "FakeChest";
    }

    @Override
    public void executionContent() {
        this.chestLocation = this.generateRandomLocation(150, 300);
        this.fakeChest = new FakeChest(54, this.getPartie().groupe.getAutomatedChestManager());
        this.fakeChest.setChestLocation(this.chestLocation);
        this.fakeChest.spawn();
    }

    @Override
    public void beforeExecute() {
        for (Player joueur : this.getPartie().groupe.getPlayers()) {
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ELYTRA)});
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.FIREWORK_ROCKET, 32)});
        }
    }

    @Override
    public void afterExecute() {
        for (Player joueur : this.getPartie().groupe.getPlayers()) {
            joueur.sendMessage(mineralcontest.prefixPrive + "Vous avez re\u00e7u une cape d'Halloween, utilisez la pour vous rendre au coffre d'Halloween");
            joueur.sendMessage(mineralcontest.prefixPrive + "Le coffre se situe en X:" + this.chestLocation.getBlockX() + ", Y:" + this.chestLocation.getBlockY() + ", Z:" + this.chestLocation.getBlockZ());
            joueur.sendMessage(mineralcontest.prefixPrive + "Une belle r\u00e9compense vous attend...");
        }
    }

    @Override
    public String getEventTitle() {
        return "Coffre d'Halloween";
    }

    @Override
    public String getEventDescription() {
        return "Un coffre surprise vous attend... Afin de vous y rendre, utilisez votre nouvelle cape...";
    }

    @Override
    public boolean isTextMessageNotificationEnabled() {
        return false;
    }

    @Override
    public boolean isNotificationDelayed() {
        return false;
    }
}

