package fr.synchroneyes.mineral.Utils.VersionChecker;

import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.UrlFetcher.Urls;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONArray;
import org.json.JSONObject;

public class Version {
    public static boolean isUpdating = false;
    public static boolean hasUpdated = false;
    public static boolean isCheckingStarted = false;

    public static void fetchAllMessages(List<String> listToFill) {
        listToFill.clear();
        String currentVersion = mineralcontest.plugin.getDescription().getVersion();
        HttpGet request = new HttpGet(Urls.API_URL_MESSAGES);
        CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String entityContents = EntityUtils.toString(entity);
            JSONObject messages = new JSONObject(entityContents);
            if (!messages.has(currentVersion)) {
                return;
            }
            JSONArray messagesArray = messages.getJSONArray(currentVersion);
            for (int i = 0; i < messagesArray.length(); ++i) {
                listToFill.add(Lang.translate(messagesArray.getString(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void doCheck() {
        String currentVersion = mineralcontest.plugin.getDescription().getVersion();
        HttpGet request = new HttpGet(Urls.API_URL_VERSIONS);
        CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String entityContents = EntityUtils.toString(entity);
            JSONObject files = new JSONObject(entityContents);
            JSONObject versions = files.getJSONObject("plugins");
            ArrayList<String> available_versions = new ArrayList<String>(versions.keySet());
            available_versions.sort((v1, v2) -> {
                String[] parts1 = v1.split("\\.");
                String[] parts2 = v2.split("\\.");
                int length = Math.max(parts1.length, parts2.length);
                for (int i = 0; i < length; ++i) {
                    int num2;
                    int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
                    int n = num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
                    if (num1 == num2) continue;
                    return Integer.compare(num2, num1);
                }
                return 0;
            });
            String latestVersion = (String)available_versions.get(0);
            if (Version.isCurrentVersionLast(latestVersion)) {
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Plugin is up-to-date! Current Version: " + currentVersion + " - Latest Version: " + latestVersion);
            } else {
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.RED + " A new update is available, plugin will now auto-update to version " + latestVersion);
                isUpdating = true;
                Version.DownloadNewVersion(versions.getJSONObject(latestVersion).getString("file_url"), versions.getJSONObject(latestVersion).getString("file_name"), versions.getJSONObject(latestVersion).getString("file_size"), latestVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Check(boolean theadedCheck) {
        if (MapBuilder.getInstance().isBuilderModeEnabled) {
            return;
        }
        if (!isCheckingStarted) {
            isCheckingStarted = true;
        }
        if (theadedCheck) {
            Thread thread = new Thread(Version::doCheck);
            thread.start();
        } else {
            Version.doCheck();
        }
    }

    private static boolean isCurrentVersionLast(String version) {
        String currentVersion = mineralcontest.plugin.getDescription().getVersion();
        return Version.compareVersions(currentVersion, version) >= 0;
    }

    private static int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; ++i) {
            int num2;
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int n = num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 == num2) continue;
            return Integer.compare(num1, num2);
        }
        return 0;
    }

    private static String toVersion(String chaine) {
        chaine = chaine.replace(",", "");
        return chaine.replace(".", "");
    }

    private static void DownloadNewVersion(String url, String fileName, String fileSize, String version) throws InterruptedException {
        Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + "" + ChatColor.GOLD + " Downloading version " + version);
        Bukkit.broadcastMessage((String)(mineralcontest.prefix + ChatColor.GOLD + " Downloading a new version of the plugin ..."));
        try {
            int inByte;
            File dossierTelechargement = new File("plugins");
            if (!dossierTelechargement.exists()) {
                dossierTelechargement.mkdir();
            }
            File fichierTelecharge = new File(dossierTelechargement, fileName);
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            FileOutputStream fos = new FileOutputStream(fichierTelecharge);
            int taille_mo = 0x100000;
            int taille_fichier = Integer.parseInt(fileSize);
            double downloaded = 0.0;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
                if ((downloaded += 1.0) % (double)(taille_mo / 10) != 0.0) continue;
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download progress: " + downloaded / (double)taille_fichier * 100.0 + "%");
            }
            is.close();
            fos.close();
            client.close();
            Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download complete! Now reloading ...");
            isUpdating = false;
            hasUpdated = true;
        } catch (FileNotFoundException fno) {
            fno.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] setStringToSameSize(String s1, String s2) {
        StringBuilder s1Builder = new StringBuilder(s1);
        StringBuilder s2Builder = new StringBuilder(s2);
        while (s1Builder.length() > s2.length()) {
            s1Builder.append("0");
        }
        s1 = s1Builder.toString();
        while (s2Builder.length() > s1.length()) {
            s2Builder.append("0");
        }
        s2 = s2Builder.toString();
        return new String[]{s1, s2};
    }
}

