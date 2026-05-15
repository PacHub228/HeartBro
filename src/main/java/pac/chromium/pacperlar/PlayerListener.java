package pac.chromium.pacperlar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final burger plugin;

    public PlayerListener(burger plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getHeartManager().applyInitialHearts(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.equals(victim)) {
            int victimHearts = plugin.getHeartManager().getHearts(victim);

            if (victimHearts > HeartManager.MIN_HEARTS) {
                plugin.getHeartManager().removeHearts(victim, 1);
                plugin.getHeartManager().addHearts(killer, 1);
                
                victim.sendMessage("§cВы потеряли сердце, умерев от игрока " + killer.getName() + "!");
                killer.sendMessage("§aВы получили сердце, убив игрока " + victim.getName() + "!");
            } else {
                killer.sendMessage("§eВы убили игрока с минимальным количеством сердец. Сердце не получено.");
                victim.sendMessage("§cВы умерли от игрока " + killer.getName() + ", но у вас уже минимальное количество сердец.");
            }
        }
    }
}
