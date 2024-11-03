package fr.synchroneyes.mineral.Utils;

public class TimeConverter {
    public static String intToString(int valeurEnSecondes) {
        int minutes = valeurEnSecondes / 60;
        int secondes = valeurEnSecondes % 60;
        return String.format("%02d:%02d", minutes, secondes);
    }
}

