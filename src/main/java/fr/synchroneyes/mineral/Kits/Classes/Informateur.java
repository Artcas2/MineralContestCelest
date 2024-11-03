package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCAirDropSpawnEvent;
import fr.synchroneyes.custom_events.MCAirDropTickEvent;
import fr.synchroneyes.custom_events.MCArenaChestTickEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreParachute;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class Informateur extends KitAbstract {
    private int timeLeftBeforeArenaWarn = 10;
    private int timeLeftBeforeDropWarn = 60;

    @Override
    public String getNom() {
        return Lang.kit_spy_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_spy_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.BOOK;
    }

    @EventHandler
    public void onArenaChestTick(MCArenaChestTickEvent event) {
        for (Player joueur : event.getGame().groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur) || event.getTimeLeft() != this.timeLeftBeforeArenaWarn) continue;
            joueur.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.arena_chest_will_spawn_in.toString(), event.getGame().groupe));
        }
    }

    @EventHandler
    public void onAirDropTick(MCAirDropTickEvent event) {
        for (Player joueur : event.getGame().groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur) || event.getTimeLeft() != this.timeLeftBeforeDropWarn) continue;
            joueur.sendMessage(mineralcontest.prefixPrive + Lang.kit_spy_airdrop_will_spawn.toString());
        }
    }

    @EventHandler
    public void onAirdropSpawn(MCAirDropSpawnEvent event) {
        String chestLocationText = Lang.airdrop_subtitle.toString();
        Location nextDropLocation = event.getParachuteLocation();
        chestLocationText = chestLocationText.replace("%x", nextDropLocation.getBlockX() + "");
        chestLocationText = chestLocationText.replace("%z", nextDropLocation.getBlockZ() + "");
        for (Player joueur : event.getGame().groupe.getPlayers()) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            joueur.sendMessage(mineralcontest.prefixPrive + chestLocationText);
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        AutomatedChestAnimation automatedChestAnimation;
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player joueur = (Player)event.getPlayer();
        if (!this.isPlayerUsingThisKit(joueur)) {
            return;
        }
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (playerGroup == null) {
            return;
        }
        Inventory inventaire = event.getInventory();
        if (playerGroup.getAutomatedChestManager().isThisAnAnimatedInventory(inventaire) && ((automatedChestAnimation = playerGroup.getAutomatedChestManager().getFromInventory(inventaire)) instanceof CoffreArene || automatedChestAnimation instanceof CoffreParachute)) {
            joueur.closeInventory();
            playerGroup.getAutomatedChestManager().getFromInventory(inventaire).closeInventory();
            event.setCancelled(true);
        }
    }
}

