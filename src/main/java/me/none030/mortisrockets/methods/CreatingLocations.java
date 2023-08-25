package me.none030.mortisrockets.methods;

import com.palmergames.bukkit.towny.TownyAPI;
import me.none030.mortisrockets.utils.CancelReason;
import me.none030.mortisrockets.utils.RocketLaunch;
import me.none030.mortisrockets.utils.RocketLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class CreatingLocations {

    public static RocketLocation GetLocation(World world, double x1, double z1, double x2, double z2, double x, double z, int range, boolean oceanChecker) {

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

        TownyAPI town = TownyAPI.getInstance();
        Location loc = new Location(world, x, 0, z);
        if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
            Location safe = loc.getWorld().getHighestBlockAt(loc).getLocation();
            if (range == 0) {
                if (oceanChecker) {
                    if (!safe.getBlock().isLiquid() && !safe.getBlock().getType().equals(Material.AIR)) {
                        if (town.isWilderness(safe)) {
                            if (safe.getX() >= minX && safe.getX() <= maxX && safe.getZ() >= minZ && safe.getZ() <= maxZ) {
                                return new RocketLocation(safe.add(0, 2, 0), CancelReason.NONE);
                            } else {
                                return new RocketLocation(null, CancelReason.OUTSIDE_RANGE);
                            }
                        } else {
                            return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                        }
                    } else {
                        return new RocketLocation(null, CancelReason.NOT_SAFE);
                    }
                } else {
                    if (!safe.getBlock().getType().equals(Material.LAVA) && !safe.getBlock().getType().equals(Material.AIR)) {
                        if (town.isWilderness(safe)) {
                            if (safe.getX() >= minX && safe.getX() <= maxX && safe.getZ() >= minZ && safe.getZ() <= maxZ) {
                                return new RocketLocation(safe.add(0, 2, 0), CancelReason.NONE);
                            } else {
                                return new RocketLocation(null, CancelReason.OUTSIDE_RANGE);
                            }
                        } else {
                            return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                        }
                    } else {
                        return new RocketLocation(null, CancelReason.NOT_SAFE);
                    }
                }
            } else {
                if (oceanChecker) {
                    if (!safe.getBlock().isLiquid() && !safe.getBlock().getType().equals(Material.AIR)) {
                        if (town.isWilderness(safe)) {
                            if (safe.getX() >= minX && safe.getX() <= maxX && safe.getZ() >= minZ && safe.getZ() <= maxZ) {
                                if (isOutsideRadius(safe, range)) {
                                    return new RocketLocation(safe.add(0, 2, 0), CancelReason.NONE);
                                } else {
                                    return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                                }
                            } else {
                                return new RocketLocation(null, CancelReason.OUTSIDE_RANGE);
                            }
                        } else {
                            return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                        }
                    } else {
                        return new RocketLocation(null, CancelReason.NOT_SAFE);
                    }
                } else {
                    if (!safe.getBlock().getType().equals(Material.LAVA) && !safe.getBlock().getType().equals(Material.AIR)) {
                        if (town.isWilderness(safe)) {
                            if (safe.getX() >= minX && safe.getX() <= maxX && safe.getZ() >= minZ && safe.getZ() <= maxZ) {
                                if (isOutsideRadius(safe, range)) {
                                    return new RocketLocation(safe.add(0, 2, 0), CancelReason.NONE);
                                } else {
                                    return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                                }
                            } else {
                                return new RocketLocation(null, CancelReason.OUTSIDE_RANGE);
                            }
                        } else {
                            return new RocketLocation(null, CancelReason.LAND_NEAR_TOWN);
                        }
                    } else {
                        return new RocketLocation(null, CancelReason.NOT_SAFE);
                    }
                }
            }
        } else {
            return new RocketLocation(null, CancelReason.OUTSIDE_RANGE);
        }
    }

    public static boolean isOutsideRadius(Location location, int radius) {

        TownyAPI town = TownyAPI.getInstance();
        int finalRadius = 16 * radius;
        boolean isOutside = true;

        for (int x = -finalRadius; x <= finalRadius; x++) {
            for (int z = -finalRadius; z <= finalRadius; z++) {

                Location loc = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z).getLocation();
                if (!town.isWilderness(loc)) {
                    isOutside = false;
                    break;
                }
            }
        }
        return isOutside;
    }

    public static RocketLocation GetLocation(World world, double x1, double z1, double x2, double z2, int range, boolean oceanChecker) {

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
                    if (!loc.getBlock().isLiquid() && !loc.getBlock().getType().equals(Material.AIR)) {
                        return new RocketLocation(loc.add(0, 2, 0), CancelReason.NONE);
                    }
                } else {
                    if (!loc.getBlock().getType().equals(Material.LAVA) && !loc.getBlock().getType().equals(Material.AIR)) {
                        return new RocketLocation(loc.add(0, 2, 0), CancelReason.NONE);
                    }
                }
            }
            if (range > 0) {
                if (town.isWilderness(location)) {
                    RocketLaunch rocket = CanLaunch(location, range, false);
                    if (rocket.isCanLaunch()) {
                        Location loc = location.getWorld().getHighestBlockAt(location).getLocation();
                        if (oceanChecker) {
                            if (!loc.getBlock().isLiquid() && !loc.getBlock().getType().equals(Material.AIR)) {
                                return new RocketLocation(loc.add(0, 2, 0), CancelReason.NONE);
                            }
                        } else {
                            if (!loc.getBlock().getType().equals(Material.LAVA) && !loc.getBlock().getType().equals(Material.AIR)) {
                                return new RocketLocation(loc.add(0, 2, 0), CancelReason.NONE);
                            }
                        }
                    } else {
                        return new RocketLocation(null, rocket.getReason());
                    }
                }
            }
        }

        return new RocketLocation(null, CancelReason.COULD_NOT_FIND);
    }

    public static RocketLaunch CanLaunch(Location location, int radius, boolean check) {

        TownyAPI town = TownyAPI.getInstance();
        int finalRadius = radius * 16;
        if (check) {
            if (location.getWorld().getHighestBlockAt(location).getLocation().getBlockY() > location.getBlockY()) {
                return new RocketLaunch(false, CancelReason.NO_SPACE);
            }
        }
        if (radius == 0) {
            return new RocketLaunch(true, CancelReason.NONE);
        }
        if (radius > 0) {
            for (int x = -finalRadius; x <= finalRadius; x++) {
                for (int z = -finalRadius; z <= finalRadius; z++) {

                    Location loc = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z).getLocation();
                    if (!town.isWilderness(loc)) {
                        if (check) {
                            return new RocketLaunch(false, CancelReason.LAUNCH_NEAR_TOWN);
                        } else {
                            return new RocketLaunch(false, CancelReason.LAND_NEAR_TOWN);
                        }
                    }
                }
            }
        }

        return new RocketLaunch(true, CancelReason.NONE);
    }
}
