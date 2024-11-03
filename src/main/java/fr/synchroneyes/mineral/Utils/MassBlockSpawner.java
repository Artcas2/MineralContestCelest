package fr.synchroneyes.mineral.Utils;

import fr.synchroneyes.custom_events.MCMassBlockSpawnEndedEvent;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class MassBlockSpawner {
    private int blockPerBatch = 100;
    private int delayPerBatch = 20;
    private BukkitTask timer;
    private List<Block> spawnedBlocks = new ArrayList<Block>();
    private HashMap<Location, Material> blocks = new HashMap();
    private HashMap<Location, Material> blockToAddLater = new HashMap();

    public void spawnBlocks() {
        ArrayList<Location> blocksToTreat = new ArrayList<Location>();
        blocksToTreat.addAll(this.blocks.keySet());
        blocksToTreat.addAll(this.blockToAddLater.keySet());
        AtomicInteger currentIndex = new AtomicInteger();
        this.timer = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, () -> {
            for (int i = currentIndex.get(); i < currentIndex.get() + this.blockPerBatch; ++i) {
                if (i >= blocksToTreat.size()) {
                    this.blockSpawnEnded();
                    return;
                }
                if (blocksToTreat.get(i) == null) {
                    this.blockSpawnEnded();
                    return;
                }
                Location location = (Location)blocksToTreat.get(i);
                Block block = location.getBlock();
                Material type = null;
                type = i < this.blocks.size() ? this.blocks.get(location) : this.blockToAddLater.get(location);
                block.setType(type);
                this.spawnedBlocks.add(block);
            }
            currentIndex.addAndGet(this.blockPerBatch);
            Bukkit.getLogger().info("[MassBlockSpawner] " + currentIndex + "/" + blocksToTreat.size());
        }, 0L, (long)this.delayPerBatch);
    }

    private void blockSpawnEnded() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        Bukkit.getPluginManager().callEvent((Event)new MCMassBlockSpawnEndedEvent(this));
    }

    public void removeSpawnedBlocks() {
        for (Block block : this.spawnedBlocks) {
            block.setType(Material.AIR);
        }
    }

    public void addBlock(Location location, Material material) {
        if (material == Material.LAVA) {
            this.blockToAddLater.put(location, material);
        } else {
            this.blocks.put(location, material);
        }
    }

    public void setBlockPerBatch(int blockPerBatch) {
        this.blockPerBatch = blockPerBatch;
    }

    public void setDelayPerBatch(int delayPerBatch) {
        this.delayPerBatch = delayPerBatch;
    }
}

