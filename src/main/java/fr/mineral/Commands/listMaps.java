package fr.mineral.Commands;

import fr.mineral.Utils.Save.SaveHouse;
import fr.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class listMaps implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage("Listing maps for folder: " + args[0]);

        if(args[0].equals("save")) {
            sender.sendMessage("Saving to file ...");
            SaveHouse sh = new SaveHouse();
            try {
                sh.saveToFile();
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("Erreur lors de la sauvegarde");
            }
            return true;
        }

        try {


            String[] files;
            if(args.length > 1) files = getResourceListing(this.getClass(), args[0], args[1]);
            else files = getResourceListing(this.getClass(), args[0], ".json");
            int i = 1;
            for(String f : files) {
                sender.sendMessage(i + " - " + f);
                ++i;
            }
        }catch (Exception e) {
            sender.sendMessage("Erreur: " + e.getMessage());
            mineralcontest.log.severe(e.getLocalizedMessage());
            e.printStackTrace();

        }

        return true;
    }



    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     *
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    String[] getResourceListing(Class clazz, String path, String contains) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while(entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    if(entry.contains(contains)) result.add(entry);
                }
            }

            String lastchar = path.substring(path.length() - 1);
            if(result.size() == 0 && !lastchar.equals("/")) return getResourceListing(clazz, path + "/", contains);
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }
}