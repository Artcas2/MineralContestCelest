package fr.synchroneyes.custom_plugins;

import fr.synchroneyes.custom_plugins.CustomPlugin;
import java.util.HashMap;

public class CustomPluginManager {
    private HashMap<CustomPlugin, Boolean> enabled_plugins = new HashMap();

    public void registerPlugin(CustomPlugin plugin) {
        this.enabled_plugins.put(plugin, true);
    }
}

