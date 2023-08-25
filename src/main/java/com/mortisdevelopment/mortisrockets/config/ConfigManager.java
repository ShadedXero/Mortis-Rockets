package com.mortisdevelopment.mortisrockets.config;

import com.mortisdevelopment.mortisrockets.managers.Manager;
import lombok.Getter;

@Getter
public class ConfigManager {

    private final Manager manager;
    private final MainConfig mainConfig;

    public ConfigManager(Manager manager) {
        this.manager = manager;
        this.mainConfig = new MainConfig(this);
    }
}
