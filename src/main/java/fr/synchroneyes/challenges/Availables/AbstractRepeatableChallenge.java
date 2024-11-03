package fr.synchroneyes.challenges.Availables;

import fr.synchroneyes.challenges.Availables.AbstractChallenge;
import fr.synchroneyes.challenges.ChallengeManager;

public abstract class AbstractRepeatableChallenge extends AbstractChallenge {
    public AbstractRepeatableChallenge(ChallengeManager manager) {
        super(manager);
    }

    public abstract int repetitionNeeded();
}

