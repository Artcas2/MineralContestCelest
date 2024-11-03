package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Core.Coffre.AutomatedChestAnimation;
import org.bukkit.entity.Player;

public class MCAutomatedChestTimeOverEvent extends MCEvent {
    private AutomatedChestAnimation automatedChest;
    private Player openingPlayer;

    public MCAutomatedChestTimeOverEvent(AutomatedChestAnimation automatedChestAnimation, Player openingPlayer) {
        this.automatedChest = automatedChestAnimation;
        this.openingPlayer = openingPlayer;
    }

    public AutomatedChestAnimation getAutomatedChest() {
        return this.automatedChest;
    }

    public Player getOpeningPlayer() {
        return this.openingPlayer;
    }
}

