package fr.synchroneyes.custom_plugins;

import fr.synchroneyes.custom_events.MCWorldLoadedEvent;
import fr.synchroneyes.custom_plugins.PluginInterface;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomPlugin extends JavaPlugin implements PluginInterface,
Listener {
    private List<Listener> evenements = new LinkedList<Listener>();

    public void registerEvent(Listener listener) {
        this.evenements.add(listener);
    }

    public void unloadPlugin() {
        Bukkit.getLogger().info("Unloading plugin: " + this.getPluginName());
        HandlerList.unregisterAll((Plugin)this);
        Bukkit.getPluginManager().disablePlugin((Plugin)this);
        ClassLoader classLoader = this.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader)classLoader).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.gc();
    }

    @EventHandler
    public void onPluginLoaded(MCWorldLoadedEvent worldLoadedEvent) {
        boolean shoudDisablePlugin = true;
        if (this.shouldPluginRunOnDefinedMap()) {
            for (String map : this.getAllowedMaps()) {
                if (!map.equals(worldLoadedEvent.getWorld_name())) continue;
                shoudDisablePlugin = false;
                break;
            }
        } else {
            shoudDisablePlugin = false;
        }
        if (shoudDisablePlugin) {
            this.unloadPlugin();
            return;
        }
    }

    public void onEnable() {
        mineralcontest.plugin.registerNewPlugin(this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.onPluginEnabled();
    }

    public abstract void onPluginEnabled();
}

