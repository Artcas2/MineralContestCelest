package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Core.MapVote;
import fr.synchroneyes.groups.Menus.MenuVote;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Arena.Coffre;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import fr.synchroneyes.mineral.Core.Coffre.Coffres.CoffreArene;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.Referee.Items.RefereeItemTemplate;
import fr.synchroneyes.mineral.Exception.EventAlreadyHandledException;
import fr.synchroneyes.mineral.Shop.Categories.Abstract.Category;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.Shop.NPCs.Event.NPCPlayerInteract;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ChestEvent implements Listener {
    @EventHandler
    public void onChestClose(InventoryCloseEvent event) throws Exception {
        World worldEvent = event.getPlayer().getWorld();
        if (mineralcontest.isAMineralContestWorld(worldEvent)) {
            try {
                this.AnimatedChestInventoryCloseEvent(event);
            } catch (EventAlreadyHandledException e) {
                return;
            }
            try {
                this.MapVoteMenuInventoryCloseEvent(event);
            } catch (EventAlreadyHandledException e) {
                return;
            }
            Game partie = mineralcontest.getWorldGame(worldEvent);
            if (partie != null && partie.isGameStarted() && !partie.isGamePaused() && !partie.isPreGame()) {
                if (!partie.groupe.getMonde().equals((Object)worldEvent)) {
                    Bukkit.getLogger().severe("onChestClose L40");
                    return;
                }
                AutomatedChestAnimation coffreArene = partie.getArene().getCoffre();
                Player player = (Player)event.getPlayer();
                if (event.getInventory().getHolder() instanceof Chest) {
                    Block openedInventoryBlock = ((Chest)event.getInventory().getHolder()).getBlock();
                    if (openedInventoryBlock.getLocation().equals((Object)coffreArene.getLocation())) {
                        coffreArene.closeInventory();
                        player.closeInventory();
                        return;
                    }
                    if (partie.isReferee(player)) {
                        Inventory inventaireFerme = event.getInventory();
                        for (House maison : partie.getHouses()) {
                            Block blockCoffreMaison = maison.getCoffreEquipeLocation().getBlock();
                            if (!(blockCoffreMaison.getState() instanceof Chest)) {
                                return;
                            }
                            Chest coffre = (Chest)blockCoffreMaison.getState();
                            if (!inventaireFerme.equals((Object)coffre.getInventory())) continue;
                            maison.getTeam().updateScore(player);
                            return;
                        }
                    }
                    House playerHouse = partie.getPlayerHouse(player);
                    Coffre teamChest = playerHouse.getCoffre();
                    if (openedInventoryBlock.getLocation().equals((Object)teamChest.getPosition())) {
                        try {
                            playerHouse.getTeam().updateScore(player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChestBreaked(ItemSpawnEvent event) throws Exception {
        Game partie;
        World world = event.getEntity().getWorld();
        if (mineralcontest.isAMineralContestWorld(world) && (partie = mineralcontest.getWorldGame(world)) != null && partie.isGameStarted()) {
            if (!partie.groupe.getMonde().equals((Object)world)) {
                return;
            }
            AutomatedChestAnimation arenaChest = partie.getArene().getCoffre();
            if (arenaChest != null && event.getEntity().getItemStack().getType().equals((Object)Material.CHEST) && Radius.isBlockInRadius(arenaChest.getLocation(), event.getEntity().getLocation(), 2)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) throws Exception {
        try {
            this.AnimatedChestOpenInventoryEvent(event);
        } catch (EventAlreadyHandledException e) {
            return;
        }
        World world = event.getPlayer().getWorld();
        Game game = mineralcontest.getWorldGame(world);
        if (game == null) {
            return;
        }
        if (mineralcontest.isAMineralContestWorld(world)) {
            if (game.groupe.getMonde() != null && !game.groupe.getMonde().equals((Object)world)) {
                Bukkit.getLogger().severe("InventoryOpenEvent L141");
                return;
            }
            if (event.getInventory().getHolder() instanceof Chest || event.getInventory().getHolder() instanceof StorageMinecart) {
                Chest openedChest;
                Block openedChestBlock;
                if (event.getInventory().getHolder() instanceof Chest && !game.isThisBlockAGameChest(openedChestBlock = (openedChest = (Chest)event.getInventory().getHolder()).getBlock())) {
                    openedChest.getInventory().clear();
                    return;
                }
                if (event.getInventory().getHolder() instanceof StorageMinecart) {
                    StorageMinecart storageMinecart = (StorageMinecart)event.getInventory().getHolder();
                    storageMinecart.getInventory().clear();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) throws Exception {
        if (event.getWhoClicked() instanceof Player) {
            try {
                this.AnimatedChestInventoryClickEvent(event);
                this.ShopInventoryOnItemClick(event);
                NPCPlayerInteract.OnInventoryClickEvent(event);
            } catch (EventAlreadyHandledException e) {
                return;
            }
            try {
                this.MapVoteClickOnItem(event);
            } catch (EventAlreadyHandledException e) {
                event.setCancelled(true);
                return;
            }
            Player joueur = (Player)event.getWhoClicked();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (playerGroup.getGame() == null) {
                return;
            }
            Game partie = playerGroup.getGame();
            Inventory clickedInventory = event.getInventory();
            if (partie.isGameStarted()) {
                Chest arenaChest;
                ItemStack clickedItem;
                Inventory inventory = event.getInventory();
                if (inventory.getHolder() instanceof Chest && (clickedItem = event.getCurrentItem()) != null && ShopManager.isAnShopItem(clickedItem)) {
                    event.setCancelled(true);
                    return;
                }
                if (partie.getArene().getCoffre().getLocation().getBlock().getState() instanceof Chest && (arenaChest = (Chest)partie.getArene().getCoffre().getLocation().getBlock().getState()).getInventory().equals((Object)clickedInventory)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onStatItemClick(InventoryClickEvent event) {
        Player joueur;
        if (event.getWhoClicked() instanceof Player && mineralcontest.isInAMineralContestWorld(joueur = (Player)event.getWhoClicked())) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (playerGroup.getGame() == null) {
                return;
            }
            if (event.getView().getTitle().equals(Lang.stats_menu_title.getDefault())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void AnimatedChestOpenInventoryEvent(InventoryOpenEvent event) throws EventAlreadyHandledException {
        if (!(event.getPlayer() instanceof Player)) return;
        Player joueur = (Player)event.getPlayer();
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (playerGroup == null) {
            return;
        }
        if (!mineralcontest.isInAMineralContestWorld(joueur)) return;
        Location chestLocation = event.getInventory().getLocation();
        if (chestLocation == null) {
            return;
        }
        Block chest = chestLocation.getBlock();
        if (playerGroup.getGame().isGameStarted() && playerGroup.getGame().getPlayerTeam(joueur) != null && playerGroup.getGame().getPlayerTeam(joueur).getMaison().getCoffre().getPosition().equals((Object)chestLocation) && event.getInventory().getViewers().size() > 1) {
            event.setCancelled(true);
            joueur.closeInventory();
        }
        if (!playerGroup.getAutomatedChestManager().isThisBlockAChestAnimation(chest)) return;
        event.setCancelled(true);
        AutomatedChestAnimation automatedChestAnimation = playerGroup.getAutomatedChestManager().getChestAnomation(chest);
        if (automatedChestAnimation == null) {
            return;
        }
        if (automatedChestAnimation.isBeingOpened()) {
            return;
        }
        if (automatedChestAnimation.getClass().equals(CoffreArene.class)) {
            if (!playerGroup.getGame().getArene().isChestSpawned()) return;
            automatedChestAnimation.setOpeningPlayer(joueur);
            throw new EventAlreadyHandledException();
        } else {
            automatedChestAnimation.setOpeningPlayer(joueur);
        }
        throw new EventAlreadyHandledException();
    }

    public void AnimatedChestInventoryCloseEvent(InventoryCloseEvent event) throws EventAlreadyHandledException {
        if (event.getPlayer() instanceof Player) {
            Player joueur = (Player)event.getPlayer();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (mineralcontest.isInAMineralContestWorld(joueur)) {
                Inventory openedInventory = event.getInventory();
                if (playerGroup.getAutomatedChestManager().isThisAnAnimatedInventory(openedInventory)) {
                    AutomatedChestAnimation automatedChestAnimation = playerGroup.getAutomatedChestManager().getFromInventory(openedInventory);
                    if (automatedChestAnimation == null) {
                        return;
                    }
                    automatedChestAnimation.closeInventory();
                    throw new EventAlreadyHandledException();
                }
            }
        }
    }

    public void AnimatedChestInventoryClickEvent(InventoryClickEvent event) throws EventAlreadyHandledException {
        if (event.getWhoClicked() instanceof Player) {
            Player joueur = (Player)event.getWhoClicked();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (mineralcontest.isInAMineralContestWorld(joueur)) {
                Inventory openedInventory = event.getInventory();
                if (playerGroup.getAutomatedChestManager().isThisAnAnimatedInventory(openedInventory)) {
                    AutomatedChestAnimation automatedChestAnimation = playerGroup.getAutomatedChestManager().getFromInventory(openedInventory);
                    if (automatedChestAnimation == null) {
                        return;
                    }
                    if (!automatedChestAnimation.isAnimationOver()) {
                        event.setCancelled(true);
                        throw new EventAlreadyHandledException();
                    }
                }
            }
        }
    }

    public void MapVoteClickOnItem(InventoryClickEvent event) throws EventAlreadyHandledException {
        if (event.getWhoClicked() instanceof Player) {
            Player joueur = (Player)event.getWhoClicked();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            Inventory inventaire = event.getInventory();
            if (playerGroup.getEtatPartie() != Etats.VOTE_EN_COURS) {
                return;
            }
            if (event.getCurrentItem() == null) {
                return;
            }
            if (inventaire.equals((Object)playerGroup.getMapVote().getMenuVote().getInventory())) {
                MenuVote menuVote = playerGroup.getMapVote().getMenuVote();
                for (RefereeItemTemplate items : menuVote.getItems()) {
                    if (!items.toItemStack().equals((Object)event.getCurrentItem())) continue;
                    items.performClick(joueur);
                    joueur.closeInventory();
                    event.setCancelled(true);
                    throw new EventAlreadyHandledException();
                }
            }
        }
    }

    public void MapVoteMenuInventoryCloseEvent(InventoryCloseEvent event) throws EventAlreadyHandledException {
        if (event.getPlayer() instanceof Player) {
            Player joueur = (Player)event.getPlayer();
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null) {
                return;
            }
            if (playerGroup.getEtatPartie() != Etats.VOTE_EN_COURS) {
                return;
            }
            MapVote mapVote = playerGroup.getMapVote();
            if (event.getInventory().equals((Object)mapVote.getMenuVote().getInventory()) && !mapVote.havePlayerVoted(joueur)) {
                Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> joueur.openInventory(event.getInventory()), 1L);
                throw new EventAlreadyHandledException();
            }
        }
    }

    public void ShopInventoryOnItemClick(InventoryClickEvent event) throws EventAlreadyHandledException {
        Player joueur;
        if (event.getWhoClicked() instanceof Player && mineralcontest.isInAMineralContestWorld(joueur = (Player)event.getWhoClicked())) {
            Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
            if (playerGroup == null || playerGroup.getGame() == null) {
                return;
            }
            ShopManager shopManager = playerGroup.getGame().getShopManager();
            Inventory currentInventory = event.getInventory();
            for (BonusSeller vendeur : shopManager.getListe_pnj()) {
                if (vendeur.getInventory().equals((Object)currentInventory)) {
                    for (Category category : vendeur.getCategories_dispo()) {
                        if (!category.toItemStack().equals((Object)event.getCurrentItem())) continue;
                        category.openMenuToPlayer(joueur);
                        event.setCancelled(true);
                        throw new EventAlreadyHandledException();
                    }
                }
                for (Category category : vendeur.getCategories_dispo()) {
                    if (!category.getInventory().equals((Object)currentInventory)) continue;
                    category.onCategoryItemClick(event);
                    event.setCancelled(true);
                    throw new EventAlreadyHandledException();
                }
            }
        }
    }
}

