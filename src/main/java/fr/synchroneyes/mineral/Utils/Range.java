package fr.synchroneyes.mineral.Utils;

import fr.synchroneyes.mineral.Exception.MaterialNotInRangeException;
import java.util.LinkedList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Range {
    int min = Integer.MIN_VALUE;
    int max = Integer.MIN_VALUE;
    Material nom;

    public Range(Material nom, int min, int max) {
        this.nom = nom;
        this.min = min;
        this.max = max;
    }

    public Range(int i, double goodLuckPercentage) {
    }

    public boolean isFilled() {
        return this.nom != null && this.max != Integer.MIN_VALUE && this.min != Integer.MIN_VALUE;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Material getMaterial() {
        return this.nom;
    }

    public void setMaterial(Material nom) {
        this.nom = nom;
    }

    public boolean isInRange(int valeur) {
        return this.min <= valeur && valeur < this.max;
    }

    public static Material getInsideRange(Range[] r, int valeur) throws MaterialNotInRangeException {
        for (Range interval : r) {
            if (!interval.isInRange(valeur)) continue;
            return interval.nom;
        }
        throw new MaterialNotInRangeException();
    }

    public static ItemStack getRandomItemFromLinkedList(LinkedList<Range> items, int itemNumber) {
        for (Range item : items) {
            if (!item.isInRange(itemNumber)) continue;
            return new ItemStack(item.getMaterial(), 1);
        }
        return null;
    }
}

