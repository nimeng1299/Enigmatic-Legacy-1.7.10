package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.enchantment.Enchantment;

/**
 * Allocates numeric ids for custom enchantments, skipping ids already taken by
 * other mods or vanilla.
 * <p>
 * 1.7.10 has no enchantment registry, only the fixed {@code Enchantment.enchantmentsList} array, and
 * the {@link Enchantment} constructor throws {@code IllegalArgumentException("Duplicate enchantment
 * id!")} as soon as two mods claim the same slot. Allocating upwards from the first free id above
 * vanilla therefore kills any addon that hard-codes ids in that band: WitchingGadgets claims 64/65/71
 * and ThaumicExploration 77-79 in GTNH packs, so both crash during init and take the whole game down.
 * The resulting report is nearly unreadable too - the throw site is {@code Enchantment.<init>}, and
 * log4j fails to resolve that frame while logging the crash, which surfaces as a bare
 * {@code NoClassDefFoundError: net/minecraft/enchantment/Enchantment}.
 * <p>
 * The band is thus claimed from the top of the legacy 0-255 range downwards: pack addons cluster in
 * 64-200, and everything already registered at allocation time is skipped. Ids above 255 are never
 * used, so this stays valid both with and without EndlessIDs extending the array.
 */
public final class EnchantmentIds {

    /**
     * Highest id this mod will ever claim - the top of the vanilla 1.7.10 array.
     */
    private static final int HIGHEST = 255;

    /**
     * Never go below this; running out of slots is a pack problem that should fail loudly.
     */
    private static final int LOWEST = 128;

    private static int next = HIGHEST;

    private EnchantmentIds() {
        // NO-OP
    }

    public static synchronized int next() {
        // Be defensive in case another mod replaced the array with a shorter one.
        int highestUsable = Math.min(HIGHEST, Enchantment.enchantmentsList.length - 1);

        if (next > highestUsable) {
            next = highestUsable;
        }

        while (next >= LOWEST && Enchantment.enchantmentsList[next] != null) {
            next--;
        }

        if (next < LOWEST) {
            throw new IllegalStateException(
                "No free enchantment id left in " + LOWEST
                    + "-"
                    + highestUsable
                    + "; the pack exhausted that band, widen the range in EnchantmentIds");
        }

        return next--;
    }

}
