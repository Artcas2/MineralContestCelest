package fr.synchroneyes.mineral.Core.Referee.Items;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface RefereeItem {
    public void performClick(Player var1);

    public String getNomItem();

    public String getDescriptionItem();

    public Material getItemMaterial();
}

