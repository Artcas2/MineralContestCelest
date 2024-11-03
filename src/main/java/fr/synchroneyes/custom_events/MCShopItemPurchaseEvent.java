package fr.synchroneyes.custom_events;

import fr.synchroneyes.custom_events.MCEvent;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import org.bukkit.entity.Player;

public class MCShopItemPurchaseEvent extends MCEvent {
    private Player joueur;
    private ShopItem item;

    public MCShopItemPurchaseEvent(ShopItem item, Player acheteur) {
        this.item = item;
        this.joueur = acheteur;
    }

    public Player getJoueur() {
        return this.joueur;
    }

    public ShopItem getItem() {
        return this.item;
    }
}

