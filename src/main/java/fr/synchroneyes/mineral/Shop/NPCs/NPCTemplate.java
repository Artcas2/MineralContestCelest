package fr.synchroneyes.mineral.Shop.NPCs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

public abstract class NPCTemplate {
    public static int id = 0;
    private Location emplacement;
    protected Inventory inventaire;
    private Entity entity;

    public NPCTemplate(int nombreDeLigne) {
        this.inventaire = Bukkit.createInventory(null, (int)(9 * nombreDeLigne), (String)this.getNomAffichage());
    }

    public abstract String getNomAffichage();

    public abstract Villager.Profession getNPCType();

    public abstract void onNPCRightClick(Player var1);

    public abstract void onNPCLeftClick(Player var1);

    public abstract void onInventoryItemClick(Event var1);

    public abstract Inventory getInventory();

    public void spawn() {
        if (this.entity != null) {
            this.entity.remove();
        }
        if (this.emplacement == null) {
            return;
        }
        if (this.emplacement.getWorld() == null) {
            return;
        }
        World monde = this.emplacement.getWorld();
        Villager entitySpawned = (Villager)monde.spawn(this.emplacement, Villager.class);
        entitySpawned.setAI(false);
        entitySpawned.setAdult();
        if (this.getNPCType() != null) {
            entitySpawned.setProfession(this.getNPCType());
        }
        entitySpawned.setInvulnerable(true);
        entitySpawned.setCustomNameVisible(true);
        entitySpawned.setCustomName(this.getNomAffichage());
        entitySpawned.setCollidable(false);
        entitySpawned.setRemoveWhenFarAway(false);
        entitySpawned.setAgeLock(true);
        entitySpawned.setSilent(true);
        this.entity = entitySpawned;
    }

    public Location getEmplacement() {
        return this.emplacement;
    }

    public void setEmplacement(Location emplacement) {
        this.emplacement = emplacement;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

