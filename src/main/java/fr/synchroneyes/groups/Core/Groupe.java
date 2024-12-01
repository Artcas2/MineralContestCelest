package fr.synchroneyes.groups.Core;

import fr.synchroneyes.custom_events.MCPlayerReconnectEvent;
import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Core.Player.BaseItem.PlayerBaseItem;
import fr.synchroneyes.mineral.Core.Spectators.SpectatorManager;
import fr.synchroneyes.mineral.Kits.Classes.Mineur;
import fr.synchroneyes.mineral.Kits.KitManager;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardAPI;
import fr.synchroneyes.mineral.Scoreboard.newapi.ScoreboardFields;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.DisconnectedPlayer;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayer;
import fr.synchroneyes.mineral.Utils.Player.PlayerUtils;
import fr.synchroneyes.mineral.mineralcontest;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Groupe {
    private int tailleIdentifiant = 25;
    private String identifiant = StringUtils.repeat('0', tailleIdentifiant);
    private LinkedList<Player> admins = new LinkedList();
    private LinkedList<Player> joueurs = new LinkedList();
    private LinkedList<Player> joueursInvites = new LinkedList();
    private World gameWorld;
    private World nether;
    private MapVote mapVote;
    private Game partie;
    private String nom;
    private WorldLoader worldLoader;
    private Etats etat;
    private boolean groupLocked = false;
    private String mapName = "";
    private GameSettings parametresPartie;
    private PlayerBaseItem playerBaseItem;
    private AutomatedChestManager automatedChestManager;
    private KitManager kitManager;
    private SpectatorManager spectatorManager;
    private LinkedList<DisconnectedPlayer> disconnectedPlayers = new LinkedList();

    public Groupe() {
        this.parametresPartie = new GameSettings(true, this);
        this.playerBaseItem = new PlayerBaseItem(this);
        this.automatedChestManager = new AutomatedChestManager(this);
        this.partie = new Game(this);
        this.partie.setGroupe(this);
        this.etat = Etats.EN_ATTENTE;
        this.worldLoader = new WorldLoader(this);
        this.kitManager = new KitManager(this);
        this.spectatorManager = new SpectatorManager(this.partie);
    }

    public AutomatedChestManager getAutomatedChestManager() {
        return this.automatedChestManager;
    }

    public void resetGame() {
        this.kitManager.removeAllPlayersKit();
        String oldPartie = this.partie.toString();
        this.partie = new Game(this);
        for (Player player : this.getPlayers()) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(player);
            mcPlayer.setPartie(this.partie);
        }
        this.partie.startGameLoop();
        this.dechargerMonde();
    }

    public void removePlayer(Player joueur) {
        this.joueurs.remove(joueur);
    }

    public String getNomsJoueurNonPret() {
        ArrayList<Player> joueurNonPrets = new ArrayList<Player>(this.getPlayers());
        StringBuilder joueursNonPret_text = new StringBuilder();
        for (Player joueurPret : this.partie.getPlayersReady()) {
            joueurNonPrets.remove(joueurPret);
        }
        for (Player joueurNonPret : joueurNonPrets) {
            joueursNonPret_text.append(joueurNonPret.getDisplayName() + " ");
        }
        return joueursNonPret_text.toString();
    }

    public void removeAllDroppedItem() {
        List<Entity> entList = this.gameWorld.getEntities();
        for (Entity current : entList) {
            if (!(current instanceof Item)) continue;
            current.remove();
        }
    }

    public PlayerBaseItem getPlayerBaseItem() {
        return this.playerBaseItem;
    }

    public GameSettings getParametresPartie() {
        return this.parametresPartie;
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public LinkedList<Player> getPlayers() {
        LinkedList<Player> liste_joueurs = new LinkedList<Player>();
        for (Player membre : this.joueurs) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(membre);
            if (mcPlayer == null || !mcPlayer.isInPlugin()) continue;
            liste_joueurs.add(membre);
        }
        return liste_joueurs;
    }

    public String getIdentifiant() {
        return this.identifiant;
    }

    public World getMonde() {
        return this.gameWorld;
    }

    public Equipe getPlayerTeam(Player p) {
        for (House maison : this.getGame().getHouses()) {
            if (!maison.getTeam().isPlayerInTeam(p)) continue;
            return maison.getTeam();
        }
        return null;
    }

    public void genererIdentifiant() {
        String[] alphabet = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder id_generer = new StringBuilder();
        Random random = new Random();
        int numero_aleatoire = 0;
        for (int i = 0; i < this.tailleIdentifiant; ++i) {
            numero_aleatoire = random.nextInt(alphabet.length);
            id_generer.append(alphabet[numero_aleatoire]);
        }
        this.identifiant = id_generer.toString();
    }

    public boolean chargerMonde(String nomMonde) {
        try {
            this.sendToEveryone(mineralcontest.prefixGroupe + "Chargement de la map \"" + nomMonde + "\" en cours ...");
            this.genererIdentifiant();
            this.setMapName(nomMonde + "_" + this.getIdentifiant());
            this.worldLoader.chargerMondeThreade(nomMonde, this);
        } catch (Exception e) {
            this.sendToadmin(mineralcontest.prefixErreur + " Impossible de charger le monde. Erreur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean dechargerMonde() {
        if (this.gameWorld == null) {
            return false;
        }
        for (Player joueur : this.joueurs) {
            joueur.teleport(mineralcontest.plugin.defaultSpawn);
        }
        mineralcontest.plugin.getServer().unloadWorld(this.gameWorld, false);
        this.worldLoader.supprimerMonde(this.gameWorld);
        return true;
    }

    public boolean isGroupLocked() {
        return this.groupLocked;
    }

    public void setGroupLocked(boolean groupLocked) {
        this.sendToadmin(mineralcontest.prefixPrive + (groupLocked ? Lang.group_is_now_locked.toString() : Lang.group_is_now_unlocked.toString()));
        this.groupLocked = groupLocked;
    }

    public MapVote getMapVote() {
        return this.mapVote;
    }

    public void initVoteMap() {
        this.mapVote = new MapVote();
        this.mapVote.setGroupe(this);
        if (this.mapVote.getMaps().isEmpty()) {
            this.mapVote.disableVote();
            this.setEtat(Etats.EN_ATTENTE);
            this.setGroupLocked(false);
            this.sendToadmin(mineralcontest.prefixErreur + Lang.error_no_maps_downloaded_to_start_game.toString());
        } else {
            this.setEtat(Etats.VOTE_EN_COURS);
            this.setGroupLocked(true);
            this.sendToEveryone(mineralcontest.prefixGroupe + Lang.vote_started.toString());
            for (Player membreGroupe : this.getPlayers()) {
                this.getMapVote().getMenuVote().openInventory(membreGroupe);
            }
        }
    }

    public void enableVote() {
        if (this.mapVote != null) {
            this.mapVote.voteEnabled = true;
        }
    }

    public Etats getEtatPartie() {
        return this.etat;
    }

    public void setEtat(Etats etat) {
        this.etat = etat;
    }

    public boolean isPlayerInvited(Player p) {
        return this.joueursInvites.contains(p);
    }

    public void removeAdmin(Player joueur) {
        this.sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_is_no_longer_a_group_admin.toString(), joueur));
        this.admins.remove(joueur);
    }

    public void inviterJoueur(Player p) {
        if (this.joueursInvites.contains(p)) {
            this.sendToadmin("ERREUR DEJA INVITE");
            return;
        }
        if (this.joueurs.contains(p)) {
            this.sendToadmin(mineralcontest.prefixErreur + Lang.translate(Lang.error_player_already_in_this_group.toString(), p));
            return;
        }
        if (mineralcontest.getPlayerGroupe(p) != null) {
            this.sendToadmin(mineralcontest.prefixErreur + Lang.translate(Lang.error_player_already_have_a_group.toString(), p));
            return;
        }
        p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_got_invited_to_a_group.toString(), this));
        this.sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_successfully_invited_to_group.toString(), p));
        this.joueursInvites.add(p);
    }

    public Game getGame() {
        return this.partie;
    }

    public String getNom() {
        return this.nom;
    }

    public String setNom(String nom) {
        this.nom = nom;
        return this.nom;
    }

    public boolean containsPlayer(Player p) {
        return this.joueurs.contains(p);
    }

    public boolean isGroupeCreateur(Player p) {
        return this.admins.getFirst().equals((Object)p);
    }

    public boolean isAdmin(Player p) {
        return this.admins.contains(p);
    }

    public World getNether() {
        return this.nether;
    }

    public void kickPlayer(Player p) {
        this.retirerJoueur(p);
        p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_were_kicked_from_a_group.toString(), this));
        this.sendToEveryone(mineralcontest.prefixPrive + Lang.translate(Lang.player_got_kicked_from_group.toString(), p));
    }

    public void sendToadmin(String message) {
        for (Player player : this.getAdmins()) {
            player.sendMessage(message);
        }
    }

    public void sendToEveryone(String message) {
        for (Player p : this.getPlayers()) {
            p.sendMessage(message);
        }
    }

    public void addJoueur(Player p) {
        if (this.joueurs.contains(p)) {
            return;
        }
        p.setLevel(0);
        this.joueursInvites.remove(p);
        this.joueurs.add(p);
        if (mineralcontest.communityVersion) {
            p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.successfully_joined_a_group.toString(), this));
        }
        if (mineralcontest.communityVersion) {
            this.sendToEveryone(mineralcontest.prefixGroupe + Lang.translate(Lang.player_joined_our_group.toString(), p));
        }
        for (Player joueur : this.joueurs) {
            if (this.partie.isPlayerReady(joueur)) continue;
            joueur.sendMessage(mineralcontest.prefixPrive + Lang.set_yourself_as_ready_to_start_votemap.toString());
        }
        if (mineralcontest.plugin.getMCPlayer(p) == null) {
            mineralcontest.plugin.addNewPlayer(p);
        }
        mineralcontest.plugin.getMCPlayer(p).setGroupe(this);
        HashMap<ScoreboardFields, String> map = new HashMap<ScoreboardFields, String>();
        map.put(ScoreboardFields.SCOREBOARD_PLAYER_COUNT, ScoreboardAPI.prefix + this.getPlayerCount() + "");
        if (this.getAdmins().size() > 0) {
            map.put(ScoreboardFields.SCOREBOARD_ADMINS, ScoreboardAPI.prefix + this.getAdmins().getFirst().getDisplayName());
        }
        this.updatePlayersHUD(map);
        Bukkit.getScheduler().runTaskLater((Plugin)mineralcontest.plugin, () -> {
            this.teleportToGroupWorld(p);
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(p);
            mcPlayer.setVisible();
        }, 100L);
    }

    public void addAdmin(Player p) {
        if (!this.joueurs.contains(p)) {
            this.addJoueur(p);
        }
        if (!this.admins.contains(p)) {
            this.admins.add(p);
        }
        if (mineralcontest.communityVersion) {
            this.sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_is_now_group_admin.toString(), p));
        }
        this.teleportToGroupWorld(p);
    }

    public int getPlayerCount() {
        return this.getPlayers().size();
    }

    public void retirerJoueur(Player joueur) {
        this.joueurs.remove(joueur);
        this.admins.remove(joueur);
        this.joueursInvites.remove(joueur);
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_left_the_group.toString(), this));
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
        if (mcPlayer != null) {
            mcPlayer.setGroupe(null);
        }
        HashMap<ScoreboardFields, String> map = new HashMap<ScoreboardFields, String>();
        map.put(ScoreboardFields.SCOREBOARD_PLAYER_COUNT, ScoreboardAPI.prefix + this.getPlayerCount() + "");
        if (this.getAdmins().size() > 0) {
            map.put(ScoreboardFields.SCOREBOARD_ADMINS, ScoreboardAPI.prefix + this.getAdmins().getFirst().getDisplayName());
        }
        this.updatePlayersHUD(map);
    }

    public LinkedList<Player> getAdmins() {
        LinkedList<Player> liste_admin = new LinkedList<Player>(this.admins);
        liste_admin.removeIf(admin -> {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer((Player)admin);
            if (mcPlayer == null) {
                return false;
            }
            return !mcPlayer.isInPlugin();
        });
        return liste_admin;
    }

    public void addDisconnectedPlayer(Player p, Location oldPlayerLocation) {
        Equipe oldPlayerTeam = this.getPlayerTeam(p);
        CouplePlayer oldPlayerDeathTime = this.partie.getArene().getDeathZone().getPlayerInfo(p);
        DisconnectedPlayer joueur = new DisconnectedPlayer(p.getUniqueId(), oldPlayerTeam, this, oldPlayerDeathTime, oldPlayerLocation, p, this.getGame().getPlayerBonusManager().getListeBonusJoueur(p), this.getKitManager().getPlayerKit(p));
        if (this.mapVote != null) {
            this.getMapVote().removePlayerVote(p);
        }
        this.getGame().removePlayerReady(p);
        this.getGame().removePlayerReady(p);
        this.getGame().groupe.getPlayers().remove(p);
        if (!this.havePlayerDisconnected(p)) {
            this.disconnectedPlayers.add(joueur);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + " Disconnected location: " + oldPlayerLocation);
        }
        if (!this.havePlayerDisconnected(p)) {
            p.sendMessage(ChatColor.GOLD + "" + joueur);
        }
        this.retirerJoueur(p);
    }

    public DisconnectedPlayer getDisconnectedPlayerInfo(Player p) {
        for (DisconnectedPlayer disconnectedPlayer : this.disconnectedPlayers) {
            if (!disconnectedPlayer.getPlayerUUID().equals(p.getUniqueId())) continue;
            return disconnectedPlayer;
        }
        return null;
    }

    public void playerHaveReconnected(Player p) {
        if (this.havePlayerDisconnected(p)) {
            PlayerUtils.applyPVPtoPlayer(p);
            DisconnectedPlayer infoJoueur = this.getDisconnectedPlayerInfo(p);
            try {
                if (infoJoueur.getOldPlayerGroupe() != null) {
                    if (p.isOp()) {
                        infoJoueur.getOldPlayerGroupe().addAdmin(p);
                    } else {
                        infoJoueur.getOldPlayerGroupe().addJoueur(p);
                    }
                    if (infoJoueur.getOldPlayerTeam() != null) {
                        infoJoueur.getOldPlayerTeam().addPlayerToTeam(p, false);
                    }
                    if (infoJoueur.wasPlayerDead()) {
                        CouplePlayer deathTime = new CouplePlayer(p, infoJoueur.getOldPlayerDeathTime().getValeur());
                        Groupe playerGroup = infoJoueur.getOldPlayerGroupe();
                        if (playerGroup.getGame() != null && playerGroup.getGame().getArene() != null && playerGroup.getGame().getArene().getDeathZone() != null) {
                            playerGroup.getGame().getArene().getDeathZone().add(deathTime);
                        }
                    }
                    if (infoJoueur.getKit() != null) {
                        this.getKitManager().setPlayerKit(p, infoJoueur.getKit());
                    }
                    p.getInventory().clear();
                    if (infoJoueur.getKit() instanceof Mineur) {
                        for (int index = 9; index < 18; ++index) {
                            p.getInventory().setItem(index, Mineur.getBarrierItem());
                        }
                    }
                    for (ItemStack item : infoJoueur.getOldPlayerInventory()) {
                        if (item.getType() == Material.BARRIER) continue;
                        p.getInventory().addItem(new ItemStack[]{item});
                    }
                    p.teleport(infoJoueur.getOldPlayerLocation());
                    PlayerBonus playerBonusManager = this.getGame().getPlayerBonusManager();
                    playerBonusManager.setPlayerBonusList(p, infoJoueur.getBonus());
                    playerBonusManager.triggerEnabledBonusOnReconnect(p);
                    this.disconnectedPlayers.remove(infoJoueur);
                    MCPlayerReconnectEvent event = new MCPlayerReconnectEvent(mineralcontest.plugin.getMCPlayer(p));
                    event.callEvent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean havePlayerDisconnected(Player p) {
        for (DisconnectedPlayer disconnectedPlayer : this.disconnectedPlayers) {
            if (!disconnectedPlayer.getPlayerUUID().equals(p.getUniqueId())) continue;
            return true;
        }
        return false;
    }

    private void teleportToGroupWorld(Player p) {
        if (this.getMonde() != null && !p.getWorld().equals((Object)this.getMonde())) {
            PlayerUtils.teleportPlayer(p, this.getMonde(), this.getGame().getArene().getCoffre().getLocation());
        }
    }

    public void setNether(World nether) {
        this.nether = nether;
    }

    private void updatePlayersHUD(ScoreboardFields champs, String valeur) {
        for (Player joueur : this.getPlayers()) {
            ScoreboardAPI.updateField(joueur, champs, valeur);
        }
    }

    private void updatePlayersHUD(Map<ScoreboardFields, String> valeurs) {
        for (Player joueur : this.getPlayers()) {
            for (Map.Entry<ScoreboardFields, String> _donnes : valeurs.entrySet()) {
                ScoreboardAPI.updateField(joueur, _donnes.getKey(), _donnes.getValue());
            }
        }
    }

    protected void setGameWorld(World gameWorld) {
        this.gameWorld = gameWorld;
    }

    public KitManager getKitManager() {
        return this.kitManager;
    }

    public SpectatorManager getSpectatorManager() {
        return this.spectatorManager;
    }
}

