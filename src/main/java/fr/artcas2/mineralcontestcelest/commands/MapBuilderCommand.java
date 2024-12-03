package fr.artcas2.mineralcontestcelest.commands;

import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardAPI;
import fr.synchroneyes.mineral.Utils.UrlFetcher.Urls;
import fr.synchroneyes.mineral.mineralcontest;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapBuilderCommand implements CommandExecutor {
    private boolean mapBuilderPluginEnabled = false;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (mapBuilderPluginEnabled) {
            mapBuilderPluginEnabled = false;

            if (mineralcontest.disableMapBuilderPlugin()) {
                Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
                    if (mineralcontest.plugin.pluginWorld.equals(player.getWorld())) {
                        ScoreboardAPI.createScoreboard(player, true);
                        return;
                    }

                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                });

                sender.sendMessage(ChatColor.GREEN + "Le plugin MapBuilder a été désactivé.");
                return true;
            }
        } else {
            mapBuilderPluginEnabled = true;

            if (mineralcontest.enableMapBuilderPlugin()) {
                sender.sendMessage(ChatColor.GREEN + "Le plugin MapBuilder a été activé.");
                sender.sendMessage("L'affichage de la commande côté client nécessite une reconnexion.");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Le plugin MapBuilder n'est pas installé sur le serveur !");
        sender.sendMessage(ChatColor.RED + "Installation automatique en cours (voir console)...");

        HttpGet request = new HttpGet(Urls.API_URL_MAP_BUILDER_VERSIONS);
        CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String entityContents = EntityUtils.toString(entity);
            JSONObject versions = new JSONObject(entityContents);
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

            String latestVersion = available_versions.get(0);
            String url = versions.getJSONObject(latestVersion).getString("file_url");
            String fileName = versions.getJSONObject(latestVersion).getString("file_name");
            String fileSize = versions.getJSONObject(latestVersion).getString("file_size");
            File downloadDirectory = new File("plugins");
            File downloadFile = new File(downloadDirectory, fileName);

            Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GOLD + " Downloading MapBuilder plugin v" + latestVersion);

            if (!downloadDirectory.exists()) {
                downloadDirectory.mkdir();
            }

            CloseableHttpClient httpClient2 = HttpClientBuilder.create().build();
            HttpGet request2 = new HttpGet(url);
            CloseableHttpResponse response2 = httpClient2.execute(request2);
            HttpEntity entity2 = response2.getEntity();

            try (InputStream inputStream = entity2.getContent();
                 FileOutputStream fileOutputStream = new FileOutputStream(downloadFile)) {
                int intFileSize = Integer.parseInt(fileSize);
                int tailleMo = 0x100000;
                double downloaded = 0;
                int inputStreamByte;
                while ((inputStreamByte = inputStream.read()) != -1) {
                    fileOutputStream.write(inputStreamByte);
                    if ((downloaded += 1.0) % (double)(tailleMo / 10) != 0) continue;
                    Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download progress: " + downloaded / (double) intFileSize * 100.0 + "%");
                }
            }

            Bukkit.getConsoleSender().sendMessage(mineralcontest.prefix + ChatColor.GREEN + " Download complete! Now loading plugin...");
            httpClient.close();
            httpClient2.close();

            Plugin mapBuilderPlugin = Bukkit.getPluginManager().loadPlugin(downloadFile);

            if (mapBuilderPlugin != null) {
                Bukkit.getPluginManager().enablePlugin(mapBuilderPlugin);
            }
        } catch (IOException | InvalidPluginException | InvalidDescriptionException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}

