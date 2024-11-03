package fr.synchroneyes.mineral.Core;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.DisconnectedPlayer;
import fr.synchroneyes.mineral.Utils.Player.HUD.BaseHUD;
import fr.synchroneyes.mineral.Utils.Player.HUD.PlayerHUD;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class MCPlayer {
    private Groupe groupe;
    private Game partie;
    private Equipe equipe;
    private House maison;
    private Player joueur;
    private int databasePlayerId = 0;
    private int score_brought = 0;
    private int score_lost = 0;
    private HashMap<World, Location> player_world_locations;
    private PlayerHUD hud;
    private boolean isInPlugin = true;
    private KitAbstract kit;

    public MCPlayer(Player joueur) {
        this.joueur = joueur;
        this.player_world_locations = new HashMap();
        this.hud = new BaseHUD(this);
    }

    public void setVisible() {
        if (this.groupe != null) {
            for (Player joueur : this.groupe.getPlayers()) {
                if (joueur.getUniqueId().equals(this.joueur.getUniqueId())) continue;
                this.joueur.showPlayer((Plugin)mineralcontest.plugin, joueur);
                joueur.showPlayer((Plugin)mineralcontest.plugin, this.joueur);
            }
        }
    }

    public void setInvisible() {
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            if (this.groupe != null) {
                for (Player joueur : this.groupe.getPlayers()) {
                    if (joueur.getUniqueId().equals(this.joueur.getUniqueId())) continue;
                    this.joueur.hidePlayer((Plugin)mineralcontest.plugin, joueur);
                    joueur.hidePlayer((Plugin)mineralcontest.plugin, this.joueur);
                }
            }
        }, 20L);
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
        if (groupe != null) {
            this.partie = groupe.getGame();
        }
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        this.maison = equipe.getMaison();
    }

    public void setMaison(House house) {
        this.maison = house;
        this.equipe = house.getTeam();
    }

    public void cancelDeathEvent() {
        this.setMaxHealth();
        this.setMaxFood();
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            this.joueur.openInventory((Inventory)this.joueur.getInventory());
            Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> this.joueur.closeInventory(), 1L);
        }, 1L);
    }

    public void setMaxHealth() {
        double maxPlayerHealth = this.joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null ? this.joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : 20.0;
        this.joueur.setHealth(maxPlayerHealth);
    }

    public void setMaxFood() {
        int maxHungerLevel = 20;
        this.joueur.setFoodLevel(maxHungerLevel);
    }

    public void clearPlayerPotionEffects() {
        for (PotionEffect potion : this.joueur.getActivePotionEffects()) {
            this.joueur.removePotionEffect(potion.getType());
        }
    }

    public void clearInventory() {
        this.joueur.getInventory().clear();
    }

    public void giveBaseItems() {
        if (this.groupe != null) {
            this.groupe.getPlayerBaseItem().giveItemsToPlayer(this.joueur);
        }
    }

    public void teleportToHouse() {
        if (this.maison != null) {
            this.joueur.teleport(this.maison.getHouseLocation());
        }
    }

    public void playFireworks(Color couleur) {
        Firework firework = (Firework)this.joueur.getWorld().spawn(this.joueur.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(couleur).withFade(Color.WHITE).build());
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void sendPrivateMessage(String message) {
        this.joueur.sendMessage(mineralcontest.prefixPrive + message);
    }

    public void addPlayerScore(int score) {
        this.score_brought += score;
    }

    public void addPlayerScorePenalityToOtherTeams(int score) {
        this.score_lost += score;
    }

    public void resetPlayerScores() {
        this.score_lost = 0;
        this.score_brought = 0;
    }

    public void setPlayerWorldLocation(World w, Location l) {
        if (!this.joueur.isOnGround()) {
            return;
        }
        if (this.player_world_locations.containsKey(w)) {
            this.player_world_locations.replace(w, l);
        } else {
            this.player_world_locations.put(w, l);
        }
    }

    public Location getPLayerLocationFromWorld(World w) {
        return this.player_world_locations.getOrDefault(w, null);
    }

    public boolean isInPlugin() {
        return this.isInPlugin;
    }

    public void setInPlugin(boolean inPlugin) {
        this.isInPlugin = inPlugin;
    }

    public void disconnectPlayer() {
        if (this.getGroupe() != null) {
            Groupe groupe = this.getGroupe();
            groupe.addDisconnectedPlayer(this.joueur, this.joueur.getLocation());
            if (groupe.getGame() != null) {
                groupe.getGame().removePlayerReady(this.joueur);
                groupe.getGame().removeReferee(this.joueur, false);
            }
            groupe.removeAdmin(this.joueur);
            groupe.removePlayer(this.joueur);
            if (this.getEquipe() != null) {
                this.getEquipe().removePlayer(this.joueur);
            }
            if (this.getPartie().isGameStarted()) {
                this.getMaison().getPorte().forceCloseDoor();
            }
        }
        mineralcontest.plugin.removePlayer(this.joueur);
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + this.joueur.getDisplayName() + " s'est d\u00e9connect\u00e9");
    }

    public void reconnectPlayer(DisconnectedPlayer disconnectedPlayer) {
        if (disconnectedPlayer.getOldPlayerGroupe() != null) {
            Player joueur = Bukkit.getPlayer((UUID)disconnectedPlayer.getPlayerUUID());
            disconnectedPlayer.getOldPlayerGroupe().playerHaveReconnected(joueur);
        }
    }

    public boolean isInventoryFull() {
        int inventorySize = this.joueur.getInventory().getSize();
        int item_count = 0;
        for (ItemStack item : this.joueur.getInventory().getContents()) {
            if (item == null) continue;
            ++item_count;
        }
        return item_count == inventorySize;
    }

    public Groupe getGroupe() {
        return this.groupe;
    }

    public Game getPartie() {
        return this.partie;
    }

    public void setPartie(Game partie) {
        this.partie = partie;
    }

    public Equipe getEquipe() {
        return this.equipe;
    }

    public House getMaison() {
        return this.maison;
    }

    public Player getJoueur() {
        return this.joueur;
    }

    public int getDatabasePlayerId() {
        return this.databasePlayerId;
    }

    public void setDatabasePlayerId(int databasePlayerId) {
        this.databasePlayerId = databasePlayerId;
    }

    public int getScore_brought() {
        return this.score_brought;
    }

    public void setScore_brought(int score_brought) {
        this.score_brought = score_brought;
    }

    public int getScore_lost() {
        return this.score_lost;
    }

    public void setScore_lost(int score_lost) {
        this.score_lost = score_lost;
    }

    public KitAbstract getKit() {
        return this.kit;
    }

    public void setKit(KitAbstract kit) {
        this.kit = kit;
    }
}

