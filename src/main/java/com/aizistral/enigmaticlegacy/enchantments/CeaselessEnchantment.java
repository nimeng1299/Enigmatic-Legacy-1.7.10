package com.aizistral.enigmaticlegacy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

public class CeaselessEnchantment extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    public static Omniconfig.BooleanParameter allowNoArrow;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("CeaselessEnchantment");

        allowNoArrow = builder.comment(
            "Whether or not crossbows with Ceaseless should be able to shoot basic arrows even if there are none in player's inventory.")
            .getBoolean("AllowNoArrow", true);

        builder.popPrefix();
    }

    public CeaselessEnchantment(final EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.CROSSBOW, slots);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    protected boolean checkCompatibility(final Enchantment ench) {
        return super.checkCompatibility(ench);
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
