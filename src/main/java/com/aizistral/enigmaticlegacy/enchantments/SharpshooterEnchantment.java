package com.aizistral.enigmaticlegacy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

public class SharpshooterEnchantment extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    public SharpshooterEnchantment(EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.CROSSBOW, slots);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    protected boolean checkCompatibility(final Enchantment ench) {
        return ench != Enchantments.MULTISHOT && ench != Enchantments.PIERCING && super.checkCompatibility(ench);
    }

    @Override
    public boolean canApplyAtEnchantingTable(final ItemStack stack) {
        return this.canEnchant(stack) && stack.getItem() == net.minecraft.init.Items.bow;
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public boolean isDiscoverable() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public boolean canEnchant(ItemStack stack) {
        return OmniconfigHandler.isItemEnabled(this);
    }

}
