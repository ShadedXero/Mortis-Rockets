package com.mortisdevelopment.mortisrockets.rockets;

import com.mortisdevelopment.mortisrockets.MortisRockets;
import com.palmergames.bukkit.towny.TownyAPI;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

@Getter
public class Rocket {

    private final MortisRockets plugin = MortisRockets.getInstance();
    private final Economy economy = plugin.getEconomy();
    private final String id;
    private final RocketWorld world;
    private final RocketLocation location1;
    private final RocketLocation location2;
    private final boolean specificLocation;
    private final double cost;
    private final int launchingRadius;
    private final int landingRadius;
    private final boolean oceanChecker;
    private final String launchingMessage;
    private final String landingMessage;

    public Rocket(String id, RocketWorld world, RocketLocation location1, RocketLocation location2, boolean specificLocation, double cost, int launchingRadius, int landingRadius, boolean oceanChecker, String launchingMessage, String landingMessage) {
        this.id = id;
        this.world = world;
        this.location1 = location1;
        this.location2 = location2;
        this.specificLocation = specificLocation;
        this.cost = cost;
        this.launchingRadius = launchingRadius;
        this.landingRadius = landingRadius;
        this.oceanChecker = oceanChecker;
        this.launchingMessage = launchingMessage;
        this.landingMessage = landingMessage;
    }

    public boolean isOutsideRange(RocketLocation rocketLocation) {
        double minX = Math.min(location1.getX(), location2.getX());
        double maxX = Math.max(location1.getX(), location2.getX());
        double minZ = Math.min(location1.getZ(), location2.getZ());
        double maxZ = Math.max(location1.getZ(), location2.getZ());
        double x = rocketLocation.getX();
        double z = rocketLocation.getZ();
        return x < minX || x > maxX || z < minZ || z > maxZ;
    }

    public Location getLandingLocation() {
        double minX = Math.min(location1.getX(), location2.getX());
        double maxX = Math.max(location1.getX(), location2.getX());
        double minZ = Math.min(location1.getZ(), location2.getZ());
        double maxZ = Math.max(location1.getZ(), location2.getZ());
        World world = getWorld().getWorld();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            double x = random.nextDouble(minX, maxX);
            double z = random.nextDouble(minZ, maxZ);
            Location location = new Location(world, x, 0, z);
            location.setY(location.getWorld().getHighestBlockYAt(location));
            if (!hasRequirements(location)) {
                continue;
            }
            return location;
        }
        return null;
    }

    public boolean isOutsideTownRadius(Location location, int radius) {
        if (!plugin.isTowny() || radius <= 0) {
            return true;
        }
        TownyAPI towny = TownyAPI.getInstance();
        double locationX = location.getX();
        double locationY = location.getY();
        double locationZ = location.getZ();
        for (double x = -radius; x <= radius; x++) {
            for (double z = -radius; z <= radius; z++) {
                Location loc = new Location(location.getWorld(), locationX + x, locationY, locationZ + z);
                if (!towny.isWilderness(loc)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasRequirements(Location location) {
        Material type = location.getWorld().getHighestBlockAt(location).getType();
        if (type.isAir() || type.equals(Material.LAVA)) {
            return false;
        }
        if (oceanChecker) {
            return !type.equals(Material.WATER) && !type.equals(Material.KELP) && !type.equals(Material.KELP_PLANT) && !type.equals(Material.SEAGRASS) && !type.equals(Material.TALL_SEAGRASS);
        }
        return true;
    }

    public boolean hasCost(Player player) {
        return economy.has(player, cost);
    }

    public void removeCost(Player player) {
        economy.withdrawPlayer(player, cost);
    }
}
