package me.none030.mortisrockets.methods;

import com.palmergames.bukkit.towny.TownyAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreatingLocations {

    public static Location GetLocation(World world, double x, double z, int range, boolean oceanChecker) {

        TownyAPI town = TownyAPI.getInstance();
        Location location = new Location(world, x, 0, z);
        if (range > 0) {
            Location loc = GetFinal(location, range);
            if (town.isWilderness(loc)) {
                Location safe = GetSafe(loc, oceanChecker);
                if (safe != null) {
                    if (town.isWilderness(safe)) {
                        return safe.add(0, 2, 0);
                    }
                }
            }
        } else {
            Location safe = GetSafe(location, oceanChecker);
            if (safe != null) {
                if (town.isWilderness(safe)) {
                    return safe.add(0, 2, 0);
                }
            }
        }

        return null;
    }

    public static Location GetFinal(Location location, int range) {

        Random random = new Random();
        TownyAPI town = TownyAPI.getInstance();
        int finalRadius = range * 16;
        boolean inTown = false;
        for (int x = -finalRadius; x <= finalRadius; x++) {
            for (int z = -finalRadius; z <= finalRadius; z++) {

                Location loc = location.getBlock().getRelative(x, location.getBlockY(), z).getLocation();
                if (!town.isWilderness(loc)) {
                    inTown = true;
                    break;
                }
            }
        }
        if (!inTown) {
            return location;
        } else {
            List<Location> locationList = new ArrayList<>();
            locationList.add(new Location(location.getWorld(), location.getBlockX() + finalRadius, location.getBlockY(), location.getBlockZ()));
            locationList.add(new Location(location.getWorld(), location.getBlockX() - finalRadius, location.getBlockY(), location.getBlockZ()));
            locationList.add(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()+ finalRadius));
            locationList.add(new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ() - finalRadius));

            int number = random.nextInt(0, 3);

            return locationList.get(number);
        }
    }

    public static Location GetSafe(Location location, boolean oceanChecker) {

        int finalRadius = 16;
        for (int x = -finalRadius; x <= finalRadius; x++) {
            for (int z = -finalRadius; z <= finalRadius; z++) {

                Location loc = location.getBlock().getRelative(x, location.getBlockY(), z).getLocation();
                Location highest = loc.getWorld().getHighestBlockAt(loc).getLocation();
                if (oceanChecker) {
                    if (!highest.getBlock().isLiquid()) {
                        return highest;
                    }
                } else {
                    if (!highest.getBlock().getType().equals(Material.LAVA)) {
                        return highest;
                    }
                }
            }
        }

        return null;
    }

    public static Location GetLocation(World world, double x1, double z1, double x2, double z2, int range, boolean oceanChecker) {

        double minX;
        double maxX;
        double minZ;
        double maxZ;

        if (x1 > x2) {
            maxX = x1;
            minX = x2;
        } else {
            minX = x1;
            maxX = x2;
        }
        if (z1 > z2) {
            maxZ = z1;
            minZ = z2;
        } else {
            minZ = z1;
            maxZ = z2;
        }

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            double x = random.nextDouble(minX, maxX);
            double y = 0;
            double z = random.nextDouble(minZ, maxZ);

            Location location = new Location(world, x, y, z);
            TownyAPI town = TownyAPI.getInstance();
            if (range == 0) {
                Location loc = location.getWorld().getHighestBlockAt(location).getLocation();
                if (oceanChecker) {
                    if (!loc.getBlock().isLiquid()) {
                        return loc.add(0, 2, 0);
                    }
                } else {
                    if (!loc.getBlock().getType().equals(Material.LAVA)) {
                        return loc.add(0, 2, 0);
                    }
                }
            }
            if (range > 0) {
                if (town.isWilderness(location)) {
                    if (CanLaunch(location, range)) {
                        Location loc = location.getWorld().getHighestBlockAt(location).getLocation();
                        if (oceanChecker) {
                            if (!loc.getBlock().isLiquid()) {
                                return loc.add(0, 2, 0);
                            }
                        } else {
                            if (!loc.getBlock().getType().equals(Material.LAVA)) {
                                return loc.add(0, 2, 0);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static boolean CanLaunch(Location location, int radius) {

        TownyAPI town = TownyAPI.getInstance();
        int finalRadius = radius * 16;
        if (radius == 0) {
            return true;
        }
        if (radius > 0) {
            for (int x = -finalRadius; x <= finalRadius; x++) {
                for (int z = -finalRadius; z <= finalRadius; z++) {

                    Location loc = location.getBlock().getRelative(x, location.getBlockY(), z).getLocation();
                    if (!town.isWilderness(loc)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
