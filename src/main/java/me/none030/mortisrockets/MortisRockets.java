package me.none030.mortisrockets;

import me.none030.mortisrockets.commands.RocketsCommand;
import me.none030.mortisrockets.events.RocketsEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static me.none030.mortisrockets.methods.StoringFiles.StoreFiles;

public final class MortisRockets extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        StoreFiles();
        getServer().getPluginCommand("rocket").setExecutor(new RocketsCommand());
        getServer().getPluginManager().registerEvents(new RocketsEvents(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
