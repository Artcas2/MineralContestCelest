package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.Teams.Equipe;
import org.bukkit.event.Cancellable;

public class MCPlayerJoinTeamEvent extends MCEvent implements Cancellable {
    private boolean cancelled;
    private final MCPlayer mcPlayer;
    private final Equipe joinedTeam;

    public MCPlayerJoinTeamEvent(MCPlayer mcPlayer, Equipe joinedTeam) {
        this.mcPlayer = mcPlayer;
        this.joinedTeam = joinedTeam;
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

    public Equipe getJoinedTeam() {
        return this.joinedTeam;
    }
}

