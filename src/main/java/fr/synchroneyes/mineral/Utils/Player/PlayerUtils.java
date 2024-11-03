package fr.synchroneyes.mineral.Utils.Player;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Arena.Zones.DeathZone;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Core.Referee.Referee;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayer;
import fr.synchroneyes.mineral.Utils.Radius;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.LinkedList;
import java.util.ListIterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class PlayerUtils {
    public static void setFirework(Player joueur, Color couleur) {
        Firework firework = (Firework)joueur.getWorld().spawn(joueur.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(couleur).withFade(Color.WHITE).build());
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }

    public static void setFirework(Location position, Color couleur, int puissance) {
        Firework firework = (Firework)position.getWorld().spawn(position, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(couleur).withFade(couleur).build());
        fireworkMeta.setPower(puissance);
        firework.setFireworkMeta(fireworkMeta);
    }

    public static void setFirework(Player joueur, Color couleur, int puissance) {
        Firework firework = (Firework)joueur.getWorld().spawn(joueur.getLocation(), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(couleur).withFade(Color.WHITE).build());
        fireworkMeta.setPower(puissance);
        firework.setFireworkMeta(fireworkMeta);
    }

    public static void applyPVPtoPlayer(Player joueur) {
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (playerGroup == null) {
            joueur.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0);
            return;
        }
        if (playerGroup.getParametresPartie().getCVAR("mp_enable_old_pvp").getValeurNumerique() == 1) {
            joueur.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024.0);
        } else {
            joueur.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
        }
    }

    public static void teleportPlayer(Player p, double x, double y, double z) {
        World world = PlayerUtils.getPluginWorld();
        Location loc = new Location(world, x, y, z);
        p.teleport(loc);
    }

    public static void teleportPlayer(Player p, World w, Location loc) {
        Location new_loc = new Location(w, loc.getX(), loc.getY(), loc.getZ());
        p.teleport(new_loc);
    }

    public static World getPluginWorld() {
        World world;
        String world_name = mineralcontest.getPluginConfigValue("world_name").toString();
        mineralcontest.plugin.pluginWorld = world = Bukkit.getWorld((String)world_name);
        if (world != null) {
            mineralcontest.plugin.defaultSpawn = world.getSpawnLocation();
        }
        return world;
    }

    public static void runScoreboardManager() {
        Bukkit.getServer().getScheduler().runTaskTimer((Plugin)mineralcontest.plugin, PlayerUtils::scoreboardManager, 0L, 20L);
    }

    private static void scoreboardManager() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!mineralcontest.isInAMineralContestWorld(onlinePlayer)) continue;
            Game playerGame = mineralcontest.getPlayerGame(onlinePlayer);
            if (playerGame == null || playerGame.groupe.getEtatPartie().equals((Object)Etats.EN_ATTENTE)) {
                onlinePlayer.setPlayerListName(onlinePlayer.getDisplayName());
                continue;
            }
            Equipe playerTeam = mineralcontest.getPlayerGame(onlinePlayer).getPlayerTeam(onlinePlayer);
            StringBuilder playerPrefix = new StringBuilder();
            Game game = mineralcontest.getPlayerGame(onlinePlayer);
            if (mineralcontest.communityVersion) {
                playerPrefix.append(ChatColor.GOLD + "<" + playerGame.groupe.getNom() + "> " + ChatColor.RESET);
            }
            if (game.getArene().getDeathZone().isPlayerDead(onlinePlayer)) {
                playerPrefix.append(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "");
            } else {
                if (game.isReferee(onlinePlayer)) {
                    playerPrefix.append(ChatColor.GOLD + "[\u2606] " + ChatColor.WHITE);
                }
                if (playerTeam != null) {
                    playerPrefix.append(playerTeam.getCouleur() + "\u2588\u2588 " + ChatColor.WHITE);
                }
            }
            onlinePlayer.setPlayerListName(playerPrefix.toString() + onlinePlayer.getDisplayName());
        }
    }

    public static boolean isPlayerInDeathZone(Player joueur) {
        for (CouplePlayer infoJoueur : mineralcontest.getPlayerGame(joueur).getArene().getDeathZone().getPlayers()) {
            if (!infoJoueur.getJoueur().equals((Object)joueur)) continue;
            return true;
        }
        return false;
    }

    public static boolean isPlayerInHisBase(Player p) throws Exception {
        Game game = mineralcontest.getPlayerGame(p);
        if (game == null) {
            return false;
        }
        House playerHouse = game.getPlayerHouse(p);
        int house_radius = 7;
        if (playerHouse == null) {
            return false;
        }
        return Radius.isBlockInRadius(playerHouse.getHouseLocation(), p.getLocation(), house_radius);
    }

    public static int getPlayerItemsCountInInventory(Player p) {
        int item_count = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || !item.getType().equals((Object)Material.AIR)) continue;
            ++item_count;
        }
        return item_count;
    }

    public static void clearPlayer(Player joueur, boolean clearInventory) {
        if (clearInventory) {
            joueur.getInventory().clear();
        }
        for (PotionEffect potion : joueur.getActivePotionEffects()) {
            joueur.removePotionEffect(potion.getType());
        }
        joueur.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        joueur.setWalkSpeed(0.2f);
        joueur.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
        joueur.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
    }

    public static void equipReferee(Player joueur) {
        if (mineralcontest.getPlayerGame(joueur).isReferee(joueur)) {
            joueur.getInventory().setItemInMainHand(Referee.getRefereeItem());
            joueur.setGameMode(GameMode.CREATIVE);
        }
    }

    public static boolean isPlayerHidden(Player p) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.canSee(p)) continue;
            return true;
        }
        return false;
    }

    public static void killPlayer(Player player) {
        GameSettings gameSettings;
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
        if (mcPlayer != null && (gameSettings = mcPlayer.getGroupe().getParametresPartie()).getCVAR("drop_chest_on_death").getValeurNumerique() == 1 && !Radius.isBlockInRadius(player.getLocation(), mcPlayer.getGroupe().getGame().getArene().getCoffre().getLocation(), gameSettings.getCVAR("protected_zone_area_radius").getValeurNumerique())) {
            try {
                mineralcontest.getPlayerGame(player).getArene().getDeathZone().add(player);
                GameLogger.addLog(new Log("PlayerUtils_Dead", "Player " + player.getDisplayName() + " died", "death"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        LinkedList<ItemStack> inventaire = new LinkedList<ItemStack>();
        player.setLevel(0);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            inventaire.add(item);
        }
        ListIterator iterateur = inventaire.listIterator();
        while (iterateur.hasNext()) {
            ItemStack item = (ItemStack)iterateur.next();
            LinkedList<Material> item_a_drop = new LinkedList<Material>();
            item_a_drop.add(Material.IRON_INGOT);
            item_a_drop.add(Material.GOLD_INGOT);
            item_a_drop.add(Material.DIAMOND);
            item_a_drop.add(Material.EMERALD);
            item_a_drop.add(Material.IRON_ORE);
            item_a_drop.add(Material.GOLD_ORE);
            item_a_drop.add(Material.DIAMOND_ORE);
            item_a_drop.add(Material.EMERALD_ORE);
            item_a_drop.add(Material.IRON_ORE);
            item_a_drop.add(Material.GOLD_ORE);
            item_a_drop.add(Material.EMERALD_ORE);
            item_a_drop.add(Material.DIAMOND_ORE);
            item_a_drop.add(Material.POTION);
            item_a_drop.add(Material.REDSTONE);
            item_a_drop.add(Material.ENCHANTED_BOOK);
            Groupe playerGroup = mineralcontest.getPlayerGroupe(player);
            GameSettings settings = playerGroup.getParametresPartie();
            int mp_enable_item_drop = 0;
            try {
                mp_enable_item_drop = settings.getCVAR("mp_enable_item_drop").getValeurNumerique();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mp_enable_item_drop == 1 && !item_a_drop.contains(item.getType())) {
                iterateur.remove();
            }
            if (mp_enable_item_drop != 0) continue;
            iterateur.remove();
        }
        for (ItemStack item : inventaire) {
            if (item.getType().equals((Object)Material.AIR)) continue;
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        try {
            mineralcontest.getPlayerGame(player).getArene().getDeathZone().add(player);
            GameLogger.addLog(new Log("PlayerUtils_Dead", "Player " + player.getDisplayName() + " died", "death"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMaxHealth(Player p) {
        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        p.setFoodLevel(30);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
    }

    public static void respawnPlayer(Player p) {
        Game partie = mineralcontest.getPlayerGame(p);
        if (partie == null) {
            return;
        }
        DeathZone deathZone = partie.getArene().getDeathZone();
        if (!deathZone.isPlayerDead(p)) {
            return;
        }
        CouplePlayer couplePlayer = new CouplePlayer(p, 0);
        try {
            deathZone.libererJoueur(couplePlayer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

