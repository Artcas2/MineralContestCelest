package fr.synchroneyes.mineral.Events;

import fr.synchroneyes.custom_events.MCPlayerLeaveWorldPluginEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLeavePluginWorld implements Listener {
    @EventHandler
    public void onPlayerLeavePluginWorld(MCPlayerLeaveWorldPluginEvent event) {
        event.getJoueur().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        MCPlayer mcPlayer = event.getMcPlayer();
        Location location = event.getJoueur().getLocation();
        if (mcPlayer.getGroupe() != null && mcPlayer.getGroupe().getMonde() != null) {
            location = mcPlayer.getPLayerLocationFromWorld(mcPlayer.getGroupe().getMonde());
            event.getMcPlayer().getGroupe().addDisconnectedPlayer(event.getJoueur(), location);
        }
        event.getMcPlayer().setInPlugin(false);
    }
}

