package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Teams.Equipe;
import org.bukkit.event.Cancellable;

public class MCPlayerLeaveTeamEvent extends MCEvent implements Cancellable {
    private boolean cancelled;
    private final MCPlayer mcPlayer;
    private final Equipe oldTeam;

    public MCPlayerLeaveTeamEvent(MCPlayer mcPlayer, Equipe joinedTeam) {
        this.mcPlayer = mcPlayer;
        this.oldTeam = joinedTeam;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = true;
    }

    public MCPlayer getMcPlayer() {
        return this.mcPlayer;
    }

    public Equipe getOldTeam() {
        return this.oldTeam;
    }
}

