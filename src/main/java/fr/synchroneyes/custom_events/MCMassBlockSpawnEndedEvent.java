package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Utils.MassBlockSpawner;

public class MCMassBlockSpawnEndedEvent extends MCEvent {
    private MassBlockSpawner spawner;

    public MCMassBlockSpawnEndedEvent(MassBlockSpawner spawner) {
        this.spawner = spawner;
    }

    public MassBlockSpawner getSpawner() {
        return this.spawner;
    }
}

