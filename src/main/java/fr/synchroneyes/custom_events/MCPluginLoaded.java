package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.custom_plugins.CustomPlugin;

public class MCPluginLoaded extends MCEvent {
    private CustomPlugin plugin;

    public MCPluginLoaded(CustomPlugin plugin) {
        this.plugin = plugin;
    }

    public CustomPlugin getPlugin() {
        return this.plugin;
    }
}

