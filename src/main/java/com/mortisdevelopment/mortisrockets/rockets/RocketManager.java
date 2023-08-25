package com.mortisdevelopment.mortisrockets.rockets;

import com.mortisdevelopment.mortisrockets.MortisRockets;
import com.mortisdevelopment.mortisrockets.managers.CoreManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class RocketManager extends CoreManager {

    private final MortisRockets plugin = MortisRockets.getInstance();
    private final RocketSettings settings;
    private final Map<String, Rocket> rocketById;
    private final Set<UUID> traveling;

    public RocketManager(RocketSettings settings) {
        this.settings = settings;
        this.rocketById = new HashMap<>();
        this.traveling = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(new RocketListener(this), plugin);
    }

    public boolean canLand(Rocket rocket, Player player, Location location) {
        if (!rocket.isOutsideTownRadius(location, rocket.getLandingRadius() * 16)) {
            player.sendMessage(getMessage("LAND_NEAR_TOWN"));
            return false;
        }
        return true;
    }

    public boolean canLaunch(Rocket rocket, Player player) {
        Location location = player.getLocation();
        World world = location.getWorld();
        if (world.getHighestBlockAt(location).getLocation().getBlockY() > location.getBlockY()) {
            player.sendMessage(getMessage("NO_SPACE"));
            return false;
        }
        if (!rocket.isOutsideTownRadius(location, rocket.getLaunchingRadius() * 16)) {
            player.sendMessage(getMessage("LAUNCH_NEAR_TOWN"));
            return false;
        }
        return true;
    }

    private boolean canTravel(Rocket rocket, Player player) {
        if (traveling.contains(player.getUniqueId())) {
            player.sendMessage(getMessage("ALREADY_TRAVELING"));
            return false;
        }
        if (!rocket.hasCost(player)) {
            player.sendMessage(getMessage("NOT_ENOUGH_MONEY"));
            return false;
        }
        return canLaunch(rocket, player);
    }

    public boolean travel(Rocket rocket, Player player, RocketLocation rocketLocation) {
        if (!canTravel(rocket, player)) {
            return false;
        }
        Location location = rocketLocation.getLocation(rocket.getWorld().getWorld());
        if (location == null) {
            player.sendMessage(getMessage("NOT_SAFE"));
            return false;
        }
        if (rocket.isOutsideRange(rocketLocation)) {
            player.sendMessage(getMessage("OUTSIDE_RANGE"));
            return false;
        }
        if (!canLand(rocket, player, location)) {
            return false;
        }
        rocket.removeCost(player);
        launch(rocket, player, location);
        return true;
    }

    public boolean travel(Rocket rocket, Player player) {
        if (!canTravel(rocket, player)) {
            return false;
        }
        Location location = rocket.getLandingLocation();
        if (location == null) {
            player.sendMessage(getMessage("NOT_SAFE"));
            return false;
        }
        if (!canLand(rocket, player, location)) {
            return false;
        }
        rocket.removeCost(player);
        launch(rocket, player, location);
        return true;
    }

    public void launch(Rocket rocket, Player player, Location location) {
        traveling.add(player.getUniqueId());
        player.sendMessage(rocket.getLaunchingMessage());
        ArmorStand stand = settings.getRocket(player);
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                stand.addPassenger(player);
                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                if (count <= settings.getLaunchingTime()) {
                    stand.setVelocity(stand.getVelocity().setY(stand.getVelocity().getY() + settings.getLaunchingSpeed()));
                    return;
                }
                land(rocket, player, location, stand);
                cancel();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void land(Rocket rocket, Player player, Location location, ArmorStand stand) {
        Location loc = new Location(location.getWorld(), location.getX(), location.getY() + settings.getLandingDistance(), location.getZ());
        stand.eject();
        stand.teleport(loc);
        player.teleport(loc);
        new BukkitRunnable() {
            @Override
            public void run() {
                stand.addPassenger(player);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                stand.setGravity(false);
                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                if (stand.isDead() || stand.isOnGround() || stand.isInLava() || stand.isInPowderedSnow() || stand.isInWaterOrBubbleColumn() || stand.getPassengers().isEmpty()) {
                    traveling.remove(player.getUniqueId());
                    player.sendMessage(rocket.getLandingMessage());
                    stand.eject();
                    stand.remove();
                    cancel();
                    return;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stand.addPassenger(player);
                        stand.setGravity(true);
                        stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                    }
                }.runTaskLater(plugin, 20L);
            }
        }.runTaskTimer(plugin, 20L, 40L);
    }
}
