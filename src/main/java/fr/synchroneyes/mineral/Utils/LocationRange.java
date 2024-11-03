package fr.synchroneyes.mineral.Utils;

import org.bukkit.Location;

public class LocationRange {
    public static boolean isLocationBetween(Location check, Location target, Location min, Location max) {
        return min.getX() < check.getX() && check.getX() < max.getX() && min.getY() < check.getY() && check.getY() < max.getY() && min.getZ() < check.getZ() && check.getZ() < max.getZ();
    }

    public static boolean isLocationBetween(Location check, Location target, int min, int max) {
        Location _min = target.clone();
        Location _max = target.clone();
        _min.setX(_min.getX() - (double)min);
        _min.setY(_min.getY() - (double)min);
        _min.setZ(_min.getZ() - (double)min);
        _max.setX(_max.getX() + (double)min);
        _max.setY(_max.getY() + (double)min);
        _max.setZ(_max.getZ() + (double)min);
        return LocationRange.isLocationBetween(check, target, _min, _max);
    }
}

