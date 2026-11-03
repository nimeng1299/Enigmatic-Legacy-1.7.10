package com.aizistral.enigmaticlegacy.client;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-side mirror of {@link com.aizistral.enigmaticlegacy.compat.PlayerCooldowns},
 * kept in sync through {@link com.aizistral.enigmaticlegacy.packets.clients.PacketSyncCooldown}.
 */
@SideOnly(Side.CLIENT)
public final class ClientCooldowns {

    private static final Map<Item, Entry> COOLDOWNS = new HashMap<>();
    private static final int TIME_CORRECTION_TOLERANCE = 100;

    private ClientCooldowns() {
        // NO-OP
    }

    public static void setCooldown(Item item, int ticks) {
        if (item == null) return;

        if (ticks <= 0) {
            COOLDOWNS.remove(item);
        } else {
            COOLDOWNS.put(item, new Entry(getTotalWorldTime() + ticks, ticks));
        }
    }

    public static boolean isOnCooldown(Item item) {
        return getRemainingFraction(item) > 0.0D;
    }

    public static double getRemainingFraction(Item item) {
        Entry entry = COOLDOWNS.get(item);

        if (entry == null) return 0.0D;

        long remaining = entry.endTick - getTotalWorldTime();

        // A cooldown that ends further in the future than it is long is a leftover
        // from another world session, where total world time restarted.
        if (remaining <= 0 || remaining > entry.durationTicks + TIME_CORRECTION_TOLERANCE) {
            COOLDOWNS.remove(item);
            return 0.0D;
        }

        return Math.min(1.0D, (double) remaining / entry.durationTicks);
    }

    private static long getTotalWorldTime() {
        World world = Minecraft.getMinecraft().theWorld;
        return world != null ? world.getTotalWorldTime() : 0L;
    }

    private static final class Entry {

        private final long endTick;
        private final int durationTicks;

        private Entry(long endTick, int durationTicks) {
            this.endTick = endTick;
            this.durationTicks = durationTicks;
        }

    }

}
