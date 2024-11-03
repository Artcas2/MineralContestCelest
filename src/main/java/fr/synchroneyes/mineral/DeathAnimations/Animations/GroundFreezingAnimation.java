package fr.synchroneyes.mineral.DeathAnimations.Animations;

import fr.synchroneyes.mapbuilder.Blocks.SaveableBlock;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.Utils.Pair;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GroundFreezingAnimation extends DeathAnimation {
    private boolean sendNotification = false;
    private List<Pair<Location, SaveableBlock>> blocks_affected = new LinkedList<Pair<Location, SaveableBlock>>();

    @Override
    public String getAnimationName() {
        return "FreezingGround";
    }

    @Override
    public Material getIcone() {
        return Material.BLUE_ICE;
    }

    @Override
    public void playAnimation(LivingEntity player) {
        Player killer = player.getKiller();
        if (killer == null && !(player instanceof Player)) {
            return;
        }
        if (killer == null) {
            killer = (Player)player;
        }
        final Material MaterialToReplace = Material.FROSTED_ICE;
        if (this.sendNotification) {
            killer.sendTitle(ChatColor.BLUE + "\u2744 \u2744 \u2744", "Il fait froid par ici ...", 20, 100, 20);
        }
        int nb_second_animation = 10;
        final AtomicInteger duree_animation = new AtomicInteger(nb_second_animation * 4);
        this.blocks_affected.clear();
        final Player finalKiller = killer;
        new BukkitRunnable(){

            public void run() {
                if (duree_animation.get() == 0) {
                    this.cancel();
                    GroundFreezingAnimation.this.revertBlocks();
                    return;
                }
                int rayon = 3;
                for (int x = finalKiller.getLocation().getBlockX() - rayon; x < finalKiller.getLocation().getBlockX() + rayon; ++x) {
                    for (int z = finalKiller.getLocation().getBlockZ() - rayon; z < finalKiller.getLocation().getBlockZ() + rayon; ++z) {
                        Location loc = new Location(finalKiller.getWorld(), (double)x, (double)(finalKiller.getLocation().getBlockY() - 1), (double)z);
                        if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == MaterialToReplace || loc.getBlock().getRelative(BlockFace.DOWN).getType() == MaterialToReplace) continue;
                        GroundFreezingAnimation.this.blocks_affected.add(new Pair<Location, SaveableBlock>(loc, new SaveableBlock(loc.getBlock())));
                        loc.getBlock().setType(MaterialToReplace);
                    }
                }
                duree_animation.decrementAndGet();
            }
        }.runTaskTimer((Plugin)mineralcontest.plugin, 0L, 5L);
    }

    private void revertBlocks() {
        for (Pair<Location, SaveableBlock> pair : this.blocks_affected) {
            pair.getValue().setBlock();
        }
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
}

