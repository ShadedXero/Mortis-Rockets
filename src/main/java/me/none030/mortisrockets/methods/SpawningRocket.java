package me.none030.mortisrockets.methods;

import me.none030.mortisrockets.utils.RocketLaunch;
import me.none030.mortisrockets.utils.RocketLocation;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static me.none030.mortisrockets.MortisRockets.getEconomy;
import static me.none030.mortisrockets.MortisRockets.plugin;
import static me.none030.mortisrockets.methods.CreatingLocations.CanLaunch;
import static me.none030.mortisrockets.methods.CreatingLocations.GetLocation;

public class SpawningRocket {

    public static List<UUID> Rockets = new ArrayList<>();

    public static void SpawnRocket(Player player, String rocket) {

        File file = new File("plugins/MortisRockets/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config." + rocket);
        ConfigurationSection rocketSection = config.getConfigurationSection("config.rocket");
        ConfigurationSection itemSection = config.getConfigurationSection("config.item");
        assert section != null && rocketSection != null && itemSection != null;

        Economy economy = getEconomy();
        double amount = section.getDouble("cost");
        if (economy.has(player.getName(), amount)) {
            int launch = section.getInt("launching-radius");

            RocketLaunch launchPair = CanLaunch(player.getLocation(), launch, true);
            if (launchPair.isCanLaunch()) {

                String[] loc1 = Objects.requireNonNull(section.getString("location")).split(",");
                String[] loc2 = Objects.requireNonNull(section.getString("location2")).split(",");
                int range = section.getInt("landing-radius");
                boolean oceanChecker = section.getBoolean("ocean-checker");

                RocketLocation pair = GetLocation(Bukkit.getWorld(Objects.requireNonNull(section.getString("world"))), Double.parseDouble(loc1[0]), Double.parseDouble(loc1[1]), Double.parseDouble(loc2[0]), Double.parseDouble(loc2[1]), range, oceanChecker);
                Location location = pair.getLocation();

                if (location != null) {
                    location.getChunk().load();

                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(itemSection.getInt("custom-model-data"));
                    item.setItemMeta(meta);

                    ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    stand.getEquipment().setHelmet(item, true);
                    stand.addDisabledSlots(EquipmentSlot.HEAD);
                    stand.setCanPickupItems(false);
                    stand.setSilent(true);
                    stand.setInvulnerable(true);
                    stand.setCanMove(true);
                    stand.setAI(true);
                    stand.setInvisible(true);
                    stand.addPassenger(player);

                    Rockets.add(stand.getUniqueId());
                    double launchDistance = rocketSection.getDouble("launch.speed");
                    int launchTime = rocketSection.getInt("launch.time");
                    int landDistance = rocketSection.getInt("land.distance");
                    String launchMessage = Objects.requireNonNull(section.getString("launching-message")).replace("&", "§");
                    String landMessage = Objects.requireNonNull(section.getString("landing-message")).replace("&", "§");
                    final int[] count = {0};
                    player.setInvulnerable(true);
                    player.sendMessage(launchMessage);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            count[0] = count[0] + 1;
                            stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                            if (count[0] <= launchTime) {
                                stand.setVelocity(stand.getVelocity().setY(stand.getVelocity().getY() + launchDistance));
                            } else {
                                Location loc = new Location(location.getWorld(), location.getBlockX(),  landDistance + location.getBlockY(), location.getBlockZ());
                                Rockets.remove(stand.getUniqueId());
                                stand.eject();
                                stand.teleport(loc);
                                player.teleport(loc);
                                Rockets.add(stand.getUniqueId());
                                economy.withdrawPlayer(player, amount);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        stand.addPassenger(player);
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                        stand.setGravity(false);
                                        stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                stand.setGravity(true);
                                                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                            }
                                        }.runTaskLater(plugin, 20L);

                                        if (stand.isOnGround() || stand.isInWater() || stand.getPassengers().size() == 0) {
                                            player.sendMessage(landMessage);
                                            Rockets.remove(stand.getUniqueId());
                                            stand.eject();
                                            stand.remove();
                                            player.teleport(location);
                                            player.setInvulnerable(false);
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 20L, 40L);
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                } else {
                    player.sendMessage(Objects.requireNonNull(rocketSection.getString("messages." + pair.getReason().toString().toLowerCase())).replace("&", "§"));
                }
            } else {
                player.sendMessage(Objects.requireNonNull(rocketSection.getString("messages." + launchPair.getReason().toString().toLowerCase())).replace("&", "§"));
            }
        } else {
            player.sendMessage("§cYou don't have enough money");
        }
    }

    public static void SpawnRocket(Player player, String rocket, double x, double z) {

        File file = new File("plugins/MortisRockets/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config." + rocket);
        ConfigurationSection rocketSection = config.getConfigurationSection("config.rocket");
        ConfigurationSection itemSection = config.getConfigurationSection("config.item");
        assert section != null && rocketSection != null && itemSection != null;

        Economy economy = getEconomy();
        double amount = section.getDouble("cost");
        if (economy.has(player.getName(), amount)) {
            int launch = section.getInt("launching-radius");

            RocketLaunch launchPair = CanLaunch(player.getLocation(), launch, true);
            if (launchPair.isCanLaunch()) {

                String[] loc1 = Objects.requireNonNull(section.getString("location")).split(",");
                String[] loc2 = Objects.requireNonNull(section.getString("location2")).split(",");
                int range = section.getInt("landing-radius");
                boolean oceanChecker = section.getBoolean("ocean-checker");

                RocketLocation pair = GetLocation(Bukkit.getWorld(Objects.requireNonNull(section.getString("world"))), Double.parseDouble(loc1[0]), Double.parseDouble(loc1[1]), Double.parseDouble(loc2[0]), Double.parseDouble(loc2[1]), x, z, range, oceanChecker);
                Location location = pair.getLocation();

                if (location != null) {
                    location.getChunk().load();

                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(itemSection.getInt("custom-model-data"));
                    item.setItemMeta(meta);

                    ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    stand.getEquipment().setHelmet(item, true);
                    stand.addDisabledSlots(EquipmentSlot.HEAD);
                    stand.setCanPickupItems(false);
                    stand.setSilent(true);
                    stand.setInvulnerable(true);
                    stand.setCanMove(true);
                    stand.setAI(true);
                    stand.setInvisible(true);
                    stand.addPassenger(player);

                    Rockets.add(stand.getUniqueId());
                    double launchDistance = rocketSection.getDouble("launch.speed");
                    int launchTime = rocketSection.getInt("launch.time");
                    int landDistance = rocketSection.getInt("land.distance");
                    String launchMessage = Objects.requireNonNull(section.getString("launching-message")).replace("&", "§");
                    String landMessage = Objects.requireNonNull(section.getString("landing-message")).replace("&", "§");
                    final int[] count = {0};
                    player.setInvulnerable(true);
                    player.sendMessage(launchMessage);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            count[0] = count[0] + 1;
                            stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                            if (count[0] <= launchTime) {
                                stand.setVelocity(stand.getVelocity().setY(stand.getVelocity().getY() + launchDistance));
                            } else {
                                Location loc = new Location(location.getWorld(), location.getBlockX(),  landDistance + location.getBlockY(), location.getBlockZ());
                                Rockets.remove(stand.getUniqueId());
                                stand.eject();
                                stand.teleport(loc);
                                player.teleport(loc);
                                Rockets.add(stand.getUniqueId());
                                economy.withdrawPlayer(player, amount);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        stand.addPassenger(player);
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                        stand.setGravity(false);
                                        stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                stand.setGravity(true);
                                                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                            }
                                        }.runTaskLater(plugin, 20L);

                                        if (stand.isOnGround() || stand.isInWater() || stand.getPassengers().size() == 0) {
                                            player.sendMessage(landMessage);
                                            Rockets.remove(stand.getUniqueId());
                                            stand.eject();
                                            stand.remove();
                                            player.teleport(location);
                                            player.setInvulnerable(false);
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 20L, 40L);
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                } else {
                    player.sendMessage(Objects.requireNonNull(rocketSection.getString("messages." + pair.getReason().toString().toLowerCase())).replace("&", "§"));
                }
            } else {
                player.sendMessage(Objects.requireNonNull(rocketSection.getString("messages." + launchPair.getReason().toString().toLowerCase())).replace("&", "§"));
            }
        } else {
            player.sendMessage("§cYou don't have enough money");
        }
    }
}
