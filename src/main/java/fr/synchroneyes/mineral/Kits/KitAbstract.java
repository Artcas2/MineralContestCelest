package fr.synchroneyes.mineral.Kits;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class KitAbstract implements Listener {
    public abstract String getNom();

    public abstract String getDescription();

    public KitAbstract() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }

    public abstract Material getRepresentationMaterialForSelectionMenu();

    public boolean isPlayerUsingThisKit(Player joueur) {
        Groupe groupe = mineralcontest.getPlayerGroupe(joueur);
        if (groupe == null) {
            return false;
        }
        if (groupe.getKitManager().getPlayerKit(joueur) == null) {
            return false;
        }
        return groupe.getKitManager().getPlayerKit(joueur).equals(groupe.getKitManager().getKitFromClass(this.getClass()));
    }
}

