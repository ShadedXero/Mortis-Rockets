package com.mortisdevelopment.mortisrockets;

import com.mortisdevelopment.mortisrockets.managers.Manager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MortisRockets extends JavaPlugin {

    @Getter
    private static MortisRockets Instance;
    @Getter
    private Economy economy;
    @Getter
    public boolean towny;
    @Getter
    private Manager manager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        towny = getServer().getPluginManager().getPlugin("Towny") != null;
        manager = new Manager();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
