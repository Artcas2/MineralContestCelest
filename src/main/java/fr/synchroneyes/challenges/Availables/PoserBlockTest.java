package fr.synchroneyes.challenges.Availables;

import fr.synchroneyes.challenges.Availables.AbstractChallenge;
import fr.synchroneyes.challenges.ChallengeManager;
import fr.synchroneyes.challenges.Rewards.AbstractReward;
import fr.synchroneyes.challenges.Rewards.SingleArticleReward;
import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PoserBlockTest extends AbstractChallenge {
    public PoserBlockTest(ChallengeManager manager) {
        super(manager);
    }

    @Override
    public String getNom() {
        return "vive l'\u00e9cologie";
    }

    @Override
    public String getObjectifTexte() {
        return "Posez un bloc de bouse!";
    }

    @Override
    public AbstractReward getReward() {
        return new SingleArticleReward(new ItemStack(Material.DIAMOND_SWORD));
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        MCPlayer mcPlayer = mineralcontest.plugin.getMCPlayer(event.getPlayer());
        if (mcPlayer == null) {
            return;
        }
        if (this.getManager().doesPlayerHaveThisAchievement(mcPlayer, this) && event.getBlock().getType() == Material.DIRT) {
            this.getManager().playerDidAchievement(mcPlayer, this);
        }
    }
}

