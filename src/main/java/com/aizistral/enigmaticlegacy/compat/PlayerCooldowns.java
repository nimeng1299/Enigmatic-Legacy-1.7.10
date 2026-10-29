package com.aizistral.enigmaticlegacy.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

/**
 * Item cooldown stand-in for the modern ItemCooldowns API.
 */
public final class PlayerCooldowns {

    private static final Map<EntityPlayer, Map<Item, Long>> COOLDOWNS = new WeakHashMap<>();

    private PlayerCooldowns() {
        // NO-OP
    }

    public static boolean isOnCooldown(EntityPlayer player, Item item) {
        Map<Item, Long> map = COOLDOWNS.get(player);

        if (map == null) return false;

        Long expiry = map.get(item);
        long time = player.worldObj.getTotalWorldTime();

        if (expiry == null) return false;

        if (time >= expiry) {
            map.remove(item);
            return false;
        }

        return true;
    }

    public static void addCooldown(EntityPlayer player, Item item, int ticks) {
        COOLDOWNS.computeIfAbsent(player, key -> new HashMap<>())
            .put(item, player.worldObj.getTotalWorldTime() + ticks);
    }

    public static void removeCooldown(EntityPlayer player, Item item) {
        Map<Item, Long> map = COOLDOWNS.get(player);
        if (map != null) {
            map.remove(item);
        }
    }

}
