package com.mortisdevelopment.mortisrockets.config;

import com.mortisdevelopment.mortisrockets.MortisRockets;
import lombok.Getter;

import java.io.File;

@Getter
public abstract class Config {

    private final MortisRockets plugin = MortisRockets.getInstance();
    private final String fileName;

    public Config(String fileName) {
        this.fileName = fileName;
    }

    public abstract void loadConfig();

    public File saveConfig() {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, true);
        }
        return file;
    }
}
