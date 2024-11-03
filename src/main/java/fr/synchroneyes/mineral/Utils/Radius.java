package fr.synchroneyes.mineral.Utils;

import org.bukkit.Location;

public class Radius {
    public static boolean isBlockInRadius(Location source, Location blockToFind, int radius) {
        return source.getX() - (double)radius < blockToFind.getX() && source.getX() + (double)radius > blockToFind.getX() && source.getZ() - (double)radius < blockToFind.getZ() && source.getZ() + (double)radius > blockToFind.getZ() && source.getY() - (double)radius < blockToFind.getY() && source.getY() + (double)radius > blockToFind.getY();
    }

    public static boolean isBlockInRadiusWithDividedYAxis(Location source, Location blockToFind, int radius, int divider) {
        return source.getX() - (double)radius < blockToFind.getX() && source.getX() + (double)radius > blockToFind.getX() && source.getZ() - (double)radius < blockToFind.getZ() && source.getZ() + (double)radius > blockToFind.getZ() && source.getY() - (double)(radius / divider) < blockToFind.getY() && source.getY() + (double)(radius / divider) > blockToFind.getY();
    }
}

