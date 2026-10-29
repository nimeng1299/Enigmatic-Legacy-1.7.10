package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.enchantment.Enchantment;

/**
 * Base class mirroring the modern enchantment constructor signature on top of
 * the 1.7.10 numeric-id based {@link Enchantment}.
 */
public class CompatEnchantment extends Enchantment {

    public enum EnumRarity {
        common,
        uncommon,
        rare,
        very_rare
    }

    public CompatEnchantment(EnumRarity rarity, EnchantmentCategory category, EquipmentSlot... slots) {
        super(EnchantmentIds.next(), weightFor(rarity), category.getVanillaType());
    }

    public CompatEnchantment(int weight, EnchantmentCategory category, EquipmentSlot... slots) {
        super(EnchantmentIds.next(), weight, category.getVanillaType());
    }

    private static int weightFor(EnumRarity rarity) {
        if (rarity == EnumRarity.common) return 10;
        if (rarity == EnumRarity.uncommon) return 5;
        if (rarity == EnumRarity.rare) return 2;
        return 1;
    }

    @Override
    public boolean canApply(net.minecraft.item.ItemStack stack) {
        return this.canEnchant(stack) && super.canApply(stack);
    }

    public boolean canEnchant(net.minecraft.item.ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyTogether(Enchantment other) {
        return this.checkCompatibility(other) && super.canApplyTogether(other);
    }

    protected boolean checkCompatibility(Enchantment other) {
        return true;
    }

    public boolean canApplyAtEnchantingTable(net.minecraft.item.ItemStack stack) {
        return true;
    }

    public boolean isAllowedOnBooks() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public int getMinEnchantability(int level) {
        return this.getMinCost(level);
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMaxCost(level);
    }

    public int getMinCost(int level) {
        return 1 + level * 10;
    }

    public int getMaxCost(int level) {
        return this.getMinCost(level) + 5;
    }

}
