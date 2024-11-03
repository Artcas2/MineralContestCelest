package fr.synchroneyes.file_manager;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import org.bukkit.Bukkit;

public class RessourceFilesManager {
    public static void createDefaultFiles() {
        for (FileList fichier : FileList.values()) {
            File _fichier;
            File _dossier = RessourceFilesManager.pathToFile(fichier.getPath());
            if (!_dossier.exists()) {
                _dossier.mkdir();
            }
            if ((_fichier = new File(_dossier, fichier.getFileName())).exists()) continue;
            mineralcontest.plugin.saveResource(fichier.toString(), true);
            Bukkit.getLogger().info(mineralcontest.prefix + " Created " + fichier.toString());
        }
    }

    private static File pathToFile(String path) {
        File defaultPluginFolder = mineralcontest.plugin.getDataFolder();
        if (!defaultPluginFolder.exists()) {
            defaultPluginFolder.mkdir();
        }
        String[] dossiers = path.split("/");
        int folderLevel = 0;
        File _tmpFolder = null;
        for (String sous_dossier : dossiers) {
            if (sous_dossier.length() == 0) continue;
            _tmpFolder = folderLevel == 0 ? new File(defaultPluginFolder, sous_dossier) : new File(_tmpFolder, sous_dossier);
            if (!_tmpFolder.exists()) {
                _tmpFolder.mkdir();
            }
            ++folderLevel;
        }
        return _tmpFolder;
    }
}

