package fr.synchroneyes.special_events.halloween2024;

import fr.synchroneyes.mineral.mineralcontest;
import fr.synchroneyes.special_events.SpecialEvent;
import fr.synchroneyes.special_events.halloween2024.OnGameStart;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Halloween2024Event extends SpecialEvent {
    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new OnGameStart(), (Plugin)mineralcontest.plugin);
    }

    @Override
    public String getEventName() {
        return "Halloween";
    }

    @Override
    public boolean isEventEnabled() {
        LocalDateTime startDate = LocalDateTime.of(2024, 10, 30, 1, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 11, 10, 23, 53);
        LocalDateTime currentDate = LocalDateTime.now(ZoneId.systemDefault());
        return currentDate.isAfter(startDate) && currentDate.isBefore(endDate);
    }
}

