package me.none030.mortisrockets.methods;

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

            if (CanLaunch(player.getLocation(), launch)) {

                String[] loc1 = Objects.requireNonNull(section.getString("location")).split(",");
                String[] loc2 = Objects.requireNonNull(section.getString("location2")).split(",");
                int range = section.getInt("landing-radius");
                boolean oceanChecker = section.getBoolean("ocean-checker");

                Location location = GetLocation(Bukkit.getWorld(Objects.requireNonNull(section.getString("world"))), Double.parseDouble(loc1[0]), Double.parseDouble(loc1[1]), Double.parseDouble(loc2[0]), Double.parseDouble(loc2[1]), range, oceanChecker);

                if (location != null) {
                    location.getChunk().load();

                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(itemSection.getInt("custom-model-data"));
                    item.setItemMeta(meta);

                    ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    stand.getEquipment().setHelmet(item, true);
                    stand.addDisabledSlots(EquipmentSlot.HEAD);
                    stand.setCustomName(UUID.randomUUID().toString());
                    stand.setCustomNameVisible(false);
                    stand.setCanPickupItems(false);
                    stand.setSilent(true);
                    stand.setInvulnerable(true);
                    stand.setCanMove(true);
                    stand.setAI(true);
                    stand.setInvisible(true);
                    stand.addPassenger(player);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (Rockets.contains(stand.getUniqueId())) {
                                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 5L);

                    Rockets.add(stand.getUniqueId());
                    double launchSpeed = rocketSection.getDouble("launch.speed");
                    int launchTime = rocketSection.getInt("launch.time");
                    double landSpeed = rocketSection.getDouble("land.speed");
                    int landTime = rocketSection.getInt("land.time");
                    String message = Objects.requireNonNull(section.getString("landing-message")).replace("&", "§");
                    final int[] count = {0};
                    player.setInvulnerable(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            count[0] = count[0] + 1;
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                            if (count[0] <= launchTime) {
                                stand.setVelocity(stand.getVelocity().setY(stand.getVelocity().getY() + launchSpeed));
                            } else {
                                Location loc = new Location(location.getWorld(), location.getBlockX(),  landSpeed * 10 + location.getBlockZ(), location.getBlockZ());
                                Rockets.remove(stand.getUniqueId());
                                stand.eject();
                                Rockets.add(stand.getUniqueId());
                                stand.teleport(loc);
                                player.teleport(loc);
                                stand.addPassenger(player);
                                economy.withdrawPlayer(player, amount);
                                final int[] landing = {0};
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        landing[0] = landing[0] + 1;
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                        if (landing[0] <= (int) landTime / 2) {
                                            stand.setGravity(false);
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    stand.setGravity(true);
                                                }
                                            }.runTaskLater(plugin, 20L);
                                        }

                                        if (stand.isOnGround() || stand.isInWater()) {
                                            player.sendMessage(message);
                                            stand.remove();
                                            player.teleport(location);
                                            player.setInvulnerable(false);
                                            Rockets.remove(stand.getUniqueId());
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 0L, 40L);
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                } else {
                    player.sendMessage("§cCould not find a suitable location");
                }
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

            if (CanLaunch(player.getLocation(), launch)) {
                int range = section.getInt("landing-radius");
                boolean oceanChecker = section.getBoolean("ocean-checker");

                Location location = GetLocation(Bukkit.getWorld(Objects.requireNonNull(section.getString("world"))), x, z, range, oceanChecker);

                if (location != null) {
                    location.getChunk().load();

                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(itemSection.getInt("custom-model-data"));
                    item.setItemMeta(meta);

                    ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
                    stand.getEquipment().setHelmet(item, true);
                    stand.addDisabledSlots(EquipmentSlot.HEAD);
                    stand.setCustomName(UUID.randomUUID().toString());
                    stand.setCustomNameVisible(false);
                    stand.setCanPickupItems(false);
                    stand.setSilent(true);
                    stand.setInvulnerable(true);
                    stand.setCanMove(true);
                    stand.setAI(true);
                    stand.setInvisible(true);
                    stand.addPassenger(player);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (Rockets.contains(stand.getUniqueId())) {
                                stand.getWorld().spawnParticle(Particle.LAVA, stand.getLocation(), 50);
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 5L);

                    Rockets.add(stand.getUniqueId());
                    double launchSpeed = rocketSection.getDouble("launch.speed");
                    int launchTime = rocketSection.getInt("launch.time");
                    double landSpeed = rocketSection.getDouble("land.speed");
                    int landTime = rocketSection.getInt("land.time");
                    String message = Objects.requireNonNull(section.getString("landing-message")).replace("&", "§");
                    final int[] count = {0};
                    player.setInvulnerable(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            count[0] = count[0] + 1;
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                            if (count[0] <= launchTime) {
                                stand.setVelocity(stand.getVelocity().setY(stand.getVelocity().getY() + launchSpeed));
                            } else {
                                Location loc = new Location(location.getWorld(), location.getBlockX(), landSpeed * 10 + location.getBlockZ(), location.getBlockZ());
                                Rockets.remove(stand.getUniqueId());
                                stand.eject();
                                Rockets.add(stand.getUniqueId());
                                stand.teleport(loc);
                                player.teleport(loc);
                                stand.addPassenger(player);
                                economy.withdrawPlayer(player, amount);
                                final int[] landing = {0};
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        landing[0] = landing[0] + 1;
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                        if (landing[0] <= (int) landTime / 2) {
                                            stand.setGravity(false);
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    stand.setGravity(true);
                                                }
                                            }.runTaskLater(plugin, 20L);
                                        }

                                        if (stand.isOnGround() || stand.isInWater()) {
                                            player.sendMessage(message);
                                            stand.remove();
                                            player.teleport(location);
                                            player.setInvulnerable(false);
                                            Rockets.remove(stand.getUniqueId());
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 0L, 40L);
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                } else {
                    player.sendMessage("§cCould not find a suitable location");
                }
            }
        } else {
            player.sendMessage("§cYou don't have enough money");
        }
    }
}
