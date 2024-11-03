package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCPlayerChangedWorldEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class Spigot_WorldChangeEvent implements Listener {
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        World monde_source = event.getFrom();
        World monde_destination = event.getPlayer().getWorld();
        Player joueur = event.getPlayer();
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        if (mcPlayer == null) {
            if (mineralcontest.isAMineralContestWorld(monde_destination) && !mineralcontest.isAMineralContestWorld(monde_source)) {
                MCPlayerJoinEvent joinEvent = new MCPlayerJoinEvent(joueur);
                Bukkit.getServer().getPluginManager().callEvent((Event)joinEvent);
                return;
            }
            return;
        }
        MCPlayerChangedWorldEvent mcPlayerChangedWorldEvent = new MCPlayerChangedWorldEvent(monde_source, monde_destination, mcPlayer);
        Bukkit.getServer().getPluginManager().callEvent((Event)mcPlayerChangedWorldEvent);
        if (mcPlayerChangedWorldEvent.isCancelled()) {
            event.getPlayer().teleport(mcPlayer.getPLayerLocationFromWorld(monde_source));
        }
    }
}

