package com.aizistral.enigmaticlegacy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

public class NemesisCurse extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    public NemesisCurse(final EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.WEAPON, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int enchantmentLevel) {
        return 50;
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    public boolean canEnchant(ItemStack stack) {
        return OmniconfigHandler.isItemEnabled(this) && (super.canEnchant(stack)
            || com.aizistral.enigmaticlegacy.compat.Enchantments.canEnchant(Enchantments.SHARPNESS, stack));
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public boolean isDiscoverable() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    protected boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench);
    }
}
