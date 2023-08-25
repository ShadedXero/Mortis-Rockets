package com.mortisdevelopment.mortisrockets.rockets;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

@Getter @Setter
public class RocketLocation {

    private double x;
    private double z;

    public RocketLocation(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Location getLocation(World world) {
        Location location = new Location(world, x, 0, z);
        location.setY(world.getHighestBlockYAt(location));
        return location;
    }
}
