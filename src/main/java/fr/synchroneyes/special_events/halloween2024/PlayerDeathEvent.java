package fr.synchroneyes.special_events.halloween2024;

import fr.synchroneyes.custom_events.PlayerDeathByPlayerEvent;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.DeathAnimations.Animations.HalloweenHurricaneAnimation;
import fr.synchroneyes.mineral.DeathAnimations.DeathAnimation;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDeathEvent implements Listener {
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathByPlayerEvent event) {
        Game partie = event.getPartie();
        if (partie == null) {
            return;
        }
        GameSettings parametres = partie.groupe.getParametresPartie();
        boolean halloweenEnabled = parametres.getCVAR("enable_halloween_event").getValeurNumerique() == 1;
        halloweenEnabled = false;
        if (!halloweenEnabled) {
            return;
        }
        HalloweenHurricaneAnimation animationMort = new HalloweenHurricaneAnimation();
        ((DeathAnimation)animationMort).playAnimation((LivingEntity)event.getPlayerDead());
        Groupe playerGroup = mineralcontest.getPlayerGroupe(event.getKiller());
        if (playerGroup == null) {
            return;
        }
        for (Player joueurGroupe : playerGroup.getPlayers()) {
            if (playerGroup.getGame().isReferee(joueurGroupe) || joueurGroupe.equals((Object)event.getKiller()) || joueurGroupe.equals((Object)event.getPlayerDead())) continue;
            joueurGroupe.playSound(joueurGroupe.getLocation(), this.getRandomSound(), 0.8f, 1.0f);
        }
    }

    private Sound getRandomSound() {
        Sound[] sounds = new Sound[]{Sound.ENTITY_ZOMBIE_AMBIENT, Sound.ENTITY_CREEPER_PRIMED, Sound.BLOCK_STONE_STEP, Sound.ENTITY_SPIDER_AMBIENT, Sound.ENTITY_SPIDER_STEP, Sound.ENTITY_ENDERMAN_AMBIENT, Sound.ENTITY_ENDERMAN_TELEPORT, Sound.ENTITY_ENDERMAN_SCREAM};
        int random = new Random().nextInt(sounds.length);
        return sounds[random];
    }
}

