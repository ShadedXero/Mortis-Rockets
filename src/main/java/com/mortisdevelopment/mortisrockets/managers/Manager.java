package com.mortisdevelopment.mortisrockets.managers;

import com.mortisdevelopment.mortisrockets.MortisRockets;
import com.mortisdevelopment.mortisrockets.config.ConfigManager;
import com.mortisdevelopment.mortisrockets.rockets.RocketManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

@Getter @Setter
public class Manager {

    private final MortisRockets plugin = MortisRockets.getInstance();
    private RocketManager rocketManager;
    private ConfigManager configManager;

    public Manager() {
        this.configManager = new ConfigManager(this);
        plugin.getCommand("rocket").setExecutor(new RocketCommand(this));
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        Bukkit.getScheduler().cancelTasks(plugin);
        setConfigManager(new ConfigManager(this));
    }
}
