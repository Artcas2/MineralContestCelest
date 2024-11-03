package fr.synchroneyes.challenges.Rewards;

import fr.synchroneyes.mineral.Core.MCPlayer;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class AbstractReward {
    private Player joueur;
    private MCPlayer mcPlayer;

    protected abstract void giveToPlayer();

    public abstract String getRewardText();

    public Player getJoueur() {
        return this.joueur;
    }

    public void setJoueur(Player joueur) {
        this.joueur = joueur;
        this.mcPlayer = mineralcontest.plugin.getMCPlayer(joueur);
    }

    public MCPlayer getMcPlayer() {
        return this.mcPlayer;
    }

    public void rewardPlayer() {
        this.getJoueur().sendMessage(mineralcontest.prefixPrive + ChatColor.GREEN + this.getRewardText());
        this.giveToPlayer();
    }
}

