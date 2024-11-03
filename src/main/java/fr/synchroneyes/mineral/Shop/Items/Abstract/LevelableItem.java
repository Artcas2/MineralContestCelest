package fr.synchroneyes.mineral.Shop.Items.Abstract;

import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Shop.Players.PlayerBonus;
import fr.synchroneyes.mineral.mineralcontest;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class LevelableItem extends ShopItem {
    private boolean playerBonusEnabled = true;

    public abstract Class getRequiredLevel();

    public static LevelableItem fromClass(Class c) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (LevelableItem)c.getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    @Override
    public boolean isEnabledOnPurchase() {
        return true;
    }

    @Override
    public String getPurchaseText() {
        return "Vous avez achet\u00e9 l'am\u00e9lioration: " + this.getNomItem();
    }

    @Override
    public void onPlayerBonusAdded() {
        this.onItemUse();
    }

    @Override
    public boolean isEnabledOnReconnect() {
        return false;
    }

    public void onLevelAdded() {
        Game partie = mineralcontest.getPlayerGame(this.joueur);
        if (partie == null) {
            return;
        }
        PlayerBonus playerBonusManager = partie.getPlayerBonusManager();
        if (playerBonusManager.doesPlayerHaveThisBonus(this.getRequiredLevel(), this.joueur)) {
            LinkedBlockingQueue<ShopItem> bonus_joueur = playerBonusManager.getListeBonusJoueur(this.joueur);
            if (bonus_joueur == null) {
                return;
            }
            for (ShopItem bonus : bonus_joueur) {
                LevelableItem bonus_;
                if (!bonus.getClass().equals(this.getRequiredLevel()) || !PlayerBonus.isLevelableBonus(bonus) || !(bonus_ = (LevelableItem)bonus).isPlayerBonusEnabled()) continue;
                bonus_.setPlayerBonusEnabled(false);
                return;
            }
        }
    }

    public boolean canPlayerUseThisLevel() {
        Game playerGame = mineralcontest.getPlayerGame(this.joueur);
        if (playerGame == null) {
            return false;
        }
        PlayerBonus bonusManager = playerGame.getPlayerBonusManager();
        LinkedBlockingQueue<ShopItem> bonus_joueur = bonusManager.getListeBonusJoueur(this.joueur);
        if (bonus_joueur.isEmpty()) {
            return false;
        }
        if (!bonusManager.doesPlayerHaveThisBonus(this.getClass(), this.joueur)) {
            return false;
        }
        for (ShopItem bonus : bonus_joueur) {
            if (!bonus.getClass().equals(this.getClass())) continue;
            LevelableItem bonus_ = (LevelableItem)bonus;
            return bonus_.isPlayerBonusEnabled();
        }
        return false;
    }

    public boolean isPlayerBonusEnabled() {
        return this.playerBonusEnabled;
    }

    public void setPlayerBonusEnabled(boolean playerBonusEnabled) {
        this.playerBonusEnabled = playerBonusEnabled;
    }
}

