package fr.synchroneyes.custom_plugins;

public interface PluginInterface {
    public String getPluginName();

    public String getPluginDescription();

    public boolean shouldPluginRunOnDefinedMap();

    public String[] getAllowedMaps();
}

