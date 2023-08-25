package com.mortisdevelopment.mortisrockets.managers;

import com.mortisdevelopment.mortisrockets.rockets.Rocket;
import com.mortisdevelopment.mortisrockets.rockets.RocketLocation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RocketCommand implements TabExecutor {

    private final Manager manager;

    public RocketCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("send")) {
            if (!sender.hasPermission("rockets.send")) {
                sender.sendMessage(manager.getRocketManager().getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(manager.getRocketManager().getMessage("SEND_USAGE"));
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(manager.getRocketManager().getMessage("INVALID_TARGET"));
                return false;
            }
            Rocket rocket = manager.getRocketManager().getRocketById().get(args[2]);
            if (rocket == null) {
                sender.sendMessage(manager.getRocketManager().getMessage("INVALID_ROCKET"));
                return false;
            }
            if (args.length >= 5) {
                if (!rocket.isSpecificLocation()) {
                    sender.sendMessage(manager.getRocketManager().getMessage("NO_SPECIFIC_LOCATION"));
                    return false;
                }
                double x;
                double z;
                try {
                    x = Double.parseDouble(args[3]);
                    z = Double.parseDouble(args[4]);
                }catch (NumberFormatException exp) {
                    return false;
                }
                RocketLocation rocketLocation = new RocketLocation(x, z);
                return manager.getRocketManager().travel(rocket, target, rocketLocation);
            }else {
                return manager.getRocketManager().travel(rocket, target);
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(manager.getRocketManager().getMessage("NO_CONSOLE"));
            return false;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("travel")) {
            if (!player.hasPermission("rockets.travel")) {
                sender.sendMessage(manager.getRocketManager().getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(manager.getRocketManager().getMessage("TRAVEL_USAGE"));
                return false;
            }
            Rocket rocket = manager.getRocketManager().getRocketById().get(args[1]);
            if (rocket == null) {
                sender.sendMessage(manager.getRocketManager().getMessage("INVALID_ROCKET"));
                return false;
            }
            if (args.length >= 4) {
                if (!rocket.isSpecificLocation()) {
                    sender.sendMessage(manager.getRocketManager().getMessage("NO_SPECIFIC_LOCATION"));
                    return false;
                }
                double x;
                double z;
                try {
                    x = Double.parseDouble(args[3]);
                    z = Double.parseDouble(args[4]);
                }catch (NumberFormatException exp) {
                    return false;
                }
                RocketLocation rocketLocation = new RocketLocation(x, z);
                return manager.getRocketManager().travel(rocket, player, rocketLocation);
            }else {
                return manager.getRocketManager().travel(rocket, player);
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("send");
            arguments.add("travel");
            return arguments;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("travel")) {
                return new ArrayList<>(manager.getRocketManager().getRocketById().keySet());
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("send")) {
                return new ArrayList<>(manager.getRocketManager().getRocketById().keySet());
            }
        }
        return null;
    }
}
