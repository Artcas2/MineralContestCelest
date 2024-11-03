package fr.synchroneyes.mineral.Core.Referee.Inventory;

import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.AfficherScoreboardToAdminsItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.AfficherScoreboardToEveryoneItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.EnableDisableChickenItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.PauseGameItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.ResumeGameItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.SpawnChestItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.SpawnDropItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.StartChickenItem;
import fr.synchroneyes.mineral.Core.Referee.Items.GestionPartie.StartGameItem;
import fr.synchroneyes.mineral.Translation.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GestionPartieInventory extends InventoryTemplate {
    @Override
    public void setInventoryItems(Player arbitre) {
        this.registerItem(new StartGameItem(null, this));
        this.registerItem(new PauseGameItem(null, this));
        this.registerItem(new ResumeGameItem(null, this));
        this.registerItem(new SpawnChestItem(null, this));
        this.registerItem(new SpawnDropItem(null, this));
        this.registerItem(new StartChickenItem(null, this));
        this.registerItem(new EnableDisableChickenItem(null, this));
        this.registerItem(new AfficherScoreboardToAdminsItem(null, this));
        this.registerItem(new AfficherScoreboardToEveryoneItem(null, this));
    }

    @Override
    public Material getItemMaterial() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public String getNomInventaire() {
        return Lang.referee_inventory_game_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.referee_inventory_game_description.toString();
    }
}

