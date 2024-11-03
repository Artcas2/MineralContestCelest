package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.mineral.Exception.MaterialNotInRangeException;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.Utils.Range;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Parieur extends KitAbstract {
    private Material materialToReact = Material.DIAMOND_ORE;
    private double badLuckPercentage = 20.0;
    private double goodLuckPercentage = 20.0;
    private int badLuckMultiplier = 2;
    private Material badLuckMaterial = Material.DIRT;
    private int goodLuckMultiplier = 2;
    private Material goodLuckMaterial = Material.DIAMOND;

    @Override
    public String getNom() {
        return Lang.kit_crazy_bet_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_crazy_bet_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.EMERALD_ORE;
    }

    @EventHandler
    public void onBlockDestroyed(BlockBreakEvent event) throws MaterialNotInRangeException {
        if (!this.isPlayerUsingThisKit(event.getPlayer())) {
            return;
        }
        if (event.getBlock().getType() == this.materialToReact && event.isDropItems()) {
            Range[] range = new Range[]{new Range(this.goodLuckMaterial, 0, (int)this.goodLuckPercentage), new Range(Material.AIR, (int)this.goodLuckPercentage, (int)(100.0 - this.badLuckPercentage)), new Range(this.badLuckMaterial, (int)(100.0 - this.badLuckPercentage), 100)};
            Random random = new Random();
            int nombreAleatoire = random.nextInt(100);
            Material materialToDrop = Range.getInsideRange(range, nombreAleatoire);
            if (materialToDrop == Material.AIR) {
                return;
            }
            Block blockDestroyed = event.getBlock();
            blockDestroyed.getDrops().clear();
            event.setDropItems(false);
            Player joueur = event.getPlayer();
            if (materialToDrop == this.goodLuckMaterial) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(this.goodLuckMaterial, this.goodLuckMultiplier));
                PlayerUtils.setFirework(joueur, Color.GREEN, 0);
                PlayerUtils.setFirework(joueur, Color.RED, 1);
            } else {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(this.badLuckMaterial, this.badLuckMultiplier));
                joueur.playSound(joueur.getLocation(), Sound.ENTITY_PIG_HURT, 1.0f, 1.0f);
            }
        }
    }
}

