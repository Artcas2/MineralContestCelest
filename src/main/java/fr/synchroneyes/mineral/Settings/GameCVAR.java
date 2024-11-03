package fr.synchroneyes.mineral.Settings;

public class GameCVAR {
    private String command;
    private String description;
    private String valeur;
    private String type;
    private boolean canBeReloaded;
    private boolean isNumber;
    private int valeurNumerique;

    public GameCVAR(String command, String valeur, String description, String type, boolean canBeReloadedInGame, boolean isNumber) {
        this.command = command;
        this.valeur = valeur;
        this.description = description;
        this.type = type;
        this.canBeReloaded = canBeReloadedInGame;
        this.isNumber = isNumber;
        if (isNumber) {
            this.valeurNumerique = Integer.parseInt(valeur);
        }
    }

    public String getCommand() {
        return this.command;
    }

    public String getDescription() {
        return this.description;
    }

    public String getValeur() {
        return this.valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
        if (this.isNumber) {
            this.valeurNumerique = Integer.parseInt(valeur);
        }
    }

    public String getType() {
        return this.type;
    }

    public boolean canBeReloaded() {
        return this.canBeReloaded;
    }

    public boolean isNumber() {
        return this.isNumber;
    }

    public int getValeurNumerique() {
        return this.valeurNumerique;
    }
}

