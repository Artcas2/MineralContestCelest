package fr.synchroneyes.mineral.Utils;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DisconnectedPlayer {
    private UUID playerUUID;
    private Equipe oldPlayerTeam;
    private Groupe oldPlayerGroupe;
    private CouplePlayer oldPlayerDeathTime;
    private Location oldPlayerLocation;
    private List<ItemStack> oldPlayerInventory;
    private LinkedBlockingQueue<ShopItem> bonus;
    private KitAbstract kit;

    public DisconnectedPlayer(UUID playerUUID, Equipe oldPlayerTeam, Groupe oldPlayerGroupe, CouplePlayer oldPlayerDeathTime, Location oldPlayerLocation, Player p, LinkedBlockingQueue bonus, KitAbstract kit) {
        this.playerUUID = playerUUID;
        this.oldPlayerTeam = oldPlayerTeam;
        this.oldPlayerGroupe = oldPlayerGroupe;
        this.oldPlayerDeathTime = oldPlayerDeathTime;
        this.oldPlayerLocation = oldPlayerLocation;
        this.oldPlayerInventory = new LinkedList<ItemStack>();
        this.bonus = bonus;
        this.kit = kit;
        Bukkit.getLogger().info(oldPlayerLocation + "");
        for (ItemStack item : p.getInventory().getStorageContents()) {
            if (item == null) continue;
            this.oldPlayerInventory.add(item);
        }
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public Equipe getOldPlayerTeam() {
        return this.oldPlayerTeam;
    }

    public Groupe getOldPlayerGroupe() {
        return this.oldPlayerGroupe;
    }

    public CouplePlayer getOldPlayerDeathTime() {
        return this.oldPlayerDeathTime;
    }

    public Location getOldPlayerLocation() {
        return this.oldPlayerLocation;
    }

    public boolean wasPlayerDead() {
        return this.oldPlayerDeathTime != null && this.oldPlayerDeathTime.getValeur() > 0;
    }

    public List<ItemStack> getOldPlayerInventory() {
        return this.oldPlayerInventory;
    }

    public LinkedBlockingQueue<ShopItem> getBonus() {
        return this.bonus;
    }

    public KitAbstract getKit() {
        return this.kit;
    }
}

