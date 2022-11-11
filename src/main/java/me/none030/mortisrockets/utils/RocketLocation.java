package me.none030.mortisrockets.utils;

import org.bukkit.Location;

public class RocketLocation {

    private Location location;
    private CancelReason reason;

    public RocketLocation(Location location, CancelReason reason) {
        this.location = location;
        this.reason = reason;
    }

    public Location getLocation() {
        return location;
    }

    public CancelReason getReason() {
        return reason;
    }
}
