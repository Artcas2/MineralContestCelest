package fr.synchroneyes.mineral.DeathAnimations;

import fr.synchroneyes.custom_events.MCGameStartedEvent;
import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.mineral.DeathAnimations.Animations.EnderDomeAnimation;
import fr.synchroneyes.mineral.DeathAnimations.Animations.HalloweenHurricaneAnimation;
import fr.synchroneyes.mineral.DeathAnimations.Animations.LavaSpiderAnimation;
import fr.synchroneyes.mineral.DeathAnimations.Animations.SmokeAnimation;
import fr.synchroneyes.mineral.DeathAnimations.Animations.WaterSpiderAnimation;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.mineralcontest;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class DeathAnimationManager implements Listener {
    private List<DeathAnimation> liste_animations;
    private HashMap<Player, DeathAnimation> animation_par_joueur;
    private Inventory inventaireSelectionAnimation;
    private File fichier_data;

    public DeathAnimationManager() {
        Bukkit.getLogger().info("[MineralContestCelest] Enabling death animation manager");
        this.liste_animations = new LinkedList<DeathAnimation>();
        this.animation_par_joueur = new HashMap();
        this.initAnimations();
        this.inventaireSelectionAnimation = Bukkit.createInventory(null, (int)9, (String)"Selection d'une animation de mort");
        for (DeathAnimation animation : this.liste_animations) {
            this.inventaireSelectionAnimation.addItem(new ItemStack[]{animation.toItemStack()});
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)mineralcontest.plugin);
        this.fichier_data = new File(mineralcontest.plugin.getDataFolder(), FileList.DeathAnimation_DataFile.toString());
    }

    private void initAnimations() {
        this.liste_animations.add(new EnderDomeAnimation());
        this.liste_animations.add(new HalloweenHurricaneAnimation());
        this.liste_animations.add(new LavaSpiderAnimation());
        this.liste_animations.add(new WaterSpiderAnimation());
        this.liste_animations.add(new SmokeAnimation());
    }

    private void setPlayerAnimation(Player player, DeathAnimation animation) {
        if (this.animation_par_joueur.containsKey(player)) {
            this.animation_par_joueur.replace(player, animation);
        } else {
            this.animation_par_joueur.put(player, animation);
        }
        this.savePlayerAnimation(player, animation);
    }

    private DeathAnimation getPlayerAnimation(Player p) {
        if (this.animation_par_joueur.containsKey(p)) {
            return this.animation_par_joueur.get(p);
        }
        return null;
    }

    public void openMenuSelection(Player p) {
        p.openInventory(this.inventaireSelectionAnimation);
    }

    public void loadAnimationData() {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)this.fichier_data);
        for (String player_uid : configuration.getKeys(false)) {
            DeathAnimation animation;
            if (Bukkit.getPlayer((UUID)UUID.fromString(player_uid)) == null || (animation = this.getAnimationFromString(configuration.get(player_uid).toString())) == null) continue;
            this.setPlayerAnimation(Bukkit.getPlayer((UUID)UUID.fromString(player_uid)), animation);
        }
    }

    private DeathAnimation getAnimationFromString(String name) {
        for (DeathAnimation animation : this.liste_animations) {
            if (!animation.getClass().getSimpleName().equals(name)) continue;
            return animation;
        }
        return null;
    }

    public void savePlayerAnimation(Player player, DeathAnimation animation) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration((File)this.fichier_data);
        configuration.set(player.getUniqueId().toString(), (Object)animation.getClass().getSimpleName());
        try {
            configuration.save(this.fichier_data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onGameStart(MCGameStartedEvent event) {
        this.loadAnimationData();
    }

    @EventHandler
    public void onPlayerAnimationSelected(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player joueur = (Player)event.getWhoClicked();
        if (event.getInventory().equals((Object)this.inventaireSelectionAnimation)) {
            if (event.getCurrentItem() == null) {
                return;
            }
            for (DeathAnimation animation : this.liste_animations) {
                if (!animation.toItemStack().equals((Object)event.getCurrentItem())) continue;
                this.setPlayerAnimation(joueur, animation);
                joueur.sendMessage(mineralcontest.prefixPrive + "Vous avez s\u00e9lectionn\u00e9 l'animation: " + animation.getAnimationName());
                joueur.closeInventory();
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerKilled(PlayerDeathByPlayerEvent playerEvent) {
        if (this.getPlayerAnimation(playerEvent.getKiller()) == null) {
            return;
        }
        DeathAnimation animation = this.getPlayerAnimation(playerEvent.getKiller());
        animation.playAnimation((LivingEntity)playerEvent.getPlayerDead());
    }
}

