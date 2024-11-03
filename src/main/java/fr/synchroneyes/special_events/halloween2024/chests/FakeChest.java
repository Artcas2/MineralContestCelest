package fr.synchroneyes.special_events.halloween2024.chests;

import fr.synchroneyes.mineral.Core.Coffre.Animations;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.special_events.halloween2024.utils.HalloweenTitle;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FakeChest extends AutomatedChestAnimation {
    private Game game;

    public FakeChest(int tailleInventaire, AutomatedChestManager manager) {
        super(tailleInventaire, manager);
        this.game = manager.getGroupe().getGame();
    }

    @Override
    public int playNoteOnTick() {
        return 0;
    }

    @Override
    public int playNoteOnEnd() {
        return 0;
    }

    @Override
    public void actionToPerformBeforeSpawn() {
        Location platformLocation = this.getLocation().clone();
        platformLocation.setY(platformLocation.getY() - 1.0);
        for (int x = (int)platformLocation.getX() - 5; x < (int)platformLocation.getX() + 5; ++x) {
            for (int z = (int)platformLocation.getZ() - 5; z < (int)platformLocation.getZ() + 5; ++z) {
                platformLocation.getWorld().getBlockAt(x, (int)platformLocation.getY(), z).setType(Material.PUMPKIN);
            }
        }
    }

    @Override
    public void actionToPerformAfterAnimationOver() {
        this.getOpeningPlayer().closeInventory();
        this.getLocation().getBlock().setType(Material.AIR);
        TNTPrimed tnt = (TNTPrimed)this.getLocation().getWorld().spawnEntity(this.getLocation(), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(40);
        for (Player joueur : this.game.groupe.getPlayers()) {
            PlayerInventory playerInventory = joueur.getInventory();
            for (ItemStack item : playerInventory.getContents()) {
                if (item == null || item.getType() != Material.ELYTRA && item.getType() != Material.FIREWORK_ROCKET) continue;
                playerInventory.remove(item);
            }
            ItemStack[] armor = new ItemStack[joueur.getInventory().getArmorContents().length];
            ItemStack[] items = joueur.getInventory().getArmorContents();
            for (int i = 0; i < items.length; ++i) {
                if (items[i] == null || items[i].getType() == Material.ELYTRA) continue;
                armor[i] = items[i];
            }
            joueur.getInventory().setArmorContents(armor);
            joueur.getInventory().remove(new ItemStack(Material.ELYTRA));
            joueur.getInventory().remove(new ItemStack(Material.FIREWORK_ROCKET));
            HalloweenTitle.sendTitle(joueur, "Le coffre \u00e9tait pi\u00e9g\u00e9!", "Maintenant... Attention \u00e0 la chute...", 1, 5, 1);
            joueur.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 300, 1));
        }
    }

    @Override
    public boolean displayWaitingItems() {
        return true;
    }

    @Override
    public String getOpeningChestTitle() {
        return "Coffre d'Halloween";
    }

    @Override
    public String getOpenedChestTitle() {
        return "Coffre d'Halloween";
    }

    @Override
    public ItemStack getWaitingItemMaterial() {
        ItemStack item = new ItemStack(Material.PUMPKIN);
        ItemMeta itemMeta = item.getItemMeta();
        assert (itemMeta != null);
        itemMeta.setDisplayName("Patientez... la surprise arrive");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public ItemStack getUsedItemMaterial() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = item.getItemMeta();
        assert (itemMeta != null);
        itemMeta.setDisplayName("Elle est presque l\u00e0...");
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public LinkedList<Integer> getOpeningSequence() {
        return Animations.SIX_LINES_PUMPKINS.toList();
    }

    @Override
    public Material getChestMaterial() {
        return Material.CHEST;
    }

    @Override
    public int getAnimationTime() {
        return 5;
    }

    @Override
    public boolean canChestBeOpenedByMultiplePlayers() {
        return false;
    }

    @Override
    public List<ItemStack> genererContenuCoffre() {
        return new LinkedList<ItemStack>();
    }

    @Override
    public boolean automaticallyGiveItemsToPlayer() {
        return false;
    }
}

