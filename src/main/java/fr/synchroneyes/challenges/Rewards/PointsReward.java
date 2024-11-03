package fr.synchroneyes.challenges.Rewards;

import fr.synchroneyes.challenges.Rewards.AbstractReward;
import fr.synchroneyes.mineral.Teams.Equipe;

public class PointsReward extends AbstractReward {
    private int points;

    public PointsReward(int points) {
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public void giveToPlayer() {
        Equipe equipe = this.getMcPlayer().getEquipe();
        if (equipe == null) {
            return;
        }
        int old_score = equipe.getScore();
        equipe.setScore(old_score += this.getPoints());
    }

    @Override
    public String getRewardText() {
        return "Vous avez re\u00e7u " + this.points + " points de r\u00e9compense.";
    }
}

