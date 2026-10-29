package com.aizistral.enigmaticlegacy.compat;

/**
 * Allocates numeric ids for custom enchantments, skipping ids already taken by
 * other mods or vanilla.
 */
public final class EnchantmentIds {

    private static int next = 64;

    private EnchantmentIds() {
        // NO-OP
    }

    public static synchronized int next() {
        while (next < net.minecraft.enchantment.Enchantment.enchantmentsList.length
            && net.minecraft.enchantment.Enchantment.enchantmentsList[next] != null) {
            next++;
        }

        return next++;
    }

}
