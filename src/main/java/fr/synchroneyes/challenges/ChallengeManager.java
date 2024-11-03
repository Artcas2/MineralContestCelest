package fr.synchroneyes.challenges;

import fr.synchroneyes.challenges.Availables.AbstractChallenge;
import fr.synchroneyes.challenges.Availables.AbstractRepeatableChallenge;
import fr.synchroneyes.challenges.Availables.PoserBlockTest;
import fr.synchroneyes.challenges.Availables.TuerZombie;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Utils.Pair;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ChallengeManager {
    private List<AbstractChallenge> liste_defis;
    private int CHALLENGE_COMPLETED = -1;
    private HashMap<MCPlayer, List<Pair<AbstractChallenge, Integer>>> defis_par_joueur;
    private Game game;

    public ChallengeManager(Game game) {
        this.game = game;
        this.liste_defis = new ArrayList<AbstractChallenge>();
        this.defis_par_joueur = new HashMap();
        this.initAchievements();
    }

    private void initAchievements() {
        this.liste_defis.add(new PoserBlockTest(this));
        this.liste_defis.add(new TuerZombie(this));
    }

    public List<AbstractChallenge> getListe_defis() {
        return this.liste_defis;
    }

    public AbstractChallenge getAchievement(Class clazz) {
        for (AbstractChallenge achievement : this.liste_defis) {
            if (!achievement.getClass().getName().equals(clazz.getName())) continue;
            return achievement;
        }
        return null;
    }

    public void addPlayerAchievement(MCPlayer joueur, AbstractChallenge abstractChallenge) {
        List defis_joueur = this.defis_par_joueur.computeIfAbsent(joueur, k -> new LinkedList());
        defis_joueur.add(new Pair<AbstractChallenge, Integer>(abstractChallenge, 0));
        this.defis_par_joueur.replace(joueur, defis_joueur);
        joueur.sendPrivateMessage("Vous avez un nouveau d\u00e9fi: " + abstractChallenge.getNom());
        joueur.sendPrivateMessage(abstractChallenge.getObjectifTexte());
    }

    public boolean doesPlayerHaveThisAchievement(MCPlayer mcPlayer, AbstractChallenge abstractChallenge) {
        if (this.defis_par_joueur.get(mcPlayer) == null) {
            return false;
        }
        for (Pair<AbstractChallenge, Integer> defis : this.defis_par_joueur.get(mcPlayer)) {
            if (!defis.getKey().getNom().equals(abstractChallenge.getNom()) || defis.getValue() == this.CHALLENGE_COMPLETED) continue;
            return true;
        }
        return false;
    }

    public void playerDidAchievement(MCPlayer player, AbstractChallenge abstractChallenge) {
        List<Pair<AbstractChallenge, Integer>> defis_joueur = this.defis_par_joueur.get(player);
        if (defis_joueur == null) {
            return;
        }
        for (Pair<AbstractChallenge, Integer> defis : this.defis_par_joueur.get(player)) {
            AbstractChallenge challenge = defis.getKey();
            if (!this.doesPlayerHaveThisAchievement(player, challenge)) continue;
            if (challenge instanceof AbstractRepeatableChallenge) {
                AbstractRepeatableChallenge abstractRepeatableChallenge = (AbstractRepeatableChallenge)challenge;
                int nb_realisation = defis.getValue() + 1;
                if (abstractRepeatableChallenge.repetitionNeeded() == nb_realisation) {
                    defis.setValue(this.CHALLENGE_COMPLETED);
                    challenge.setAchievementCompleted(player);
                } else {
                    defis.setValue(nb_realisation);
                }
                return;
            }
            defis.setValue(this.CHALLENGE_COMPLETED);
            challenge.setAchievementCompleted(player);
            return;
        }
    }

    public void unloadAchievementManager() {
        for (AbstractChallenge achievement : this.liste_defis) {
            HandlerList.unregisterAll((Listener)achievement);
        }
    }

    public void init() {
        for (AbstractChallenge achievement : this.liste_defis) {
            Bukkit.getLogger().info("Registered achievement: " + achievement.getNom());
            Bukkit.getPluginManager().registerEvents((Listener)achievement, (Plugin)mineralcontest.plugin);
        }
        for (Player joueur : this.game.groupe.getPlayers()) {
            MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
            this.addPlayerAchievement(mcPlayer, this.getAchievement(PoserBlockTest.class));
            this.addPlayerAchievement(mcPlayer, this.getAchievement(TuerZombie.class));
        }
    }
}

