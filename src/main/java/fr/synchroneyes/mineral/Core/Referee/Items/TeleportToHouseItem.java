package fr.synchroneyes.mineral.Core.Referee.Items;

import fr.synchroneyes.mineral.Core.Referee.Inventory.InventoryTemplate;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ChatColorString;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeleportToHouseItem extends RefereeItemTemplate {
    public TeleportToHouseItem(Object target, InventoryTemplate inventaireSource) {
        super(target, inventaireSource);
    }

    @Override
    public void performClick(Player joueur) {
        if (this.target instanceof Equipe) {
            Equipe equipe = (Equipe)this.target;
            try {
                PlayerUtils.teleportPlayer(joueur, joueur.getWorld(), equipe.getMaison().getHouseLocation());
                joueur.sendMessage("T\u00e9l\u00e9portation en cours ...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getNomItem() {
        return Lang.translate(Lang.referee_item_teleport_to_house_title.toString(), (Equipe)this.target);
    }

    @Override
    public String getDescriptionItem() {
        return Lang.translate(Lang.referee_item_teleport_to_house_description.toString(), (Equipe)this.target);
    }

    @Override
    public Material getItemMaterial() {
        Equipe equipe = (Equipe)this.target;
        if (equipe == null) {
            return Material.WHITE_WOOL;
        }
        try {
            return Material.valueOf((String)(ChatColorString.toStringEN(equipe.getCouleur()) + "_CONCRETE"));
        } catch (IllegalArgumentException iae) {
            return Material.WHITE_WOOL;
        }
    }
}

