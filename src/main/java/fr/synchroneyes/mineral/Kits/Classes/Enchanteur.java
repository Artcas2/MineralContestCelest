package fr.synchroneyes.mineral.Kits.Classes;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.MCPlayerRespawnEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Kits.KitAbstract;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchanteur extends KitAbstract {
    private HashMap<Player, Integer> niveaux_joueurs = new HashMap();
    private int niveauxExpRespawn = 15;
    private int nombreLapisRespawn = 32;
    private int nombreLivreEnchant = 5;

    @Override
    public String getNom() {
        return Lang.kit_wizard_title.toString();
    }

    @Override
    public String getDescription() {
        return Lang.kit_wizard_description.toString();
    }

    @Override
    public Material getRepresentationMaterialForSelectionMenu() {
        return Material.ENCHANTING_TABLE;
    }

    @EventHandler
    public void OnGameStarted(MCGameStartedEvent event) {
        Game partie = event.getGame();
        LinkedList<Player> joueurs = partie.groupe.getPlayers();
        for (Player joueur : joueurs) {
            if (!this.isPlayerUsingThisKit(joueur)) continue;
            this.applyKitEffectToPlayer(joueur);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathByPlayerEvent event) {
        Player deadPlayer = event.getPlayerDead();
        if (!this.isPlayerUsingThisKit(deadPlayer)) {
            return;
        }
        if (this.niveaux_joueurs.containsKey(deadPlayer)) {
            this.niveaux_joueurs.replace(deadPlayer, deadPlayer.getLevel());
        } else {
            this.niveaux_joueurs.put(deadPlayer, deadPlayer.getLevel());
        }
    }

    @EventHandler
    public void onPlayerRespawn(MCPlayerRespawnEvent event) {
        if (!this.isPlayerUsingThisKit(event.getJoueur())) {
            return;
        }
        if (!this.niveaux_joueurs.containsKey(event.getJoueur())) {
            return;
        }
        event.getJoueur().setLevel(this.niveaux_joueurs.get(event.getJoueur()).intValue());
    }

    private void applyKitEffectToPlayer(Player joueur) {
        int max;
        File fichierConfig = new File(mineralcontest.plugin.getDataFolder(), FileList.Kit_Enchanteur_ConfigFile.toString());
        if (!fichierConfig.exists()) {
            this.applyDefaultEffects(joueur);
            return;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)fichierConfig);
        int book_count = -1;
        int book_min_level = -1;
        int book_max_level = -1;
        int lapis_count = -1;
        int level_on_spawn = -1;
        lapis_count = Integer.parseInt(Objects.requireNonNull(configuration.get("lapis_on_game_start")).toString());
        book_count = Integer.parseInt(Objects.requireNonNull(configuration.get("enchantment_book_count")).toString());
        book_min_level = Integer.parseInt(Objects.requireNonNull(configuration.get("enchantment_book_min_level")).toString());
        book_max_level = Integer.parseInt(Objects.requireNonNull(configuration.get("enchantment_book_max_level")).toString());
        level_on_spawn = Integer.parseInt(Objects.requireNonNull(configuration.get("level_on_game_start")).toString());
        if (book_count == -1 || book_min_level <= 0 || book_max_level <= 0 || lapis_count == -1 || level_on_spawn <= 0) {
            this.applyDefaultEffects(joueur);
            return;
        }
        int min = Math.min(book_max_level, book_min_level);
        book_max_level = max = Math.max(book_max_level, book_min_level);
        book_min_level = min;
        List<String> enchantments = configuration.getStringList("enchantment_available");
        ArrayList<EnchantmentWrapper> enchantements_list = new ArrayList<EnchantmentWrapper>();
        for (String enchant : enchantments) {
            enchantements_list.add(new EnchantmentWrapper(enchant));
        }
        joueur.setLevel(level_on_spawn);
        joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.LAPIS_LAZULI, lapis_count)});
        for (int indexLivre = 0; indexLivre < book_count; ++indexLivre) {
            ItemStack livre = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)livre.getItemMeta();
            Random random = new Random();
            int niveauGenere = 0;
            niveauGenere = random.nextInt(book_max_level - book_min_level + 1) + book_min_level;
            Enchantment enchantment_genere = (Enchantment)enchantements_list.get(new Random().nextInt(enchantements_list.size()));
            meta.addStoredEnchant(enchantment_genere, niveauGenere, true);
            livre.setItemMeta((ItemMeta)meta);
            joueur.getInventory().addItem(new ItemStack[]{livre});
        }
    }

    private void applyDefaultEffects(Player joueur) {
        int niveauMinEnchantement = 1;
        int niveamMaxEnchantement = 3;
        Enchantment[] enchantments = new Enchantment[]{Enchantment.DURABILITY, Enchantment.DAMAGE_ALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE, Enchantment.KNOCKBACK, Enchantment.LOOT_BONUS_BLOCKS};
        ItemStack[] items = new ItemStack[this.nombreLivreEnchant];
        int indexLivre = 0;
        if (indexLivre < this.nombreLivreEnchant) {
            ItemStack livre = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)livre.getItemMeta();
            Random random = new Random();
            int niveauGenere = 0;
            Enchantment enchantmentGenere = enchantments[random.nextInt(enchantments.length)];
            niveauGenere = random.nextInt(niveamMaxEnchantement - niveauMinEnchantement + 1) + niveauMinEnchantement;
            meta.addStoredEnchant(enchantmentGenere, niveauGenere, true);
            livre.setItemMeta((ItemMeta)meta);
            joueur.getInventory().addItem(new ItemStack[]{livre});
            joueur.setLevel(this.niveauxExpRespawn);
            joueur.getInventory().addItem(new ItemStack[]{new ItemStack(Material.LAPIS_LAZULI, this.nombreLapisRespawn)});
            return;
        }
    }
}

