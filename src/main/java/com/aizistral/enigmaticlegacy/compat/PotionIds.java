package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.potion.Potion;

/**
 * Allocates free legacy potion ids for the mod's custom effects.
 */
public final class PotionIds {

    private static int next = 24;

    private PotionIds() {
        // NO-OP
    }

    public static synchronized int next() {
        while (next < Potion.potionTypes.length && Potion.potionTypes[next] != null) {
            next++;
        }

        int id = next++;

        while (id >= Potion.potionTypes.length) {
            // 1.7.10 caps potion ids at 31; wrap around and reuse a free slot.
            id = 24 + (id % 8);
            if (Potion.potionTypes[id] == null) break;
        }

        return id;
    }

}
