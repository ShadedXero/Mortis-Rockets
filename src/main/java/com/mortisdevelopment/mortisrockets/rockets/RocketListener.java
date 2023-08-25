package com.mortisdevelopment.mortisrockets.rockets;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class RocketListener implements Listener {

    private final RocketManager rocketManager;

    public RocketListener(RocketManager rocketManager) {
        this.rocketManager = rocketManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!rocketManager.getSettings().hasUrl()) {
            return;
        }
        e.getPlayer().setResourcePack(rocketManager.getSettings().getUrl());
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (!(e.getDismounted() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getDismounted();
        if (!rocketManager.getTraveling().contains(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        if (!rocketManager.getTraveling().contains(player.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
    }
}
