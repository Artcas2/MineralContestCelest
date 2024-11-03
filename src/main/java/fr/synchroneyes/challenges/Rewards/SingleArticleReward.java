package fr.synchroneyes.challenges.Rewards;

import fr.synchroneyes.challenges.Rewards.AbstractReward;
import fr.synchroneyes.mineral.Core.MCPlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class SingleArticleReward extends AbstractReward {
    private ItemStack item;

    public SingleArticleReward(ItemStack itemToReward) {
        this.item = itemToReward;
    }

    @Override
    public void giveToPlayer() {
        World playerWorld = this.getJoueur().getWorld();
        MCPlayer mcPlayer = this.getMcPlayer();
        if (this.getMcPlayer().isInventoryFull()) {
            playerWorld.dropItemNaturally(this.getJoueur().getLocation(), this.item);
        } else {
            mcPlayer.getJoueur().getInventory().addItem(new ItemStack[]{this.item});
        }
    }

    @Override
    public String getRewardText() {
        return "Vous avez re\u00e7u une r\u00e9compense dans votre inventaire! Si votre inventaire est plein, l'objet a \u00e9t\u00e9 d\u00e9pos\u00e9 \u00e0 c\u00f4t\u00e9 de vous.";
    }
}

