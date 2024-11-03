package fr.synchroneyes.mineral.Shop.Players;

import fr.synchroneyes.custom_events.MCShopItemPurchaseEvent;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ConsumableItem;
import fr.synchroneyes.mineral.Shop.Items.Abstract.LevelableItem;
import fr.synchroneyes.mineral.Shop.Items.Abstract.PermanentItem;
import fr.synchroneyes.mineral.Shop.Items.Abstract.ShopItem;
import fr.synchroneyes.mineral.Shop.Items.Informations.ProchainCoffreAreneItem;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerBonus {
    public static LinkedBlockingQueue<ShopItem> listeBonusActif;
    public Map<Player, LinkedBlockingQueue<ShopItem>> bonus_par_joueur;
    private Game partie;

    public PlayerBonus(Game g) {
        if (listeBonusActif == null) {
            this.enregistrerBonus();
        }
        this.bonus_par_joueur = new HashMap<Player, LinkedBlockingQueue<ShopItem>>();
        this.partie = g;
    }

    private void enregistrerBonus() {
        if (listeBonusActif == null) {
            listeBonusActif = new LinkedBlockingQueue();
        }
        listeBonusActif.add(new ProchainCoffreAreneItem());
    }

    public void setPlayerBonusList(Player joueur, LinkedBlockingQueue liste) {
        if (liste == null) {
            return;
        }
        for (Object bonus_achete : liste) {
            ((ShopItem)bonus_achete).setJoueur(joueur);
        }
        if (this.bonus_par_joueur.containsKey(joueur)) {
            this.bonus_par_joueur.replace(joueur, liste);
        } else {
            this.bonus_par_joueur.put(joueur, liste);
        }
    }

    public LinkedBlockingQueue<ShopItem> getListeBonusJoueur(Player joueur) {
        return this.bonus_par_joueur.get(joueur);
    }

    public void ajouterBonusPourJoueur(ShopItem bonus, Player joueur) {
        if (!this.bonus_par_joueur.containsKey(joueur)) {
            this.bonus_par_joueur.put(joueur, new LinkedBlockingQueue());
        }
        LinkedBlockingQueue<ShopItem> liste_bonus_joueur = this.bonus_par_joueur.get(joueur);
        boolean doesPlayerAlreadyHaveBonus = false;
        ShopItem currentBonus = null;
        for (ShopItem bonus_joueur : liste_bonus_joueur) {
            if (!bonus_joueur.getClass().equals(bonus.getClass())) continue;
            doesPlayerAlreadyHaveBonus = true;
            currentBonus = bonus_joueur;
            break;
        }
        if (currentBonus != null && PlayerBonus.isConsummableBonus(currentBonus)) {
            ConsumableItem currentBonus_consommable = (ConsumableItem)currentBonus;
            int nb_use_actuel = currentBonus_consommable.getNombreUtilisationRestantes();
            liste_bonus_joueur.remove(currentBonus);
            currentBonus_consommable.setNombreUtilisationRestantes(nb_use_actuel + 1);
            bonus = currentBonus_consommable;
            doesPlayerAlreadyHaveBonus = false;
        }
        if (!doesPlayerAlreadyHaveBonus) {
            if (PlayerBonus.isConsummableBonus(bonus) && currentBonus == null) {
                ((ConsumableItem)bonus).setNombreUtilisationRestantes(bonus.getNombreUtilisations());
            }
            liste_bonus_joueur.add(bonus);
        }
        if (PlayerBonus.isLevelableBonus(bonus)) {
            Class classe_requise = ((LevelableItem)bonus).getRequiredLevel();
            if (classe_requise != null && !this.doesPlayerHaveThisBonus(classe_requise, joueur)) {
                String bonus_required_text = Lang.shopitem_bonus_required.toString();
                bonus_required_text = bonus_required_text.replace("%bonus", classe_requise.getName());
                joueur.sendMessage(mineralcontest.prefixErreur + bonus_required_text);
            } else {
                for (ShopItem shopItem : liste_bonus_joueur) {
                    if (!shopItem.getClass().equals(classe_requise)) continue;
                    LevelableItem item = (LevelableItem)shopItem;
                    item.setPlayerBonusEnabled(false);
                    break;
                }
            }
        }
        if (bonus.isEnabledOnPurchase()) {
            bonus.onItemUse();
        }
        this.bonus_par_joueur.replace(joueur, liste_bonus_joueur);
    }

    public void purchaseItem(Player joueur, ShopItem item) {
        if (this.doesPlayerHaveThisBonus(item.getClass(), joueur) && (PlayerBonus.isPermanentBonus(item) || PlayerBonus.isLevelableBonus(item))) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.shopitem_bonus_already_purchased.toString());
            return;
        }
        joueur.sendMessage(mineralcontest.prefixPrive + Lang.translate(item.getPurchaseText()));
        item.setJoueur(joueur);
        this.takePlayerMoney(joueur, item);
        MCShopItemPurchaseEvent purchaseEvent = new MCShopItemPurchaseEvent(item, joueur);
        Bukkit.getPluginManager().callEvent((Event)purchaseEvent);
        Equipe playerTeam = mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur);
        String purchaseMessage = Lang.shopitem_player_purchased.toString();
        purchaseMessage = purchaseMessage.replace("%p", joueur.getDisplayName());
        purchaseMessage = purchaseMessage.replace("%bonus", item.getNomItem());
        playerTeam.sendMessage(mineralcontest.prefixTeamChat + purchaseMessage);
        this.ajouterBonusPourJoueur(item, joueur);
        joueur.playNote(joueur.getLocation(), Instrument.PIANO, new Note(24));
    }

    public boolean canPlayerAffordItem(ShopItem bonus, Player joueur) {
        Game playerGame = mineralcontest.getPlayerGame(joueur);
        if (playerGame == null) {
            return false;
        }
        Equipe playerTeam = playerGame.getPlayerTeam(joueur);
        if (playerTeam == null) {
            return false;
        }
        int scoreEquipe = playerTeam.getScore();
        if (playerGame.groupe.getKitManager().isKitsEnabled() && !bonus.isBonusCompatibleWithKits()) {
            return false;
        }
        if (PlayerBonus.isLevelableBonus(bonus)) {
            LevelableItem levelableItem = (LevelableItem)bonus;
            if (levelableItem.getRequiredLevel() == null) {
                return scoreEquipe >= bonus.getPrice();
            }
            return this.doesPlayerHaveThisBonus(levelableItem.getRequiredLevel(), joueur) && scoreEquipe >= bonus.getPrice();
        }
        return scoreEquipe >= bonus.getPrice();
    }

    private void takePlayerMoney(Player joueur, ShopItem shopItem) {
        Equipe playerTeam = mineralcontest.getPlayerGame(joueur).getPlayerTeam(joueur);
        int nouveauScoreEquipe = playerTeam.getScore() - shopItem.getPrice();
        playerTeam.setScore(nouveauScoreEquipe);
    }

    public void triggerEnabledBonusOnRespawn(Player joueur) {
        LinkedBlockingQueue<ShopItem> bonus_joueur = this.bonus_par_joueur.get(joueur);
        if (bonus_joueur == null) {
            return;
        }
        for (ShopItem bonus : bonus_joueur) {
            ConsumableItem bonus_consummable;
            if (PlayerBonus.isConsummableBonus(bonus) && bonus.isEnabledOnRespawn() && (bonus_consummable = (ConsumableItem)bonus).getNombreUtilisationRestantes() > 0) {
                bonus_consummable.onItemUse();
                bonus_consummable.setNombreUtilisationRestantes(bonus_consummable.getNombreUtilisations() - 1);
                continue;
            }
            if (!bonus.isEnabledOnRespawn()) continue;
            bonus.onItemUse();
        }
    }

    public void triggerEnabledBonusOnReconnect(Player joueur) {
        LinkedBlockingQueue<ShopItem> bonus_joueur = this.bonus_par_joueur.get(joueur);
        if (bonus_joueur == null) {
            return;
        }
        for (ShopItem bonus : bonus_joueur) {
            ConsumableItem bonus_consummable;
            if (PlayerBonus.isConsummableBonus(bonus) && bonus.isEnabledOnReconnect() && (bonus_consummable = (ConsumableItem)bonus).getNombreUtilisationRestantes() > 0) {
                bonus_consummable.onItemUse();
                bonus_consummable.setNombreUtilisationRestantes(bonus_consummable.getNombreUtilisations() - 1);
                continue;
            }
            if (!bonus.isEnabledOnReconnect()) continue;
            bonus.onItemUse();
        }
    }

    public void triggerEnabledBonusOnPlayerKillerKilled(Player joueur) {
        LinkedBlockingQueue<ShopItem> bonus_joueur = this.bonus_par_joueur.get(joueur);
        if (bonus_joueur == null) {
            return;
        }
        int index = -1;
        for (ShopItem bonus : bonus_joueur) {
            ++index;
            if (PlayerBonus.isConsummableBonus(bonus) && bonus.isEnabledOnDeathByAnotherPlayer()) {
                ConsumableItem bonus_consummable = (ConsumableItem)bonus;
                joueur.sendMessage(bonus_consummable.getNombreUtilisationRestantes() + " <");
                if (bonus_consummable.getNombreUtilisationRestantes() > 0) {
                    int nombre_utilisation_restantes = bonus_consummable.getNombreUtilisationRestantes();
                    bonus_consummable.onItemUse();
                    bonus_consummable.setNombreUtilisationRestantes(nombre_utilisation_restantes - 1);
                    continue;
                }
                bonus_joueur.remove(bonus);
                continue;
            }
            if (!bonus.isEnabledOnDeathByAnotherPlayer()) continue;
            bonus.onItemUse();
        }
        this.bonus_par_joueur.replace(joueur, bonus_joueur);
    }

    public static boolean isConsummableBonus(ShopItem c) {
        return c instanceof ConsumableItem;
    }

    public static boolean isPermanentBonus(ShopItem c) {
        return c instanceof PermanentItem;
    }

    public static boolean isLevelableBonus(ShopItem c) {
        return c instanceof LevelableItem;
    }

    public boolean doesPlayerHaveThisBonus(Class c, Player joueur) {
        if (!this.bonus_par_joueur.containsKey(joueur)) {
            return false;
        }
        LinkedBlockingQueue<ShopItem> liste_bonus = this.bonus_par_joueur.get(joueur);
        if (liste_bonus == null) {
            return false;
        }
        for (ShopItem bonus : liste_bonus) {
            if (!bonus.getClass().equals(c)) continue;
            return true;
        }
        return false;
    }

    public static ShopItem getPlayerBonus(Class bonusClass, Player joueur) {
        Game partie = mineralcontest.getPlayerGame(joueur);
        if (partie == null) {
            return null;
        }
        LinkedBlockingQueue<ShopItem> bonus_joueur = partie.getPlayerBonusManager().getListeBonusJoueur(joueur);
        if (bonus_joueur == null || bonus_joueur.isEmpty()) {
            return null;
        }
        for (ShopItem bonus : bonus_joueur) {
            if (!bonus.getClass().equals(bonusClass)) continue;
            return bonus;
        }
        return null;
    }

    public Game getPartie() {
        return this.partie;
    }
}

