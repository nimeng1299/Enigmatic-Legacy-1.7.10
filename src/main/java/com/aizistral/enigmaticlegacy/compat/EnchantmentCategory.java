package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.enchantment.EnumEnchantmentType;

public enum EnchantmentCategory {

    ARMOR(EnumEnchantmentType.armor),
    ARMOR_FEET(EnumEnchantmentType.armor_feet),
    ARMOR_LEGS(EnumEnchantmentType.armor_legs),
    ARMOR_CHEST(EnumEnchantmentType.armor_torso),
    ARMOR_HEAD(EnumEnchantmentType.armor_head),
    WEAPON(EnumEnchantmentType.weapon),
    DIGGER(EnumEnchantmentType.digger),
    FISHING_ROD(EnumEnchantmentType.fishing_rod),
    BREAKABLE(EnumEnchantmentType.breakable),
    BOW(EnumEnchantmentType.bow),
    WEARABLE(EnumEnchantmentType.armor),
    TRIDENT(EnumEnchantmentType.weapon),
    CROSSBOW(EnumEnchantmentType.bow),
    VANISHABLE(EnumEnchantmentType.breakable);

    private final EnumEnchantmentType vanillaType;

    private EnchantmentCategory(EnumEnchantmentType vanillaType) {
        this.vanillaType = vanillaType;
    }

    public EnumEnchantmentType getVanillaType() {
        return this.vanillaType;
    }

    public boolean canEnchant(net.minecraft.item.ItemStack stack) {
        return this.vanillaType.canEnchantItem(stack.getItem());
    }

}
