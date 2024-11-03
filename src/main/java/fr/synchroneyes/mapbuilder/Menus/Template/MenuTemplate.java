package fr.synchroneyes.mapbuilder.Menus.Template;

import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class MenuTemplate implements Listener {
    public MenuTemplate() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
    }
}

