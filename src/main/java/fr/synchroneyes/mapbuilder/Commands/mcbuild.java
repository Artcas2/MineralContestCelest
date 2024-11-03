package fr.synchroneyes.mapbuilder.Commands;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.mapbuilder.Core.Monde;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Shop.NPCs.BonusSeller;
import fr.synchroneyes.mineral.Utils.Door.DisplayBlock;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class mcbuild extends CommandTemplate {
    private LinkedList<String> actionsPossible = new LinkedList();

    public mcbuild() {
        this.actionsPossible.add("save");
        this.actionsPossible.add("menu");
        this.actionsPossible.add("setSpawn");
        this.actionsPossible.add("enable");
        this.actionsPossible.add("disable");
        this.actionsPossible.add("playzone_radius");
        this.addArgument("action", true);
        this.addArgument("nom de la map", false);
        this.constructArguments();
        this.accessCommande.add(4);
        this.accessCommande.add(10);
        this.accessCommande.add(0);
        this.accessCommande.add(12);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Monde monde = MapBuilder.monde;
        Player joueur = (Player)commandSender;
        Bukkit.getLogger().info("x: " + joueur.getLocation().getX() + "");
        Bukkit.getLogger().info("y: " + joueur.getLocation().getY() + "");
        Bukkit.getLogger().info("z: " + joueur.getLocation().getZ() + "");
        Bukkit.getLogger().info("pitch: " + joueur.getLocation().getPitch() + "");
        Bukkit.getLogger().info("yaw: " + joueur.getLocation().getYaw() + "");
        if (args[0].equalsIgnoreCase("save")) {
            if (args.length == 2) {
                String nomMap = args[1];
                joueur.sendMessage("Sauvegarde de la map: " + nomMap);
                this.sauvegarderMonde(nomMap);
                return false;
            }
            joueur.sendMessage(mineralcontest.prefixErreur + "Usage: /" + this.getCommand() + "save <nom de la map>");
            return false;
        }
        if (args[0].equalsIgnoreCase("menu")) {
            return false;
        }
        if (args[0].equalsIgnoreCase("setSpawn")) {
            monde.setSpawnDepart(joueur.getLocation());
            joueur.sendMessage(mineralcontest.prefixPrive + "Le spawn de d\u00e9part pour ce monde a bien \u00e9t\u00e9 enregistr\u00e9 !");
            return false;
        }
        if (args[0].equalsIgnoreCase("playzone_radius")) {
            int taille = Integer.parseInt(args[1]);
            World _monde = joueur.getWorld();
            _monde.getWorldBorder().setCenter(monde.getArene().getCoffre().getLocation());
            _monde.getWorldBorder().setSize((double)(taille * 2));
            monde.setHouses_playzone_radius(taille);
            return false;
        }
        if (args[0].equalsIgnoreCase("enable")) {
            MapBuilder.enableMapBuilder();
        } else if (args[0].equalsIgnoreCase("disable")) {
            MapBuilder.disableMapBuilder();
        }
        return false;
    }

    @Override
    public String getCommand() {
        return "mcbuild";
    }

    private void sauvegarderMonde(String nom) {
        Monde monde = MapBuilder.monde;
        monde.setNom(nom);
        File fichierMonde = new File(mineralcontest.plugin.getDataFolder() + File.separator + "generated_maps" + File.separator + nom + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File)fichierMonde);
        yamlConfiguration.set("map_name", (Object)nom);
        Location spawnLocation = monde.getSpawnDepart();
        if (spawnLocation == null) {
            yamlConfiguration.set("default_spawn", (Object)"null");
        } else {
            yamlConfiguration.set("default_spawn.x", (Object)spawnLocation.getBlockX());
            yamlConfiguration.set("default_spawn.y", (Object)spawnLocation.getBlockY());
            yamlConfiguration.set("default_spawn.z", (Object)spawnLocation.getBlockZ());
        }
        try {
            yamlConfiguration.set("arena.chest.x", (Object)monde.getArene().getCoffre().getLocation().getX());
            yamlConfiguration.set("arena.chest.y", (Object)monde.getArene().getCoffre().getLocation().getY());
            yamlConfiguration.set("arena.chest.z", (Object)monde.getArene().getCoffre().getLocation().getZ());
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            e.printStackTrace();
            Bukkit.broadcastMessage((String)"Une erreur est survenue lors de la sauvegarde de la map, veuillez regarder la console!");
            return;
        }
        yamlConfiguration.set("arena.teleport.x", (Object)monde.getArene().getTeleportSpawn().getX());
        yamlConfiguration.set("arena.teleport.y", (Object)monde.getArene().getTeleportSpawn().getY());
        yamlConfiguration.set("arena.teleport.z", (Object)monde.getArene().getTeleportSpawn().getZ());
        try {
            for (House house : monde.getHouses()) {
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".color", (Object)house.getTeam().getCouleur().toString());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".spawn.x", (Object)house.getHouseLocation().getX());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".spawn.y", (Object)house.getHouseLocation().getY());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".spawn.z", (Object)house.getHouseLocation().getZ());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".coffre.x", (Object)house.getCoffreEquipeLocation().getX());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".coffre.y", (Object)house.getCoffreEquipeLocation().getY());
                yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".coffre.z", (Object)house.getCoffreEquipeLocation().getZ());
                int index = 0;
                for (DisplayBlock blockPorte : house.getPorte().getPorte()) {
                    yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".porte." + index + ".x", (Object)blockPorte.getBlock().getLocation().getX());
                    yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".porte." + index + ".y", (Object)blockPorte.getBlock().getLocation().getY());
                    yamlConfiguration.set("house." + house.getTeam().getNomEquipe() + ".porte." + index + ".z", (Object)blockPorte.getBlock().getLocation().getZ());
                    ++index;
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe(e.getMessage());
            e.printStackTrace();
            Bukkit.broadcastMessage((String)"Une erreur est survenue lors de la sauvegarde de la map, veuillez regarder la console!");
            return;
        }
        try {
            int indexNPC = 0;
            for (BonusSeller npc : monde.getGroupe().getGame().getShopManager().getListe_pnj()) {
                yamlConfiguration.set("npcs." + indexNPC + ".x", (Object)npc.getEmplacement().getX());
                yamlConfiguration.set("npcs." + indexNPC + ".y", (Object)npc.getEmplacement().getY());
                yamlConfiguration.set("npcs." + indexNPC + ".z", (Object)npc.getEmplacement().getZ());
                yamlConfiguration.set("npcs." + indexNPC + ".pitch", (Object)Float.valueOf(npc.getEmplacement().getPitch()));
                yamlConfiguration.set("npcs." + indexNPC + ".yaw", (Object)Float.valueOf(npc.getEmplacement().getYaw()));
                ++indexNPC;
            }
            for (Entity entity : monde.getArene().getCoffre().getLocation().getWorld().getEntities()) {
                if (!(entity instanceof Villager)) continue;
                entity.remove();
            }
            monde.getArene().getCoffre().getLocation().getWorld().save();
        } catch (Exception indexNPC) {
            // empty catch block
        }
        yamlConfiguration.set("settings.protected_zone_area_radius", (Object)monde.getHouses_playzone_radius());
        yamlConfiguration.set("settings.mp_set_playzone_radius", (Object)1000);
        try {
            yamlConfiguration.save(fichierMonde);
            Bukkit.broadcastMessage((String)("Le fichier a bien \u00e9t\u00e9 enregistr\u00e9 ! Il se trouve dans " + fichierMonde.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Gestion du monde";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (alias.equalsIgnoreCase("mcbuild") && (args.length == 0 || args.length == 1)) {
            return this.actionsPossible;
        }
        return new LinkedList<String>();
    }
}

