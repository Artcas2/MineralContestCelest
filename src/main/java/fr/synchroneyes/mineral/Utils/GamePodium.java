package fr.synchroneyes.mineral.Utils;

import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ArmorStandUtility;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.ArmorStand;

public class GamePodium {
    private static Location getCenter(Location loc) {
        return new Location(loc.getWorld(), GamePodium.getRelativeCoord(loc.getBlockX()), GamePodium.getRelativeCoord(loc.getBlockY()), GamePodium.getRelativeCoord(loc.getBlockZ()));
    }

    private static double getRelativeCoord(int i) {
        double d = i;
        return d + 0.5;
    }

    public static void spawn(Location position, List<Equipe> equipes) {
        Location centrePodium = position.clone();
        Location premierePlace = centrePodium.getBlock().getRelative(BlockFace.UP).getLocation();
        Location secondePlace = centrePodium.getBlock().getRelative(BlockFace.WEST).getLocation();
        Location troisiemePlace = centrePodium.getBlock().getRelative(BlockFace.EAST).getLocation();
        centrePodium.getBlock().setType(Material.QUARTZ_BLOCK);
        premierePlace.getBlock().setType(Material.QUARTZ_SLAB);
        secondePlace.getBlock().setType(Material.QUARTZ_BLOCK);
        troisiemePlace.getBlock().setType(Material.QUARTZ_BLOCK);
        premierePlace.setY((double)(premierePlace.getBlockY() + 1));
        secondePlace.setY((double)(secondePlace.getBlockY() + 1));
        troisiemePlace.setY((double)(troisiemePlace.getBlockY() + 1));
        if (equipes.get(0) != null) {
            ArmorStand premierArmorStand = ArmorStandUtility.createArmorStandWithColoredLeather(GamePodium.getCenter(premierePlace), equipes.get(0).getCouleur() + equipes.get(0).getNomEquipe(), equipes.get(0).getBukkitColor(), Material.GOLDEN_SWORD);
            GamePodium.setSignWithTeamScore(premierePlace.getBlock(), equipes.get(0));
            PlayerUtils.setFirework(GamePodium.getCenter(premierArmorStand.getLocation()), equipes.get(0).getBukkitColor(), 2);
        }
        if (equipes.size() >= 2 && equipes.get(1) != null) {
            ArmorStandUtility.createArmorStandWithColoredLeather(GamePodium.getCenter(secondePlace), equipes.get(1).getCouleur() + equipes.get(1).getNomEquipe(), equipes.get(1).getBukkitColor(), Material.DIAMOND_SWORD);
            GamePodium.setSignWithTeamScore(secondePlace.getBlock(), equipes.get(1));
        }
        if (equipes.size() >= 3 && equipes.get(2) != null) {
            ArmorStandUtility.createArmorStandWithColoredLeather(GamePodium.getCenter(troisiemePlace), equipes.get(2).getCouleur() + equipes.get(2).getNomEquipe(), equipes.get(2).getBukkitColor(), Material.WOODEN_SWORD);
            GamePodium.setSignWithTeamScore(troisiemePlace.getBlock(), equipes.get(2));
        }
    }

    private static void setSignWithTeamScore(Block blockSupport, Equipe equipe) {
        Block panneauPremier = blockSupport.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN);
        panneauPremier.setType(Material.ACACIA_WALL_SIGN);
        Sign sign = (Sign)panneauPremier.getState();
        sign.setEditable(true);
        sign.setLine(1, equipe.getCouleur() + equipe.getNomEquipe());
        sign.setLine(2, Lang.hud_score_text.toString());
        sign.setLine(3, equipe.getScore() + "");
        sign.update();
        WallSign wallSign = (WallSign)panneauPremier.getBlockData();
        wallSign.setFacing(BlockFace.SOUTH);
        panneauPremier.setBlockData((BlockData)wallSign);
    }
}

