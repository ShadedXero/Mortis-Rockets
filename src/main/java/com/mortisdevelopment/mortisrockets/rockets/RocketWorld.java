package com.mortisdevelopment.mortisrockets.rockets;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

@Getter
public class RocketWorld {

    private final String worldName;

    public RocketWorld(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

}
