package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCPlayerLeavePluginEvent;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnect implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) throws Exception {
        Player joueur = event.getPlayer();
        if (mineralcontest.plugin.getMCPlayer(joueur) != null) {
            MCPlayerLeavePluginEvent event1 = new MCPlayerLeavePluginEvent(event.getPlayer(), mineralcontest.plugin.getMCPlayer(event.getPlayer()));
            Bukkit.getPluginManager().callEvent((Event)event1);
            return;
        }
    }
}

