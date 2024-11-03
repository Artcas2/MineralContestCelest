package fr.synchroneyes.challenges.Rewards;

import fr.synchroneyes.challenges.Rewards.AbstractReward;
import fr.synchroneyes.mineral.Core.MCPlayer;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class MultipleArticleReward extends AbstractReward {
    private List<ItemStack> items = new LinkedList<ItemStack>();

    public void addItem(ItemStack itemStack) {
        this.items.add(itemStack);
    }

    @Override
    public void giveToPlayer() {
        World playerWorld = this.getJoueur().getWorld();
        MCPlayer mcPlayer = this.getMcPlayer();
        for (ItemStack item : this.items) {
            if (this.getMcPlayer().isInventoryFull()) {
                playerWorld.dropItemNaturally(this.getJoueur().getLocation(), item);
                continue;
            }
            mcPlayer.getJoueur().getInventory().addItem(new ItemStack[]{item});
        }
    }

    @Override
    public String getRewardText() {
        return "Vous avez re\u00e7u une r\u00e9compense dans votre inventaire! Si votre inventaire est plein, les objets ont \u00e9t\u00e9 d\u00e9pos\u00e9s \u00e0 c\u00f4t\u00e9 de vous.";
    }
}

