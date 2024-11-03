package fr.synchroneyes.groups.Core;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.groups.Menus.MenuVote;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardAPI;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class MapVote {
    private static String folder_name = mineralcontest.plugin.getDataFolder() + File.separator + "worlds" + File.separator;
    private ArrayList<String> maps = new ArrayList();
    private HashMap<Player, String> votes = new HashMap();
    protected boolean voteEnabled;
    private MenuVote menuVote;
    private Groupe groupe;

    public MapVote() {
        this.chargerNomMaps();
        this.voteEnabled = true;
        this.menuVote = new MenuVote();
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public MenuVote getMenuVote() {
        return this.menuVote;
    }

    public void removePlayerVote(Player p) {
        if (this.votes.containsKey(p)) {
            this.votes.remove(p);
        }
    }

    public Map<String, Integer> getMapVotes(boolean orderByMostVoted) {
        HashMap<String, Integer> mapsVote = new HashMap<String, Integer>();
        for (Map.Entry<Player, String> infoVoteJoueur : this.votes.entrySet()) {
            if (!mapsVote.containsKey(infoVoteJoueur.getValue())) {
                mapsVote.put(infoVoteJoueur.getValue(), 1);
                continue;
            }
            mapsVote.replace(infoVoteJoueur.getValue(), (Integer)mapsVote.get(infoVoteJoueur.getValue()) + 1);
        }
        if (orderByMostVoted) {
            HashMap<String, Integer> mapsVoteOrdered = new HashMap<String, Integer>();
            int maxVotes = -1;
            String nomMap = "";
            HashMap _votes = (HashMap)this.votes.clone();
            while (!mapsVote.isEmpty()) {
                for (Map.Entry vote : mapsVote.entrySet()) {
                    if ((Integer)vote.getValue() < maxVotes) continue;
                    maxVotes = (Integer)vote.getValue();
                    nomMap = (String)vote.getKey();
                }
                mapsVoteOrdered.put(nomMap, maxVotes);
                mapsVote.remove(nomMap);
                maxVotes = Integer.MIN_VALUE;
            }
            return mapsVoteOrdered;
        }
        return mapsVote;
    }

    public void disableVote() {
        this.voteEnabled = false;
    }

    public void clearVotes() {
        this.votes.clear();
    }

    private void chargerNomMaps() {
        File[] maps;
        File dossierMaps = new File(folder_name);
        if (!dossierMaps.exists()) {
            dossierMaps.mkdir();
        }
        for (File map : maps = dossierMaps.listFiles()) {
            if (!map.isDirectory() || !map.getName().startsWith("mc_")) continue;
            this.maps.add(map.getName());
        }
    }

    public ArrayList<String> getMaps() {
        return this.maps;
    }

    public boolean havePlayerVoted(Player joueur) {
        for (Map.Entry<Player, String> couple : this.votes.entrySet()) {
            if (!couple.getKey().equals((Object)joueur)) continue;
            return true;
        }
        return false;
    }

    public void enregistrerVoteJoueur(String idMap, Player joueur) {
        if (!this.voteEnabled) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.vote_not_enabled.toString());
            return;
        }
        if (this.havePlayerVoted(joueur)) {
            this.votes.replace(joueur, idMap);
        } else {
            this.votes.put(joueur, idMap);
        }
        this.updatePlayersVoteHUD();
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.vote_you_voted_for_map.toString().replace("%map%", idMap));
        Groupe playerGroupe = mineralcontest.getPlayerGroupe(joueur);
        if (this.votes.size() == playerGroupe.getPlayerCount()) {
            this.setVoteEnabled(false);
            String mapAcharger = this.getMapGagnante();
            playerGroupe.chargerMonde(mapAcharger);
        }
    }

    public String getNomMapFromID(int idMap) {
        int index = 0;
        for (String nom : this.maps) {
            if (index == idMap) {
                return nom;
            }
            ++index;
        }
        return "";
    }

    public int getMapVoteCount(String nomMap) {
        int nbVote = 0;
        for (Map.Entry<Player, String> couple : this.votes.entrySet()) {
            if (!couple.getValue().equalsIgnoreCase(nomMap)) continue;
            ++nbVote;
        }
        return nbVote;
    }

    public String getMapGagnante() {
        int maxVote = -1;
        String mapGagnante = "";
        for (String map : this.maps) {
            int currentMapVote = this.getMapVoteCount(map);
            if (currentMapVote <= maxVote) continue;
            maxVote = currentMapVote;
            mapGagnante = map;
        }
        return mapGagnante;
    }

    public boolean isVoteEnabled() {
        return this.voteEnabled;
    }

    public void setVoteEnabled(boolean voteEnabled) {
        this.voteEnabled = voteEnabled;
    }

    public List<Player> joueurAyantNonVote() {
        if (this.votes.isEmpty()) {
            return new ArrayList<Player>();
        }
        ArrayList<Player> joueurSansVote = new ArrayList<Player>();
        Groupe groupe = mineralcontest.getPlayerGroupe(this.votes.entrySet().iterator().next().getKey());
        if (groupe == null) {
            return joueurSansVote;
        }
        joueurSansVote.addAll(groupe.getPlayers());
        for (Map.Entry<Player, String> infoVote : this.votes.entrySet()) {
            joueurSansVote.remove(infoVote.getKey());
        }
        return joueurSansVote;
    }

    private void updatePlayersVoteHUD() {
        if (this.groupe == null) {
            return;
        }
        Map<String, Integer> liste_votes = this.getMapVotes(true);
        for (Player joueur : this.groupe.getPlayers()) {
            ScoreboardAPI.clearScoreboard(joueur);
            int index = 16;
            for (Map.Entry<String, Integer> votes : liste_votes.entrySet()) {
                ScoreboardAPI.addScoreboardText(joueur, votes.getKey() + " - " + votes.getValue(), index--);
            }
        }
    }
}

