package me.none030.mortisrockets.events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.io.File;
import java.util.Objects;

import static me.none030.mortisrockets.methods.SpawningRocket.Rockets;


public class RocketsEvents implements Listener {

    @EventHandler
    public void onDismount(EntityDismountEvent e) {

        Entity entity = e.getDismounted();

        if (e.getEntity() instanceof Player) {
            if (entity instanceof ArmorStand) {
                if (Rockets.contains(entity.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        File file = new File("plugins/MortisRockets/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config");
        assert section != null;

        e.getPlayer().setResourcePack(Objects.requireNonNull(section.getString("url")));

    }
}
