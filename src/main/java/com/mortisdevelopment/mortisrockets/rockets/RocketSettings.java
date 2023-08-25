package com.mortisdevelopment.mortisrockets.rockets;

import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@Getter
public class RocketSettings {

    private final String url;
    private final ItemStack item;
    private final int launchingTime;
    private final int launchingSpeed;
    private final int landingDistance;

    public RocketSettings(String url, ItemStack item, int launchingTime, int launchingSpeed, int landingDistance) {
        this.url = url;
        this.item = item;
        this.launchingTime = launchingTime;
        this.launchingSpeed = launchingSpeed;
        this.landingDistance = landingDistance;
    }

    public boolean hasUrl() {
        return url != null;
    }

    public ArmorStand getRocket(Player player) {
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
        return stand;
    }
}
