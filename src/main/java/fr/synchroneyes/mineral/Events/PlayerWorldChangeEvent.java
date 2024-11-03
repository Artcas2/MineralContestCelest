package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCPlayerChangedWorldEvent;
import fr.synchroneyes.custom_events.MCPlayerJoinEvent;
import fr.synchroneyes.custom_events.MCPlayerLeaveWorldPluginEvent;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerWorldChangeEvent implements Listener {
    @EventHandler
    public void onPlayerWorldChangeEvent(MCPlayerChangedWorldEvent event) {
        if (event.getTo().getEnvironment() != World.Environment.NORMAL) {
            if (event.getTo().getEnvironment() == World.Environment.NETHER) {
                // empty if block
            }
            event.setCancelled(true);
            event.getJoueur().sendPrivateMessage(mineralcontest.prefixErreur + "L'acc\u00e8s \u00e0 ce monde n'est pas autoris\u00e9. Vous devez rester dans le monde normal et non dans le monde: " + event.getTo().getEnvironment().name());
            return;
        }
        if (!mineralcontest.isAMineralContestWorld(event.getTo())) {
            MCPlayerLeaveWorldPluginEvent event1 = new MCPlayerLeaveWorldPluginEvent(event.getJoueur().getJoueur());
            Bukkit.getServer().getPluginManager().callEvent((Event)event1);
            return;
        }
        if (mineralcontest.isAMineralContestWorld(event.getFrom())) {
            return;
        }
        MCPlayerJoinEvent event1 = new MCPlayerJoinEvent(event.getJoueur().getJoueur());
        Bukkit.getPluginManager().callEvent((Event)event1);
    }

    private void convertToNether(Location l) {
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        l.setX(x /= 8.0);
        l.setZ(z /= 8.0);
    }
}

