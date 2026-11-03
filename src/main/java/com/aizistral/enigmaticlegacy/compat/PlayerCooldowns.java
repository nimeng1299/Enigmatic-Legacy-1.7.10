package com.aizistral.enigmaticlegacy.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncCooldown;

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
        Map<Item, Long> map = COOLDOWNS.computeIfAbsent(player, key -> new HashMap<>());

        if (ticks <= 0) {
            map.remove(item);
        } else {
            map.put(item, player.worldObj.getTotalWorldTime() + ticks);
        }

        if (player instanceof EntityPlayerMP) {
            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.PLAYER.with(() -> (EntityPlayerMP) player),
                new PacketSyncCooldown(item, Math.max(ticks, 0)));
        }
    }

    public static void removeCooldown(EntityPlayer player, Item item) {
        Map<Item, Long> map = COOLDOWNS.get(player);
        if (map != null) {
            map.remove(item);
        }
    }

}
