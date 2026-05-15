package pac.chromium.pacperlar;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HeartManager {
    private final burger plugin;
    private final DatabaseManager db;
    public static final int DEFAULT_HEARTS = 10;
    public static final int MIN_HEARTS = 3;

    public HeartManager(burger plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public int getHearts(Player player) {
        int hearts = db.getHearts(player.getUniqueId());
        if (hearts == -1) {
            hearts = DEFAULT_HEARTS;
            db.setHearts(player.getUniqueId(), hearts);
        }
        return hearts;
    }

    public void setHearts(Player player, int hearts) {
        db.setHearts(player.getUniqueId(), hearts);
        updatePlayerHealth(player, hearts);
    }

    public void addHearts(Player player, int amount) {
        int current = getHearts(player);
        setHearts(player, current + amount);
    }

    public void removeHearts(Player player, int amount) {
        int current = getHearts(player);
        int newValue = Math.max(MIN_HEARTS, current - amount);
        setHearts(player, newValue);
    }

    public void updatePlayerHealth(Player player, int hearts) {
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(hearts * 2.0);
        }
    }

    public void applyInitialHearts(Player player) {
        int hearts = getHearts(player);
        updatePlayerHealth(player, hearts);
    }
}
