package fr.synchroneyes.mineral.Core.Coffre.Coffres;

import fr.synchroneyes.mineral.Core.Arena.ArenaChestContent.ArenaChestContentGenerator;
import fr.synchroneyes.mineral.Core.Coffre.Animations;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Translation.Lang;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CoffreParachute extends AutomatedChestAnimation {
    private ArenaChestContentGenerator generator = new ArenaChestContentGenerator(null);
    private AutomatedChestManager automatedChestManager;
    int minItems;
    int maxItems;

    public CoffreParachute(AutomatedChestManager manager) {
        super(45, manager);
        this.automatedChestManager = manager;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    @Override
    public int playNoteOnTick() {
        return 24;
    }

    @Override
    public int playNoteOnEnd() {
        return 24;
    }

    @Override
    public void actionToPerformBeforeSpawn() {
    }

    @Override
    public void actionToPerformAfterAnimationOver() {
    }

    @Override
    public boolean displayWaitingItems() {
        return true;
    }

    @Override
    public String getOpeningChestTitle() {
        return Lang.airdrop_chest_opening_title.toString();
    }

    @Override
    public String getOpenedChestTitle() {
        return Lang.airdrop_chest_opened_title.toString();
    }

    @Override
    public ItemStack getWaitingItemMaterial() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemStack getUsedItemMaterial() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public LinkedList<Integer> getOpeningSequence() {
        return Animations.FIVE_LINES_AROUND_THEN_CENTER.toList();
    }

    @Override
    public Material getChestMaterial() {
        return Material.CHEST;
    }

    @Override
    public int getAnimationTime() {
        return this.automatedChestManager.getGroupe().getParametresPartie().getCVAR("drop_opening_time").getValeurNumerique();
    }

    @Override
    public boolean canChestBeOpenedByMultiplePlayers() {
        return false;
    }

    @Override
    public List<ItemStack> genererContenuCoffre() {
        LinkedList<ItemStack> items = new LinkedList<ItemStack>();
        try {
            for (ItemStack item : this.generator.generateAirDropInventory(this.minItems, this.maxItems).getContents()) {
                if (item == null) continue;
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public boolean automaticallyGiveItemsToPlayer() {
        return false;
    }
}

