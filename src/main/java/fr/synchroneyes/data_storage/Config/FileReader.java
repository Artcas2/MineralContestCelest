package fr.synchroneyes.data_storage.Config;

import fr.synchroneyes.data_storage.SQLCredentials;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileReader {
    private YamlConfiguration yamlConfiguration;
    private File fichierConfiguration = new File(mineralcontest.plugin.getDataFolder(), FileList.MySQL_Config_File.toString());

    public FileReader() throws Exception {
        if (!this.fichierConfiguration.exists()) {
            throw new Exception("Unable to load " + FileList.MySQL_Config_File.toString());
        }
        this.yamlConfiguration = YamlConfiguration.loadConfiguration((File)this.fichierConfiguration);
    }

    public SQLCredentials getCredentials() {
        SQLCredentials infos_connexion = new SQLCredentials();
        infos_connexion.setHostname(Objects.requireNonNull(this.yamlConfiguration.get("host")).toString());
        infos_connexion.setPort(Objects.requireNonNull(this.yamlConfiguration.get("port")).toString());
        infos_connexion.setUsername(Objects.requireNonNull(this.yamlConfiguration.get("username")).toString());
        infos_connexion.setPassword(Objects.requireNonNull(this.yamlConfiguration.get("password")).toString());
        infos_connexion.setDatabase(Objects.requireNonNull(this.yamlConfiguration.get("database")).toString());
        return infos_connexion;
    }
}

