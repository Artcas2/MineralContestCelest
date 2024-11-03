package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Teams.Equipe;
import org.bukkit.event.Cancellable;

public class MCTeamScoreUpdated extends MCEvent implements Cancellable {
    private int oldScore;
    private int newScore;
    private Equipe equipe;
    private boolean cancelled = false;

    public MCTeamScoreUpdated(int oldScore, int newScore, Equipe equipe) {
        this.oldScore = oldScore;
        this.newScore = newScore;
        this.equipe = equipe;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public int getOldScore() {
        return this.oldScore;
    }

    public int getNewScore() {
        return this.newScore;
    }

    public Equipe getEquipe() {
        return this.equipe;
    }
}

