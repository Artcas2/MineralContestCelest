package fr.synchroneyes.mineral.Kits;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPreGameStartEvent;
import fr.synchroneyes.custom_events.PlayerKitSelectedEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Kits.Classes.Agile;
import fr.synchroneyes.mineral.Kits.Classes.CowBoy;
import fr.synchroneyes.mineral.Kits.Classes.Enchanteur;
import fr.synchroneyes.mineral.Kits.Classes.Guerrier;
import fr.synchroneyes.mineral.Kits.Classes.Informateur;
import fr.synchroneyes.mineral.Kits.Classes.Mineur;
import fr.synchroneyes.mineral.Kits.Classes.Parieur;
import fr.synchroneyes.mineral.Kits.Classes.Robuste;
import fr.synchroneyes.mineral.Kits.Classes.Soutien;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.TextUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class KitManager implements Listener {
    private List<KitAbstract> kitsDisponible = new ArrayList<KitAbstract>();
    private Map<Player, KitAbstract> kits_joueurs = new HashMap<Player, KitAbstract>();
    private boolean kitsEnabled = false;
    private boolean kitSelectionOver = false;
    private Groupe groupe;
    private Game partie;
    private BukkitTask boucleGestionKits;
    private Inventory kitSelection = null;

    public KitManager(Groupe groupe) {
        this.groupe = groupe;
        this.partie = groupe.getGame();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
        this.kitsDisponible.add(new Agile());
        this.kitsDisponible.add(new CowBoy());
        this.kitsDisponible.add(new Enchanteur());
        this.kitsDisponible.add(new Guerrier());
        this.kitsDisponible.add(new Informateur());
        this.kitsDisponible.add(new Mineur());
        this.kitsDisponible.add(new Parieur());
        this.kitsDisponible.add(new Robuste());
        this.kitsDisponible.add(new Soutien());
        this.kitsEnabled = groupe.getParametresPartie().getCVAR("enable_kits").getValeurNumerique() == 1;
    }

    public void removeAllPlayersKit() {
        this.kits_joueurs.clear();
    }

    public void setPlayerKit(Player joueur, KitAbstract kit) {
        if (this.kits_joueurs.containsKey(joueur)) {
            this.kits_joueurs.replace(joueur, kit);
        } else {
            this.kits_joueurs.put(joueur, kit);
        }
        PlayerKitSelectedEvent event = new PlayerKitSelectedEvent(joueur, kit);
        Bukkit.getPluginManager().callEvent((Event)event);
        Bukkit.getLogger().info(joueur.getDisplayName() + " -> " + kit.getNom());
        Equipe playerTeam = this.groupe.getPlayerTeam(joueur);
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        if (mcPlayer != null) {
            mcPlayer.setKit(kit);
        }
        if (playerTeam != null && !this.groupe.getGame().isGameStarted()) {
            this.groupe.sendToEveryone(mineralcontest.prefixGlobal + Lang.translate(Lang.kitmanager_player_selected_kit.toString(), joueur));
        }
        if (playerTeam != null && !this.groupe.getGame().isGameStarted()) {
            playerTeam.sendMessage(mineralcontest.prefixTeamChat + Lang.translate(Lang.kitmanager_player_selected_kit_team.toString(), joueur).replace("%k", kit.getNom()));
        }
        String separateur = ChatColor.GOLD + "----------";
        StringBuilder liste_pseudo_sans_team = new StringBuilder();
        List<Player> liste_joueur_sans_kits = this.getPlayerWithoutKits(false);
        if (!liste_joueur_sans_kits.isEmpty()) {
            for (Player joueur_sans_kit : liste_joueur_sans_kits) {
                liste_pseudo_sans_team.append(joueur_sans_kit.getDisplayName()).append(", ");
            }
            String liste_joueur = liste_pseudo_sans_team.toString();
            liste_joueur = liste_joueur.substring(0, liste_joueur.length() - 2);
            this.groupe.sendToEveryone(separateur);
            this.groupe.sendToEveryone(mineralcontest.prefixGlobal + Lang.kitmanager_player_list_without_kits.toString() + liste_joueur);
            this.groupe.sendToEveryone(separateur);
        }
        if (this.doesAllPlayerHaveAKit(false)) {
            try {
                if (!this.groupe.getGame().isGameStarted()) {
                    this.groupe.getGame().demarrerPartie(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public KitAbstract getPlayerKit(Player joueur) {
        if (!this.kits_joueurs.containsKey(joueur)) {
            return null;
        }
        return this.kits_joueurs.get(joueur);
    }

    private void kitLoop() {
        for (Player joueur : this.groupe.getPlayers()) {
            KitAbstract kit_joueur;
            if (!this.kits_joueurs.containsKey(joueur) || !((kit_joueur = this.kits_joueurs.get(joueur)) instanceof Soutien)) continue;
            ((Soutien)kit_joueur).healAroundPlayer(joueur);
        }
    }

    public void startKitLoop(int delay) {
        if (this.boucleGestionKits == null) {
            this.boucleGestionKits = Bukkit.getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, this::kitLoop, 0L, (long)delay);
        }
    }

    public KitAbstract getKitFromClass(Class classe) {
        for (KitAbstract kit : this.kitsDisponible) {
            if (!kit.getClass().equals(classe)) continue;
            return kit;
        }
        return null;
    }

    public KitAbstract getKitFromString(String nomClasse) {
        for (KitAbstract kit : this.kitsDisponible) {
            if (!kit.getNom().contains(nomClasse)) continue;
            return kit;
        }
        return null;
    }

    public Inventory getKitSelectionInventory() {
        if (this.kitSelection == null) {
            this.kitSelection = Bukkit.createInventory(null, (int)9, (String)Lang.kitmanager_inventory_kitSelectionTitle.toString());
            this.kitSelection.setMaxStackSize(1);
            for (KitAbstract kit : this.kitsDisponible) {
                ItemStack itemKit = new ItemStack(kit.getRepresentationMaterialForSelectionMenu());
                ItemMeta kitMeta = itemKit.getItemMeta();
                kitMeta.setDisplayName(kit.getNom());
                kitMeta.setLore(TextUtils.textToLore(kit.getDescription(), 50));
                itemKit.setItemMeta(kitMeta);
                this.kitSelection.addItem(new ItemStack[]{itemKit});
            }
        }
        return this.kitSelection;
    }

    public void openInventoryToPlayer(Player joueur) {
        if (!this.isKitsEnabled()) {
            return;
        }
        joueur.openInventory(this.getKitSelectionInventory());
    }

    @EventHandler
    public void onKitSelectionMenuClosed(InventoryCloseEvent event) {
        Player joueur;
        if (this.kitSelectionOver) {
            return;
        }
        Inventory menu = event.getInventory();
        if (event.getPlayer() instanceof Player && mineralcontest.isInAMineralContestWorld(joueur = (Player)event.getPlayer()) && menu.equals((Object)this.getKitSelectionInventory()) && !this.kits_joueurs.containsKey(joueur)) {
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> joueur.openInventory(this.getKitSelectionInventory()), 1L);
        }
    }

    @EventHandler
    public void onKitSelected(InventoryClickEvent event) {
        if (this.kitSelectionOver) {
            return;
        }
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        if (event.getWhoClicked() instanceof Player) {
            Player joueur = (Player)event.getWhoClicked();
            if (inventory.equals((Object)this.getKitSelectionInventory())) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getItemMeta() == null) {
                    event.setCancelled(true);
                    return;
                }
                KitAbstract selectedKit = this.getKitFromString(clickedItem.getItemMeta().getDisplayName());
                if (selectedKit == null) {
                    event.setCancelled(true);
                    return;
                }
                this.setPlayerKit(joueur, selectedKit);
                event.setCancelled(true);
                joueur.closeInventory();
            }
        }
    }

    public void openMenuToEveryone(boolean openToReferee) {
        if (!this.isKitsEnabled()) {
            return;
        }
        for (Player joueur : this.groupe.getPlayers()) {
            if (this.groupe.getGame().isReferee(joueur)) {
                if (!openToReferee) continue;
                joueur.openInventory(this.getKitSelectionInventory());
                continue;
            }
            joueur.openInventory(this.getKitSelectionInventory());
        }
    }

    public boolean doesAllPlayerHaveAKit(boolean includeReferee) {
        for (Player joueur : this.groupe.getPlayers()) {
            if (!(this.groupe.getGame().isReferee(joueur) ? includeReferee && !this.kits_joueurs.containsKey(joueur) : !this.kits_joueurs.containsKey(joueur))) continue;
            return false;
        }
        return true;
    }

    public List<Player> getPlayerWithoutKits(boolean includeReferee) {
        ArrayList<Player> joueurs_sans_kits = new ArrayList<Player>();
        for (Player joueur : this.groupe.getPlayers()) {
            if (this.groupe.getGame().isReferee(joueur)) {
                if (!includeReferee || this.kits_joueurs.containsKey(joueur)) continue;
                joueurs_sans_kits.add(joueur);
                continue;
            }
            if (this.kits_joueurs.containsKey(joueur)) continue;
            joueurs_sans_kits.add(joueur);
        }
        return joueurs_sans_kits;
    }

    @EventHandler
    private void onGameStart(MCPreGameStartEvent event) {
        if (event.getPartie().groupe.getParametresPartie().getCVAR("enable_kits").getValeurNumerique() != 1) {
            return;
        }
        List<Player> joueurs_sans_kit = this.getPlayerWithoutKits(false);
        if (joueurs_sans_kit.isEmpty()) {
            return;
        }
        event.setCancelled(true);
        for (Player joueur : joueurs_sans_kit) {
            this.openInventoryToPlayer(joueur);
        }
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        this.startKitLoop(20);
    }

    public boolean isKitsEnabled() {
        return this.kitsEnabled;
    }

    public void setKitsEnabled(boolean kitsEnabled) {
        this.kitsEnabled = kitsEnabled;
    }

    public boolean isKitSelectionOver() {
        return this.kitSelectionOver;
    }

    public void setKitSelectionOver(boolean kitSelectionOver) {
        this.kitSelectionOver = kitSelectionOver;
    }
}

