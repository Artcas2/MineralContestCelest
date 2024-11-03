package fr.synchroneyes.mineral.Core.Game.JoinTeam.Inventories;

import fr.synchroneyes.mineral.Core.Game.JoinTeam.Inventories.InventoryInterface;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.Items.JoinTeamItem;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Translation.Lang;
import java.util.List;
import org.bukkit.Material;

public class SelectionEquipeInventory extends InventoryInterface {
    private List<House> maisons;

    public SelectionEquipeInventory() {
        super(true, 27);
    }

    public SelectionEquipeInventory(List<House> maisons) {
        super(true, 27);
        this.maisons = maisons;
    }

    @Override
    public void setInventoryItems() {
        this.inventaire.setMaxStackSize(1);
        int positionItem = 8;
        int espaceAvantBloc = 1;
        int tailleBloc = 1;
        if (this.maisons != null) {
            int nombreEquipe = this.maisons.size();
            int resultatOperationNombreEquipe = nombreEquipe * (tailleBloc + espaceAvantBloc);
            int nombreItemsParLigne = 9;
            positionItem = (int)((double)positionItem + Math.ceil(nombreItemsParLigne / nombreEquipe));
            for (House maison : this.maisons) {
                JoinTeamItem item = new JoinTeamItem(maison.getTeam());
                this.inventaire.setItem(positionItem, item.toItemStack());
                this.items.add(item);
                positionItem += espaceAvantBloc + tailleBloc;
            }
        }
    }

    @Override
    public Material getItemMaterial() {
        return Material.BOOK;
    }

    @Override
    public String getNomInventaire() {
        return Lang.item_team_selection_title.toString();
    }

    @Override
    public String getDescriptionInventaire() {
        return Lang.item_team_selection_title.toString();
    }
}

