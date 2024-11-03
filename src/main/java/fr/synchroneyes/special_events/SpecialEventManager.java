package fr.synchroneyes.special_events;

import fr.synchroneyes.special_events.SpecialEvent;
import fr.synchroneyes.special_events.halloween2024.Halloween2024Event;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;

public class SpecialEventManager {
    private List<SpecialEvent> eventList = new LinkedList<SpecialEvent>();

    public void init() {
        this.eventList.add(new Halloween2024Event());
        for (SpecialEvent event : this.eventList) {
            if (!event.isEventEnabled()) continue;
            Bukkit.getLogger().info("[MineralContest] Initialising event: " + event.getEventName());
            event.init();
        }
    }
}

