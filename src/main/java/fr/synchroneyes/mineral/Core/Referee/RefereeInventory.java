package fr.synchroneyes.mineral.Core.Referee;

import fr.synchroneyes.mineral.Core.Referee.Inventory.GestionPartieInventory;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryViewerInventory;
import fr.synchroneyes.mineral.Core.Referee.Inventory.MapSelectorInventory;
import fr.synchroneyes.mineral.Core.Referee.Inventory.StopGameInventory;
import fr.synchroneyes.mineral.Core.Referee.Inventory.TeamChestInventory;
import fr.synchroneyes.mineral.Core.Referee.Inventory.TeleportMenuInventory;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.SetInvisibleItem;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RefereeInventory {
    private Inventory inventory;
    protected static LinkedList<InventoryTemplate> inventaires;
    protected static LinkedList<RefereeItemTemplate> items;
    public static RefereeInventory instance;

    private RefereeInventory() {
        if (inventaires == null) {
            inventaires = new LinkedList();
            items = new LinkedList();
            this.registerInventories();
            this.registerItems();
        }
        this.inventory = Bukkit.createInventory(null, (int)9, (String)"Menu Arbitrage");
        instance = this;
        this.fillInventory();
    }

    protected static LinkedList<RefereeItemTemplate> getItems() {
        return items;
    }

    private void registerItems() {
        items.add(new SetInvisibleItem(null, null));
    }

    private void registerInventories() {
        inventaires.add(new TeleportMenuInventory());
        inventaires.add(new InventoryViewerInventory());
        inventaires.add(new TeamChestInventory());
        inventaires.add(new MapSelectorInventory());
        inventaires.add(new GestionPartieInventory());
        inventaires.add(new StopGameInventory());
    }

    private void fillInventory() {
        this.inventory.clear();
        for (InventoryTemplate inventaire : inventaires) {
            this.inventory.addItem(new ItemStack[]{inventaire.toItemStack()});
        }
        for (RefereeItemTemplate item : items) {
            this.inventory.addItem(new ItemStack[]{item.toItemStack()});
        }
    }

    public static Inventory getInventory() {
        if (instance == null) {
            new RefereeInventory();
        }
        RefereeInventory refereeInventory = instance;
        refereeInventory.fillInventory();
        return refereeInventory.inventory;
    }
}

