package fr.synchroneyes.mineral.Utils.Player.HUD;

import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.ChatColor;

public abstract class PlayerHUD {
    private String PluginVersion;
    private ChatColor hudColor;
    private String HUDTitle;
    private MCPlayer player;

    public PlayerHUD(MCPlayer mcPlayer) {
        this.player = mcPlayer;
        this.HUDTitle = Lang.title.toString();
        this.PluginVersion = mineralcontest.plugin.getDescription().getVersion();
    }

    public abstract void update();

    public abstract void draw();

    public String getPluginVersion() {
        return this.PluginVersion;
    }

    public ChatColor getHudColor() {
        return this.hudColor;
    }

    public void setHudColor(ChatColor hudColor) {
        this.hudColor = hudColor;
    }

    public String getHUDTitle() {
        return this.HUDTitle;
    }

    public MCPlayer getPlayer() {
        return this.player;
    }
}

