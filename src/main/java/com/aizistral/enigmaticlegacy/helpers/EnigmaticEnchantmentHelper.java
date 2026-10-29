package com.aizistral.enigmaticlegacy.helpers;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.registries.EnigmaticEnchantments;

public class EnigmaticEnchantmentHelper {

    public static boolean hasCustomCrossbowEnchantments(ItemStack crossbowStack) {
        return (EnchantmentHelper.getEnchantmentLevel((EnigmaticEnchantments.CEASELESS).effectId, crossbowStack) > 0
            || EnchantmentHelper.getEnchantmentLevel((EnigmaticEnchantments.SHARPSHOOTER).effectId, crossbowStack) > 0);
    }

    public static boolean hasSharpshooterEnchantment(ItemStack stack) {
        return EnigmaticEnchantmentHelper.getSharpshooterLevel(stack) > 0;
    }

    public static boolean hasCeaselessEnchantment(ItemStack stack) {
        return EnigmaticEnchantmentHelper.getCeaselessLevel(stack) > 0;
    }

    public static boolean hasNemesisCurseEnchantment(ItemStack stack) {
        return EnigmaticEnchantmentHelper.getNemesisCurseLevel(stack) > 0;
    }

    public static int getSharpshooterLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel((EnigmaticEnchantments.SHARPSHOOTER).effectId, stack);
    }

    public static int getCeaselessLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel((EnigmaticEnchantments.CEASELESS).effectId, stack);
    }

    public static int getNemesisCurseLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel((EnigmaticEnchantments.NEMESIS).effectId, stack);
    }
}
