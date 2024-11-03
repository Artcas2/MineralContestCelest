package fr.synchroneyes.mineral.Shop.Items.Abstract;

import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;

public abstract class ConsumableItem extends ShopItem {
    private int nombreUtilisationRestantes = this.getNombreUtilisations();

    @Override
    public String getPurchaseText() {
        return "Vous avez achet\u00e9 " + this.getNombreUtilisations() + "x " + this.getNomItem();
    }

    @Override
    public void onPlayerBonusAdded() {
        this.onItemUse();
    }

    @Override
    public boolean isEnabledOnDeath() {
        return false;
    }

    @Override
    public boolean isEnabledOnReconnect() {
        return true;
    }

    @Override
    public boolean isEnabledOnDeathByAnotherPlayer() {
        return false;
    }

    public int getNombreUtilisationRestantes() {
        return this.nombreUtilisationRestantes;
    }

    public void setNombreUtilisationRestantes(int nombreUtilisationRestantes) {
        this.nombreUtilisationRestantes = nombreUtilisationRestantes;
    }
}

