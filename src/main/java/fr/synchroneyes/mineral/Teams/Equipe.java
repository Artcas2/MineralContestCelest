package fr.synchroneyes.mineral.Teams;

import fr.synchroneyes.custom_events.MCPlayerJoinTeamEvent;
import fr.synchroneyes.custom_events.MCPlayerLeaveTeamEvent;
import fr.synchroneyes.custom_events.MCTeamScoreUpdated;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Statistics.Class.MeilleurJoueurStat;
import fr.synchroneyes.mineral.Statistics.Class.VilainJoueurStat;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.Log.GameLogger;
import fr.synchroneyes.mineral.Utils.Log.Log;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class Equipe implements Comparable<Equipe> {
    private LinkedList<Player> joueurs = new LinkedList();
    private String nomEquipe;
    private ChatColor couleur;
    private int score = 0;
    private int penalty = 0;
    private House maison;
    private Groupe groupe;
    private Game partie;

    public Equipe(String nom, ChatColor c, Groupe g, House maison) {
        this.nomEquipe = nom;
        this.couleur = c;
        this.groupe = g;
        this.maison = maison;
        this.partie = this.groupe.getGame();
    }

    public House getMaison() {
        return this.maison;
    }

    public Groupe getGroupe() {
        return this.groupe;
    }

    public Game getPartie() {
        return this.partie;
    }

    public void clear() {
        this.joueurs.clear();
        this.score = 0;
        this.penalty = 0;
    }

    public int getPenalty() {
        return this.penalty;
    }

    public void updateScore(Player JoueurAyantAjouteLesPoints) throws Exception {
        MCPlayer mcPlayer;
        ItemStack[] items;
        int score_gagne = 0;
        boolean hasNegativePointItemBeenAdded = false;
        int score_perdu_equipes = 0;
        Block block_coffre = this.maison.getCoffreEquipeLocation().getBlock();
        Chest openedChest = (Chest)block_coffre.getState();
        for (ItemStack item : items = openedChest.getInventory().getContents()) {
            if (item == null) continue;
            int current_item_score = 0;
            if (item.isSimilar(new ItemStack(Material.IRON_INGOT, 1))) {
                current_item_score = this.groupe.getParametresPartie().getCVAR("SCORE_IRON").getValeurNumerique();
                if (current_item_score >= 0) {
                    score_gagne += current_item_score * item.getAmount();
                } else {
                    hasNegativePointItemBeenAdded = true;
                    score_perdu_equipes += Math.abs(current_item_score) * item.getAmount();
                }
                score_gagne += current_item_score * item.getAmount();
                continue;
            }
            if (item.isSimilar(new ItemStack(Material.GOLD_INGOT, 1))) {
                current_item_score = this.groupe.getParametresPartie().getCVAR("SCORE_GOLD").getValeurNumerique();
                if (current_item_score >= 0) {
                    score_gagne += current_item_score * item.getAmount();
                    continue;
                }
                hasNegativePointItemBeenAdded = true;
                score_perdu_equipes += Math.abs(current_item_score) * item.getAmount();
                continue;
            }
            if (item.isSimilar(new ItemStack(Material.DIAMOND, 1))) {
                current_item_score = this.groupe.getParametresPartie().getCVAR("SCORE_DIAMOND").getValeurNumerique();
                if (current_item_score >= 0) {
                    score_gagne += current_item_score * item.getAmount();
                    continue;
                }
                hasNegativePointItemBeenAdded = true;
                score_perdu_equipes += Math.abs(current_item_score) * item.getAmount();
                continue;
            }
            if (item.isSimilar(new ItemStack(Material.EMERALD, 1))) {
                current_item_score = this.groupe.getParametresPartie().getCVAR("SCORE_EMERALD").getValeurNumerique();
                if (current_item_score >= 0) {
                    score_gagne += current_item_score * item.getAmount();
                    continue;
                }
                hasNegativePointItemBeenAdded = true;
                score_perdu_equipes += Math.abs(current_item_score) * item.getAmount();
                continue;
            }
            if (item.isSimilar(new ItemStack(Material.REDSTONE))) {
                current_item_score = this.groupe.getParametresPartie().getCVAR("SCORE_REDSTONE").getValeurNumerique();
                if (current_item_score >= 0) {
                    score_gagne += current_item_score * item.getAmount();
                    continue;
                }
                hasNegativePointItemBeenAdded = true;
                score_perdu_equipes += Math.abs(current_item_score) * item.getAmount();
                continue;
            }
            block_coffre.getWorld().dropItemNaturally(block_coffre.getLocation(), item);
        }
        openedChest.getInventory().clear();
        boolean scoreWasUpdated = false;
        if (score_gagne > 0) {
            scoreWasUpdated = true;
        }
        if ((mcPlayer = mineralcontest.plugin.getMCPlayer(JoueurAyantAjouteLesPoints)) != null) {
            mcPlayer.addPlayerScore(score_gagne);
        }
        this.groupe.getGame().getStatsManager().register(MeilleurJoueurStat.class, JoueurAyantAjouteLesPoints, score_gagne);
        this.setScore(score_gagne += this.getScore());
        if (scoreWasUpdated) {
            for (Player online : this.joueurs) {
                if (online == null) continue;
                online.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.team_score_now.toString(), this));
            }
        }
        if (hasNegativePointItemBeenAdded) {
            if (mcPlayer != null) {
                mcPlayer.addPlayerScorePenalityToOtherTeams(score_perdu_equipes);
            }
            this.groupe.getGame().getStatsManager().register(VilainJoueurStat.class, JoueurAyantAjouteLesPoints, score_perdu_equipes);
            this.groupe.sendToEveryone(mineralcontest.prefixGlobal + this.getCouleur() + JoueurAyantAjouteLesPoints.getDisplayName() + ChatColor.WHITE + " a fait perdre " + ChatColor.RED + score_perdu_equipes + " points" + ChatColor.WHITE + " aux autres \u00e9quipes!");
            for (House maison : this.partie.getHouses()) {
                if (maison.getTeam() == this) continue;
                maison.getTeam().retirerPoints(score_perdu_equipes);
            }
        }
    }

    public void retirerPoints(int score) {
        MCTeamScoreUpdated event = new MCTeamScoreUpdated(this.score, this.score - score, this);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return;
        }
        this.score -= score;
    }

    public void ajouterPoints(int score) {
        MCTeamScoreUpdated event = new MCTeamScoreUpdated(this.score, this.score + score, this);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return;
        }
        this.score += score;
    }

    public void sendMessage(String message, Player sender) {
        if (this.joueurs.contains(sender)) {
            for (Player member : this.joueurs) {
                member.sendMessage(mineralcontest.prefixTeamChat + sender.getDisplayName() + ": " + message);
            }
        }
    }

    public void sendMessage(String message) {
        for (Player member : this.joueurs) {
            member.sendMessage(message);
        }
    }

    public int getScore() {
        return this.score - this.penalty;
    }

    public void setScore(int score) {
        MCTeamScoreUpdated event = new MCTeamScoreUpdated(this.score, score, this);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return;
        }
        this.score = score;
        GameLogger.addLog(new Log("TeamChestScoreUpdated", "The team " + this.getNomEquipe() + " score got updated to " + score + "", "ChestEvent"));
    }

    public boolean addPlayerToTeam(Player p, boolean teleportToBase) throws Exception {
        MCPlayerJoinTeamEvent event = new MCPlayerJoinTeamEvent(mineralcontest.plugin.getMCPlayer(p), this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled()) {
            return false;
        }
        Game partie = mineralcontest.getPlayerGame(p);
        if (partie != null) {
            Equipe team = mineralcontest.getPlayerGame(p).getPlayerTeam(p);
            if (team != null) {
                team.removePlayer(p);
            }
            if (mineralcontest.getPlayerGame(p).isReferee(p)) {
                mineralcontest.getPlayerGame(p).removeReferee(p, false);
            }
        }
        this.joueurs.add(p);
        p.setGameMode(GameMode.SURVIVAL);
        if (PlayerUtils.getPlayerItemsCountInInventory(p) == 0 && mineralcontest.getPlayerGame((Player)p).isGameInitialized && teleportToBase) {
            PlayerUtils.teleportPlayer(p, mineralcontest.getPlayerGroupe(p).getMonde(), mineralcontest.getPlayerGame(p).getPlayerHouse(p).getHouseLocation());
        }
        p.sendMessage(mineralcontest.prefix + Lang.translate(Lang.team_welcome.toString(), this));
        mineralcontest.broadcastMessage(mineralcontest.prefixGlobal + Lang.translate(Lang.team_player_joined.toString(), this, p), this.groupe);
        mineralcontest.plugin.getMCPlayer(p).setEquipe(this);
        mineralcontest.plugin.getMCPlayer(p).setVisible();
        return true;
    }

    public boolean removePlayer(Player p) {
        if (this.isPlayerInTeam(p)) {
            MCPlayerLeaveTeamEvent event = new MCPlayerLeaveTeamEvent(mineralcontest.plugin.getMCPlayer(p), this);
            Bukkit.getServer().getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                return false;
            }
            this.joueurs.remove(p);
            p.sendMessage(mineralcontest.prefix + Lang.translate(Lang.team_kicked.toString(), this));
            return true;
        }
        return false;
    }

    public boolean isPlayerInTeam(Player p) {
        return this.joueurs.contains(p);
    }

    public LinkedList<Player> getJoueurs() {
        return this.joueurs;
    }

    public String toString() {
        String joueurs = "Team " + this.getCouleur() + this.nomEquipe + ChatColor.WHITE + ": ";
        for (int i = 0; i < this.joueurs.size(); ++i) {
            joueurs = joueurs + this.joueurs.get(i).getDisplayName() + " ";
        }
        return joueurs;
    }

    public String getNomEquipe() {
        return this.nomEquipe;
    }

    public ChatColor getCouleur() {
        return this.couleur;
    }

    public Color toColor() {
        if (this.nomEquipe.equals(Lang.red_team.toString())) {
            return Color.RED;
        }
        if (this.nomEquipe.equals(Lang.yellow_team.toString())) {
            return Color.YELLOW;
        }
        if (this.nomEquipe.equals(Lang.blue_team.toString())) {
            return Color.BLUE;
        }
        return Color.WHITE;
    }

    @Override
    public int compareTo(Equipe equipe) {
        return this.getScore() - equipe.getScore();
    }

    public Color getBukkitColor() {
        switch (this.getCouleur()) {
            case YELLOW: 
            case GOLD: {
                return Color.YELLOW;
            }
            case GREEN: 
            case DARK_GREEN: {
                return Color.GREEN;
            }
            case BLACK: {
                return Color.BLACK;
            }
            case GRAY: 
            case DARK_GRAY: {
                return Color.GRAY;
            }
            case BLUE: 
            case DARK_BLUE: {
                return Color.BLUE;
            }
            case AQUA: 
            case DARK_AQUA: {
                return Color.AQUA;
            }
            case DARK_RED: 
            case RED: {
                return Color.RED;
            }
            case DARK_PURPLE: 
            case LIGHT_PURPLE: {
                return Color.PURPLE;
            }
        }
        return Color.WHITE;
    }

    public String getFormattedScore() {
        String nouveau_score = "";
        DecimalFormatSymbols customSymbols = DecimalFormatSymbols.getInstance(Locale.US);
        customSymbols.setGroupingSeparator(' ');
        nouveau_score = new DecimalFormat("#,###;-#,###", customSymbols).format(this.score);
        nouveau_score = this.score >= 0 ? ChatColor.GREEN + nouveau_score : ChatColor.RED + nouveau_score;
        return nouveau_score;
    }

    public String getFormattedScore(int score) {
        String nouveau_score = "";
        DecimalFormatSymbols customSymbols = DecimalFormatSymbols.getInstance(Locale.US);
        customSymbols.setGroupingSeparator(' ');
        nouveau_score = new DecimalFormat("#,###;-#,###", customSymbols).format(score);
        nouveau_score = score >= 0 ? ChatColor.GREEN + nouveau_score : ChatColor.RED + nouveau_score;
        return nouveau_score;
    }
}

