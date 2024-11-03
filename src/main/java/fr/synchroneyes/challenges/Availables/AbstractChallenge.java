package fr.synchroneyes.challenges.Availables;

import fr.synchroneyes.challenges.ChallengeManager;
import fr.synchroneyes.challenges.Rewards.AbstractReward;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.event.Listener;

public abstract class AbstractChallenge implements Listener {
    private MCPlayer player;
    private ChallengeManager manager;

    public AbstractChallenge(ChallengeManager manager) {
        this.manager = manager;
    }

    public abstract String getNom();

    public abstract String getObjectifTexte();

    public abstract AbstractReward getReward();

    public void setAchievementCompleted(MCPlayer mcPlayer) {
        AbstractReward abstractReward = this.getReward();
        abstractReward.setJoueur(mcPlayer.getJoueur());
        abstractReward.rewardPlayer();
    }

    public MCPlayer getPlayer() {
        return this.player;
    }

    public void setPlayer(MCPlayer player) {
        this.player = player;
    }

    public ChallengeManager getManager() {
        return this.manager;
    }
}

