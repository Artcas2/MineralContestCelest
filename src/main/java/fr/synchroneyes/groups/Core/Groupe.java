/**
 * TODO:
 * - Votemap
 * - Démarrer partie
 * - Creer teams (max3)
 */
package fr.synchroneyes.groups.Core;

import fr.synchroneyes.groups.Utils.Etats;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestManager;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.House;
import fr.synchroneyes.mineral.Core.Player.BaseItem.PlayerBaseItem;
import fr.synchroneyes.mineral.Kits.KitManager;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.DisconnectedPlayer;
import fr.synchroneyes.mineral.Utils.Player.CouplePlayer;
import fr.synchroneyes.mineral.mineralcontest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Groupe {
    private int tailleIdentifiant = 10;
    private String identifiant;

    private LinkedList<Player> admins;
    private LinkedList<Player> joueurs;
    private LinkedList<Player> joueursInvites;


    @Setter(AccessLevel.PROTECTED)
    private World gameWorld;
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

    @Getter
    private KitManager kitManager;


    // UUID, <NomEquipe, PositionDeco>
    //
    // private HashMap<UUID, Pair<String, Location>> disconnectedPlayers;
    private LinkedList<DisconnectedPlayer> disconnectedPlayers;

    public Groupe() {

        this.admins = new LinkedList<>();
        this.joueurs = new LinkedList<>();
        this.joueursInvites = new LinkedList<>();

        this.disconnectedPlayers = new LinkedList<>();

        parametresPartie = new GameSettings(true, this);

        this.playerBaseItem = new PlayerBaseItem(this);
        this.automatedChestManager = new AutomatedChestManager(this);

        this.partie = new Game(this);
        this.partie.init();
        partie.setGroupe(this);

        this.etat = Etats.EN_ATTENTE;
        this.worldLoader = new WorldLoader(this);

        this.kitManager = new KitManager(this);
        genererIdentifiant();


    }


    public AutomatedChestManager getAutomatedChestManager() {
        return automatedChestManager;
    }

    /**
     * Méthode permettant de remettre à 0 une partie
     * Cette méthode est appelée à la fin d'une partie
     */
    public void resetGame() {
        this.partie = new Game(this);
        this.partie.init();
    }


    /**
     * Retourne une chaine contenant la liste des joueurs non prêt
     *
     * @return
     */
    public String getNomsJoueurNonPret() {

        List<Player> joueurNonPrets = new ArrayList<>(joueurs);

        StringBuilder joueursNonPret_text = new StringBuilder();

        for (Player joueurPret : partie.getPlayersReady())
            joueurNonPrets.remove(joueurPret);

        for (Player joueurNonPret : joueurNonPrets)
            joueursNonPret_text.append(joueurNonPret.getDisplayName() + " ");

        return joueursNonPret_text.toString();

    }

    /**
     * Retire tous les items au sol
     * Source: https://bukkit.org/threads/remove-dropped-items-on-ground.100750/
     */
    public void removeAllDroppedItem() {
        List<Entity> entList = gameWorld.getEntities();//get all entities in the world

        for (Entity current : entList) {//loop through the list
            if (current instanceof Item) {//make sure we aren't deleting mobs/players
                current.remove();//remove it
            }
        }
    }

    public PlayerBaseItem getPlayerBaseItem() {
        return playerBaseItem;
    }

    public GameSettings getParametresPartie() {
        return parametresPartie;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public LinkedList<Player> getPlayers() {
        return joueurs;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public World getMonde() {
        return gameWorld;
    }


    public Equipe getPlayerTeam(Player p) {
        for (House maison : getGame().getHouses())
            if (maison.getTeam().isPlayerInTeam(p)) return maison.getTeam();
        return null;

    }

    public void genererIdentifiant() {
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder id_generer = new StringBuilder();
        Random random = new Random();
        int numero_aleatoire = 0;
        for (int i = 0; i < tailleIdentifiant; ++i) {
            numero_aleatoire = random.nextInt(alphabet.length);
            id_generer.append(alphabet[numero_aleatoire]);
        }

        this.identifiant = id_generer.toString();

    }

    /**
     * @param nomMonde - Nom du monde à charger
     * @return true si chargement réussi, false sinon
     */
    public boolean chargerMonde(String nomMonde) {

        try {

            sendToEveryone(mineralcontest.prefixGroupe + "Chargement de la map \"" + nomMonde + "\" en cours ...");
            //this.gameWorld = worldLoader.chargerMonde(nomMonde, getIdentifiant());
            //this.gameWorld.setAutoSave(false);

            worldLoader.chargerMondeThreade(nomMonde, this);


        } catch (Exception e) {
            sendToadmin(mineralcontest.prefixErreur + " Impossible de charger le monde. Erreur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }




        /*Location worldSpawnLocation = gameWorld.getSpawnLocation();

        try {
            if (worldSpawnLocation.getX() == WorldLoader.defaultX && worldSpawnLocation.getY() == WorldLoader.defaultY && worldSpawnLocation.getZ() == WorldLoader.defaultZ)
                worldSpawnLocation = partie.getArene().getCoffre().getLocation();
        } catch (Exception e) {
            worldSpawnLocation = gameWorld.getSpawnLocation();
        }


        for (Player joueur : joueurs) {
            joueur.getInventory().clear();

            // Si le joueur est un arbitre, on lui donne le livre
            if (getGame().isReferee(joueur)) joueur.getInventory().setItemInMainHand(Referee.getRefereeItem());

                // Sinon, on lui donne le livre de selection d'équipe!
            else joueur.getInventory().setItemInMainHand(Game.getTeamSelectionItem());
            joueur.teleport(worldSpawnLocation);
            joueur.sendMessage(mineralcontest.prefixPrive + Lang.set_yourself_as_ready_to_start_game.toString());
        }

        setMapName(nomMonde);


        if (this.mapVote != null) this.mapVote.clearVotes();*/
        return true;
    }


    /**
     * Décharge un monde
     *
     * @return
     */
    public boolean dechargerMonde() {
        if (gameWorld == null) return false;

        for (Player joueur : joueurs) {
            joueur.teleport(mineralcontest.plugin.defaultSpawn);
        }


        mineralcontest.plugin.getServer().unloadWorld(gameWorld, false);

        worldLoader.supprimerMonde(gameWorld);
        return true;
    }

    public boolean isGroupLocked() {
        return groupLocked;
    }

    public void setGroupLocked(boolean groupLocked) {
        sendToadmin(mineralcontest.prefixPrive + ((groupLocked) ? Lang.group_is_now_locked.toString() : Lang.group_is_now_unlocked.toString()));
        this.groupLocked = groupLocked;
    }

    public MapVote getMapVote() {
        return this.mapVote;
    }

    public void initVoteMap() {
        this.mapVote = new MapVote();

        if (mapVote.getMaps().isEmpty()) {
            mapVote.disableVote();
            setEtat(Etats.EN_ATTENTE);
            setGroupLocked(false);
            sendToadmin(mineralcontest.prefixErreur + Lang.error_no_maps_downloaded_to_start_game.toString());
        } else {
            setEtat(Etats.VOTE_EN_COURS);
            setGroupLocked(true);
            sendToEveryone(mineralcontest.prefixGroupe + Lang.vote_started.toString());

            for (Player membreGroupe : getPlayers())
                getMapVote().getMenuVote().openInventory(membreGroupe);


        }
    }

    public void enableVote() {
        if (this.mapVote != null) mapVote.voteEnabled = true;
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
        sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_is_no_longer_a_group_admin.toString(), joueur));
        this.admins.remove(joueur);

    }


    public void inviterJoueur(Player p) {
        if (joueursInvites.contains(p)) {
            sendToadmin("ERREUR DEJA INVITE");
            return;
        }

        if (joueurs.contains(p)) {
            sendToadmin(mineralcontest.prefixErreur + Lang.translate(Lang.error_player_already_in_this_group.toString(), p));
            return;
        }

        if (mineralcontest.getPlayerGroupe(p) != null) {
            sendToadmin(mineralcontest.prefixErreur + Lang.translate(Lang.error_player_already_have_a_group.toString(), p));
            return;
        }

        p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_got_invited_to_a_group.toString(), this));
        sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_successfully_invited_to_group.toString(), p));
        this.joueursInvites.add(p);
    }

    public Game getGame() {
        return partie;
    }

    public String getNom() {
        return this.nom;
    }

    public String setNom(String nom) {
        this.nom = nom;
        return this.nom;
    }

    public boolean containsPlayer(Player p) {
        return joueurs.contains(p);
    }

    public boolean isGroupeCreateur(Player p) {
        return this.admins.getFirst().equals(p);
    }

    public boolean isAdmin(Player p) {
        return this.admins.contains(p);
    }

    public void kickPlayer(Player p) {
        this.joueurs.remove(p);
        this.admins.remove(p);
        this.joueursInvites.remove(p);
        p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_were_kicked_from_a_group.toString(), this));
        sendToEveryone(mineralcontest.prefixPrive + Lang.translate(Lang.player_got_kicked_from_group.toString(), p));
    }

    /**
     * Envoie un message aux admins
     * @param message: Message à envoyer
     */
    public void sendToadmin(String message) {
        for (Player player : admins)
            player.sendMessage(message);
    }

    public void sendToEveryone(String message) {
        for (Player p : joueurs) {
            p.sendMessage(message);
        }
    }




    public void addJoueur(Player p) {
        if (this.joueurs.contains(p)) return;

        p.setLevel(0);
        this.joueursInvites.remove(p);
        this.joueurs.add(p);
        if (mineralcontest.communityVersion)
            p.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.successfully_joined_a_group.toString(), this));
        if (mineralcontest.communityVersion)
            sendToEveryone(mineralcontest.prefixGroupe + Lang.translate(Lang.player_joined_our_group.toString(), p));

        for (Player joueur : joueurs) {
            if (!partie.isPlayerReady(joueur))
                joueur.sendMessage(mineralcontest.prefixPrive + Lang.set_yourself_as_ready_to_start_votemap.toString());
        }
    }

    public void addAdmin(Player p) {
        if (!this.joueurs.contains(p)) addJoueur(p);
        if (!this.admins.contains(p)) this.admins.add(p);
        if (mineralcontest.communityVersion)
            sendToadmin(mineralcontest.prefixPrive + Lang.translate(Lang.player_is_now_group_admin.toString(), p));
    }

    public int getPlayerCount() {
        return this.joueurs.size();
    }

    public void retirerJoueur(Player joueur) {
        /*if (isGroupeCreateur(joueur) && mineralcontest.communityVersion && partie != null && !partie.isGameStarted()) {

            sendToEveryone(mineralcontest.prefixPrive + Lang.group_got_deleted.toString());
            this.joueurs.clear();
            this.admins.clear();
            this.joueursInvites.clear();
            mineralcontest.supprimerGroupe(this);
            return;
        }*/

        this.joueurs.remove(joueur);
        this.admins.remove(joueur);
        this.joueursInvites.remove(joueur);
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.translate(Lang.you_left_the_group.toString(), this));
    }


    public LinkedList<Player> getAdmins() {
        return admins;
    }

    /**
     * Sauvegarde les membres du groupe ayant été déconnecté, avec leur position
     *
     * @param p
     */
    public void addDisconnectedPlayer(Player p) {
        /*Pair<String, Location> playerInfo = new Pair<>(getPlayerTeam(p).getNomEquipe(), p.getLocation());
        retirerJoueur(p);
        if (!havePlayerDisconnected(p)) disconnectedPlayers.put(p.getUniqueId(), playerInfo);*/

        Equipe oldPlayerTeam = getPlayerTeam(p);
        CouplePlayer oldPlayerDeathTime = partie.getArene().getDeathZone().getPlayerInfo(p);

        if (mapVote != null) getMapVote().removePlayerVote(p);
        getGame().removePlayerReady(p);

        DisconnectedPlayer joueur = new DisconnectedPlayer(p.getUniqueId(), oldPlayerTeam, this, oldPlayerDeathTime, p.getLocation(), p, getGame().getPlayerBonusManager().getListeBonusJoueur(p));
        if (!havePlayerDisconnected(p)) disconnectedPlayers.add(joueur);
        retirerJoueur(p);
    }

    public DisconnectedPlayer getDisconnectedPlayerInfo(Player p) {
        for (DisconnectedPlayer disconnectedPlayer : disconnectedPlayers)
            if (disconnectedPlayer.getPlayerUUID().equals(p.getUniqueId())) return disconnectedPlayer;
        return null;
    }


    /**
     * Reconnecte un joueur, on le re TP dans sa position initiale, et dans son équipe
     *
     * @param p
     */
    public void playerHaveReconnected(Player p) {
        if (havePlayerDisconnected(p)) {

            DisconnectedPlayer infoJoueur = this.getDisconnectedPlayerInfo(p);

            try {
                // On traite maintenant les différent les informations

                // On remet le joueur dans son groupe
                if (infoJoueur.getOldPlayerGroupe() != null) {
                    if (p.isOp()) infoJoueur.getOldPlayerGroupe().addAdmin(p);
                    else infoJoueur.getOldPlayerGroupe().addJoueur(p);

                    // On remet le joueur dans son équipe
                    if (infoJoueur.getOldPlayerTeam() != null)
                        infoJoueur.getOldPlayerTeam().addPlayerToTeam(p, false);

                    // Si le joueur était mort, on le remets
                    if (infoJoueur.wasPlayerDead()) {
                        CouplePlayer deathTime = new CouplePlayer(p, infoJoueur.getOldPlayerDeathTime().getValeur());
                        Groupe playerGroup = infoJoueur.getOldPlayerGroupe();
                        if (playerGroup.getGame() != null)
                            if (playerGroup.getGame().getArene() != null)
                                if (playerGroup.getGame().getArene().getDeathZone() != null)
                                    playerGroup.getGame().getArene().getDeathZone().add(deathTime);

                    }

                    p.getInventory().clear();
                    for (ItemStack item : infoJoueur.getOldPlayerInventory())
                        p.getInventory().addItem(item);

                    p.teleport(infoJoueur.getOldPlayerLocation());

                    PlayerBonus playerBonusManager = getGame().getPlayerBonusManager();
                    playerBonusManager.setPlayerBonusList(p, infoJoueur.getBonus());

                    playerBonusManager.triggerEnabledBonusOnReconnect(p);

                    // On le supprime de la liste des déco
                    disconnectedPlayers.remove(infoJoueur);

                    // SI il n'y a plus personne dans la liste, on relance la partie!
                    //if (disconnectedPlayers.isEmpty()) partie.resumeGame();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


            /*Pair<String, Location> playerInfo = disconnectedPlayers.get(p.getUniqueId());

            Equipe playerTeam = partie.getHouseFromName(playerInfo.getKey()).getTeam();
            Location playerLocation = playerInfo.getValue();
            p.setFlying(false);
            // Si la team n'est pas nulle, on le remet dans son équipe
            if (playerTeam != null) {
                try {
                    playerTeam.addPlayerToTeam(p, true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (p.isOp()) {
                    partie.addReferee(p);
                    return;
                }
            }*/

            // On téléport le joueur à sa dernière position



    }

    /**
     * Retourne VRAI si le joueur s'était déjà déconnecté
     *
     * @param p
     */
    public boolean havePlayerDisconnected(Player p) {

        for (DisconnectedPlayer disconnectedPlayer : disconnectedPlayers)
            if (disconnectedPlayer.getPlayerUUID().equals(p.getUniqueId())) return true;
        return false;
    }


}

