package fr.synchroneyes.mapbuilder.Events;

import fr.synchroneyes.mapbuilder.Commands.mcteam;
import fr.synchroneyes.mapbuilder.MapBuilder;
import fr.synchroneyes.mineral.Core.House;
import java.util.LinkedList;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerClickOnBlock(PlayerInteractEvent event) {
        Player joueur;
        House maisonJoueur;
        if (!MapBuilder.getInstance().isBuilderModeEnabled) {
            return;
        }
        if (event.getClickedBlock() != null && event.getAction().equals((Object)Action.LEFT_CLICK_BLOCK) && (maisonJoueur = mcteam.getPlayerAllocatedHouse(joueur = event.getPlayer())) != null) {
            Block clickedBlock = event.getClickedBlock();
            Map.Entry<House, LinkedList<Block>> couple = mcteam.getPorteMaison(maisonJoueur);
            LinkedList<Block> porte = couple.getValue();
            if (porte.contains(clickedBlock)) {
                porte.remove(clickedBlock);
                joueur.sendMessage(ChatColor.RED + "- bloc retir\u00e9 de la porte");
            } else {
                porte.add(clickedBlock);
                joueur.sendMessage(ChatColor.GREEN + "+ bloc ajout\u00e9 \u00e0 la porte");
            }
            couple.setValue(porte);
            event.setCancelled(true);
        }
    }
}

