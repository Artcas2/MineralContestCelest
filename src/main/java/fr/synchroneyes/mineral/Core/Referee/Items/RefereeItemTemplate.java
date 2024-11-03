package fr.synchroneyes.mineral.Core.Referee.Items;

import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItem;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class RefereeItemTemplate implements RefereeItem {
    protected Object target;
    protected InventoryTemplate inventaireSource;
    protected String customName = "";

    public RefereeItemTemplate(String customName, Object target, InventoryTemplate inventaireSource) {
        this.target = target;
        this.customName = customName;
        this.inventaireSource = inventaireSource;
    }

    public RefereeItemTemplate(Object target, InventoryTemplate inventaireSource) {
        this.target = target;
        this.inventaireSource = inventaireSource;
    }

    public boolean isFromCustomInventory() {
        return this.inventaireSource != null;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.getItemMaterial(), 1);
        ItemMeta itemMeta = item.getItemMeta();
        if (this.customName.length() == 0) {
            itemMeta.setDisplayName(this.getNomItem());
        } else {
            itemMeta.setDisplayName(this.customName);
        }
        ArrayList<String> description = new ArrayList<String>();
        description.add(this.getDescriptionItem());
        itemMeta.setLore(description);
        item.setItemMeta(itemMeta);
        return item;
    }
}

