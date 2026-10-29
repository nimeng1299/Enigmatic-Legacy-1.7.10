package com.aizistral.enigmaticlegacy.compat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

/**
 * Modern enchantment constants mapped onto 1.7.10 names.
 */
public final class Enchantments {

    public static final Enchantment PROTECTION = Enchantment.protection;
    public static final Enchantment FIRE_PROTECTION = Enchantment.fireProtection;
    public static final Enchantment FEATHER_FALLING = Enchantment.featherFalling;
    public static final Enchantment BLAST_PROTECTION = Enchantment.blastProtection;
    public static final Enchantment PROJECTILE_PROTECTION = Enchantment.projectileProtection;
    public static final Enchantment RESPIRATION = Enchantment.respiration;
    public static final Enchantment AQUA_AFFINITY = Enchantment.aquaAffinity;
    public static final Enchantment THORNS = Enchantment.thorns;
    public static final Enchantment DEPTH_STRIDER = null;
    public static final Enchantment FROST_WALKER = null;
    public static final Enchantment BINDING_CURSE = null;
    public static final Enchantment SHARPNESS = Enchantment.sharpness;
    public static final Enchantment SMITE = Enchantment.smite;
    public static final Enchantment BANE_OF_ARTHROPODS = Enchantment.baneOfArthropods;
    public static final Enchantment KNOCKBACK = Enchantment.knockback;
    public static final Enchantment FIRE_ASPECT = Enchantment.fireAspect;
    public static final Enchantment LOOTING = Enchantment.looting;
    public static final Enchantment SWEEPING = null;
    public static final Enchantment EFFICIENCY = Enchantment.efficiency;
    public static final Enchantment SILK_TOUCH = Enchantment.silkTouch;
    public static final Enchantment UNBREAKING = Enchantment.unbreaking;
    public static final Enchantment FORTUNE = Enchantment.fortune;
    public static final Enchantment POWER = Enchantment.power;
    public static final Enchantment PUNCH = Enchantment.punch;
    public static final Enchantment FLAME = Enchantment.flame;
    public static final Enchantment INFINITY = Enchantment.infinity;
    public static final Enchantment LUCK_OF_THE_SEA = Enchantment.field_151370_z;
    public static final Enchantment LURE = Enchantment.field_151369_A;
    public static final Enchantment LOYALTY = null;
    public static final Enchantment IMPALING = null;
    public static final Enchantment RIPTIDE = null;
    public static final Enchantment CHANNELING = null;
    public static final Enchantment MULTISHOT = null;
    public static final Enchantment QUICK_CHARGE = null;
    public static final Enchantment PIERCING = null;
    public static final Enchantment MENDING = null;
    public static final Enchantment VANISHING_CURSE = null;

    private Enchantments() {
        // NO-OP
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> result = new HashMap<>();
        Map<Integer, Integer> raw = EnchantmentHelper.getEnchantments(stack);

        for (Map.Entry<Integer, Integer> entry : raw.entrySet()) {
            Enchantment enchantment = Enchantment.enchantmentsList[entry.getKey()];

            if (enchantment != null) {
                result.put(enchantment, entry.getValue());
            }
        }

        return result;
    }

    public static int getEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        if (enchantment == null || stack == null) return 0;

        return EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, stack);
    }

    public static void setEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        Map<Integer, Integer> raw = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (entry.getKey() != null) {
                raw.put(entry.getKey().effectId, entry.getValue());
            }
        }

        EnchantmentHelper.setEnchantments(raw, stack);
    }

    public static boolean isCurse(Enchantment enchantment) {
        return enchantment instanceof com.aizistral.enigmaticlegacy.enchantments.NemesisCurse
            || enchantment instanceof com.aizistral.enigmaticlegacy.enchantments.EternalBindingCurse
            || enchantment instanceof com.aizistral.enigmaticlegacy.enchantments.SorrowCurse;
    }

    public static boolean canEnchant(Enchantment enchantment, ItemStack stack) {
        return enchantment != null && enchantment.canApply(stack);
    }

    public static boolean isCompatibleWith(Enchantment first, Enchantment second) {
        return first == null || second == null || first.canApplyTogether(second);
    }

    public static boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == null) return false;

        if (enchantment instanceof CompatEnchantment)
            return ((CompatEnchantment) enchantment).canApplyAtEnchantingTable(stack);

        return enchantment.canApply(stack);
    }

}
