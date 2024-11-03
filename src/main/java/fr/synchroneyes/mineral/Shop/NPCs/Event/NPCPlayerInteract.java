package fr.synchroneyes.mineral.Shop.NPCs.Event;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Exception.EventAlreadyHandledException;
import fr.synchroneyes.mineral.Shop.NPCs.NPCTemplate;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCPlayerInteract {
    public static void OnPlayerRightClick(PlayerInteractAtEntityEvent entityEvent) throws EventAlreadyHandledException {
        Player joueur = entityEvent.getPlayer();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            Game partie = playerGroup.getGame();
            ShopManager shopManager = partie.getShopManager();
            if (entityEvent.getRightClicked() instanceof Villager) {
                Villager clickedEntity = (Villager)entityEvent.getRightClicked();
                for (NPCTemplate nPCTemplate : shopManager.getListe_pnj()) {
                    if (!nPCTemplate.getEmplacement().equals((Object)clickedEntity.getLocation())) continue;
                    if (playerGroup.getParametresPartie().getCVAR("enable_shop").getValeurNumerique() != 1) {
                        entityEvent.setCancelled(true);
                        throw new EventAlreadyHandledException();
                    }
                    entityEvent.setCancelled(true);
                    joueur.closeInventory();
                    nPCTemplate.onNPCRightClick(joueur);
                    throw new EventAlreadyHandledException();
                }
            }
        }
    }

    public static void OnInventoryClickEvent(InventoryClickEvent event) throws EventAlreadyHandledException {
        Player joueur = (Player)event.getWhoClicked();
        if (mineralcontest.isInAMineralContestWorld(joueur)) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            ShopManager shopManager = playerGroup.getGame().getShopManager();
            for (NPCTemplate nPCTemplate : shopManager.getListe_pnj()) {
                if (!event.getInventory().equals((Object)nPCTemplate.getInventory())) continue;
                event.setCancelled(true);
                throw new EventAlreadyHandledException();
            }
        }
    }
}

