package fr.synchroneyes.mineral.Utils;

import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class WorldUtils {
    public static void removeAllDroppedItems(World w) {
        List<Entity> entList = w.getEntities();
        for (Entity current : entList) {
            if (!(current instanceof Item)) continue;
            current.remove();
        }
    }
}

