package pac.chromium.pacperlar;

import org.bukkit.plugin.java.JavaPlugin;

public final class burger extends JavaPlugin {

    private DatabaseManager dbManager;
    private HeartManager heartManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        dbManager = new DatabaseManager(this);
        dbManager.connect();

        heartManager = new HeartManager(this, dbManager);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        HeartBroCommand cmd = new HeartBroCommand(this);
        getCommand("heartbro").setExecutor(cmd);
        getCommand("heartbro").setTabCompleter(cmd);
    }

    @Override
    public void onDisable() {
        if (dbManager != null) {
            dbManager.disconnect();
        }
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }
}
