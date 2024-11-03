package fr.synchroneyes.mineral.Core.Game.JoinTeam.Items;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.JoinTeam.Items.ItemInterface;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ChatColorString;
import fr.synchroneyes.mineral.mineralcontest;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class JoinTeamItem extends ItemInterface {
    private Equipe team;

    public JoinTeamItem(Equipe equipe) {
        this.team = equipe;
    }

    @Override
    public Material getItemMaterial() {
        try {
            return Material.valueOf((String)(ChatColorString.toStringEN(this.team.getCouleur()) + "_CONCRETE"));
        } catch (IllegalArgumentException iae) {
            return Material.WHITE_WOOL;
        }
    }

    @Override
    public String getNomInventaire() {
        return this.team.getCouleur() + this.team.getNomEquipe();
    }

    @Override
    public List<String> getDescriptionInventaire() {
        ArrayList<String> list = new ArrayList<String>();
        if (this.team.getJoueurs().isEmpty()) {
            list.add(Lang.currently_no_player_in_this_team.toString());
            return list;
        }
        for (Player joueur : this.team.getJoueurs()) {
            list.add("> " + joueur.getDisplayName());
        }
        return list;
    }

    @Override
    public void performClick(Player joueur) {
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);
        if (playerGroup == null) {
            joueur.closeInventory();
            return;
        }
        if (playerGroup.getGame() == null) {
            joueur.closeInventory();
            return;
        }
        Equipe playerTeam = playerGroup.getPlayerTeam(joueur);
        if (playerTeam != null) {
            playerTeam.removePlayer(joueur);
        }
        try {
            this.team.addPlayerToTeam(joueur, !playerGroup.getGame().isGameStarted());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

