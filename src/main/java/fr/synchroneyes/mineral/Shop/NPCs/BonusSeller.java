package fr.synchroneyes.mineral.Shop.NPCs;

import fr.synchroneyes.mineral.Shop.Categories.Abstract.Category;
import fr.synchroneyes.mineral.Shop.Categories.Ameliorations;
import fr.synchroneyes.mineral.Shop.Categories.BonusEquipe;
import fr.synchroneyes.mineral.Shop.Categories.BonusPermanent;
import fr.synchroneyes.mineral.Shop.Categories.BonusPersonnel;
import fr.synchroneyes.mineral.Shop.Categories.Informations;
import fr.synchroneyes.mineral.Shop.Categories.Items;
import fr.synchroneyes.mineral.Shop.Categories.Potions;
import fr.synchroneyes.mineral.Shop.Items.AmeliorationTemporaire.AjouterVieSupplementaire;
import fr.synchroneyes.mineral.Shop.Items.AmeliorationTemporaire.DerniereChance;
import fr.synchroneyes.mineral.Shop.Items.AmeliorationTemporaire.PotionExperience;
import fr.synchroneyes.mineral.Shop.Items.Equipe.ActiverAnnonceProchainCoffre;
import fr.synchroneyes.mineral.Shop.Items.Equipe.SingleAreneTeleport;
import fr.synchroneyes.mineral.Shop.Items.Equipe.TeleportEquipeAreneAuto;
import fr.synchroneyes.mineral.Shop.Items.Informations.ProchainCoffreAreneItem;
import fr.synchroneyes.mineral.Shop.Items.Informations.ProchainLargageAerienPosition;
import fr.synchroneyes.mineral.Shop.Items.Informations.ProchainLargageAerienTemps;
import fr.synchroneyes.mineral.Shop.Items.Items.BatonKnockback;
import fr.synchroneyes.mineral.Shop.Items.Items.BouleDeFeu;
import fr.synchroneyes.mineral.Shop.Items.Items.Boussole;
import fr.synchroneyes.mineral.Shop.Items.Items.Buche;
import fr.synchroneyes.mineral.Shop.Items.Items.PommeDoree;
import fr.synchroneyes.mineral.Shop.Items.Items.SceauDeau;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche1;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche2;
import fr.synchroneyes.mineral.Shop.Items.Levelable.Pioche.Pioche3;
import fr.synchroneyes.mineral.Shop.Items.Permanent.AjoutCoeursPermanent;
import fr.synchroneyes.mineral.Shop.Items.Permanent.AutoLingot;
import fr.synchroneyes.mineral.Shop.Items.Permanent.EpeeDiamant;
import fr.synchroneyes.mineral.Shop.Items.Potions.PotionHaste;
import fr.synchroneyes.mineral.Shop.Items.Potions.PotionInvisibilite;
import fr.synchroneyes.mineral.Shop.Items.Potions.PotionSpeed1;
import fr.synchroneyes.mineral.Shop.Items.Potions.PotionSpeed2;
import fr.synchroneyes.mineral.Shop.NPCs.NPCTemplate;
import fr.synchroneyes.mineral.Translation.Lang;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BonusSeller extends NPCTemplate {
    private List<Category> categories_dispo = new LinkedList<Category>();

    public BonusSeller(Location position) {
        super(4);
        this.setEmplacement(position);
        Informations informations = new Informations(this);
        informations.addItemToInventory(new ProchainLargageAerienPosition(), 0);
        informations.addItemToInventory(new ProchainLargageAerienTemps(), 1);
        informations.addItemToInventory(new ProchainCoffreAreneItem(), 2);
        Items items = new Items(this);
        items.addItemToInventory(new BatonKnockback(), 0);
        items.addItemToInventory(new BouleDeFeu(), 1);
        items.addItemToInventory(new Boussole(), 2);
        items.addItemToInventory(new Buche(), 3);
        items.addItemToInventory(new PommeDoree(), 4);
        items.addItemToInventory(new SceauDeau(), 5);
        Potions potions = new Potions(this);
        potions.addItemToInventory(new PotionHaste(), 0);
        potions.addItemToInventory(new PotionInvisibilite(), 1);
        potions.addItemToInventory(new PotionSpeed1(), 2);
        potions.addItemToInventory(new PotionSpeed2(), 3);
        BonusPermanent bonusPermanent = new BonusPermanent(this);
        bonusPermanent.addItemToInventory(new AjoutCoeursPermanent(), 0);
        bonusPermanent.addItemToInventory(new AutoLingot(), 1);
        bonusPermanent.addItemToInventory(new EpeeDiamant(), 2);
        BonusEquipe bonusEquipe = new BonusEquipe(this);
        bonusEquipe.addItemToInventory(new ActiverAnnonceProchainCoffre(), 0);
        bonusEquipe.addItemToInventory(new SingleAreneTeleport(), 1);
        bonusEquipe.addItemToInventory(new TeleportEquipeAreneAuto(), 2);
        BonusPersonnel bonusPersonnel = new BonusPersonnel(this);
        bonusPersonnel.addItemToInventory(new AjouterVieSupplementaire(), 0);
        bonusPersonnel.addItemToInventory(new DerniereChance(), 2);
        bonusPersonnel.addItemToInventory(new PotionExperience(), 3);
        Ameliorations ameliorations = new Ameliorations(this);
        ameliorations.addItemToInventory(new Pioche1(), 0);
        ameliorations.addItemToInventory(new Pioche2(), 1);
        ameliorations.addItemToInventory(new Pioche3(), 3);
        this.categories_dispo.add(informations);
        this.categories_dispo.add(items);
        this.categories_dispo.add(potions);
        this.categories_dispo.add(bonusPermanent);
        this.categories_dispo.add(bonusEquipe);
        this.categories_dispo.add(bonusPersonnel);
        this.categories_dispo.add(ameliorations);
    }

    @Override
    public String getNomAffichage() {
        return Lang.shopitem_npc_title.toString();
    }

    @Override
    public Villager.Profession getNPCType() {
        return null;
    }

    @Override
    public void onNPCRightClick(Player joueur) {
        joueur.openInventory(this.getInventory());
    }

    @Override
    public void onNPCLeftClick(Player joueur) {
    }

    @Override
    public void onInventoryItemClick(Event event) {
        if (event instanceof InventoryClickEvent) {
            InventoryClickEvent inventoryClickEvent = (InventoryClickEvent)event;
            Player joueur = (Player)inventoryClickEvent.getWhoClicked();
            for (Category category : this.categories_dispo) {
                if (!category.toItemStack().equals((Object)inventoryClickEvent.getCurrentItem())) continue;
                category.openMenuToPlayer(joueur);
                return;
            }
        }
    }

    @Override
    public Inventory getInventory() {
        this.inventaire.clear();
        for (Category category : this.categories_dispo) {
            this.inventaire.addItem(new ItemStack[]{category.toItemStack()});
        }
        return this.inventaire;
    }

    public List<Category> getCategories_dispo() {
        return this.categories_dispo;
    }
}

