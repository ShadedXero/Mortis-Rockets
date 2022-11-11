package me.none030.mortisrockets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.none030.mortisrockets.methods.SpawningRocket.SpawnRocket;

public class RocketsCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        File file = new File("plugins/MortisRockets/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config");
        assert section != null;

        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                System.out.println("Usage: rocket <args> <rocket-id> [x] [z]");
                return true;
            }
            if (args[0].equalsIgnoreCase("send")) {
                if (args.length == 3 || args.length == 5) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        if (args.length == 3) {
                            if (section.contains(args[2])) {
                                SpawnRocket(target, args[2]);
                                return true;
                            } else {
                                System.out.println("Invalid Rocket");
                            }
                        }
                        if (args.length == 5) {
                            if (section.contains(args[2])) {
                                if (section.getBoolean(args[2] + ".specific-location")) {
                                    SpawnRocket(target, args[2], Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                                    return true;
                                } else {
                                    System.out.println("Can not use Specific Location");
                                }
                            } else {
                                System.out.println("Invalid Rocket");
                            }
                        }
                    } else {
                        System.out.println("Invalid Target");
                    }
                } else {
                    System.out.println("Usage: rocket <args> <rocket-id> [x] [z]");
                }
            }

        } else {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage("§cUsage: /rocket <args> <rocket-id> [x] [z]");
                return true;
            }
            if (player.hasPermission("rockets.travel")) {
                if (args[0].equalsIgnoreCase("travel")) {
                    if (args.length == 2 || args.length == 4) {
                        if (args.length == 2) {
                            if (section.contains(args[1])) {
                                SpawnRocket(player, args[1]);
                                return true;
                            } else {
                                player.sendMessage("§cInvalid Rocket");
                            }
                        }
                        if (args.length == 4) {
                            if (section.contains(args[1])) {
                                if (section.getBoolean(args[1] + ".specific-location")) {
                                    SpawnRocket(player, args[1], Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                                    return true;
                                } else {
                                    player.sendMessage("§cCan not use Specific Location");
                                }
                            } else {
                                player.sendMessage("§cInvalid Rocket");
                            }
                        }
                    } else {
                        player.sendMessage("§cUsage: /rocket <args> <rocket-id> [x] [z]");
                    }
                }
            } else {
                player.sendMessage("§cYou don't have permission to use this");
            }
            if (player.hasPermission("rockets.send")) {
                if (args[0].equalsIgnoreCase("send")) {
                    if (args.length == 3 || args.length == 5) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            if (args.length == 3) {
                                if (section.contains(args[2])) {
                                    SpawnRocket(target, args[2]);
                                    return true;
                                } else {
                                    player.sendMessage("§cInvalid Rocket");
                                }
                            }
                            if (args.length == 5) {
                                if (section.contains(args[2])) {
                                    if (section.getBoolean(args[2] + ".specific-location")) {
                                        SpawnRocket(target, args[2], Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                                        return true;
                                    } else {
                                        player.sendMessage("§cCan not use Specific Location");
                                    }
                                } else {
                                    player.sendMessage("§cInvalid Rocket");
                                }
                            }
                        } else {
                            player.sendMessage("§cInvalid Player");
                        }
                    } else {
                        player.sendMessage("§cUsage: /rocket <args> <rocket-id> [x] [z]");
                    }
                }
            } else {
                player.sendMessage("§cYou don't have permission to use this");
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        File file = new File("plugins/MortisRockets/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config");
        assert section != null;

        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("send");
            arguments.add("travel");

            return arguments;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("travel")) {

                List<String> arguments = new ArrayList<>(section.getKeys(false));
                arguments.remove("url");
                arguments.remove("rocket");
                arguments.remove("item");

                return arguments;
            }
            if (args[0].equalsIgnoreCase("send")) {
                List<String> arguments = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    arguments.add(player.getName());
                }

                return arguments;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("send")) {

                List<String> arguments = new ArrayList<>(section.getKeys(false));
                arguments.remove("url");
                arguments.remove("rocket");
                arguments.remove("item");

                return arguments;
            }
        }

        return null;
    }
}
