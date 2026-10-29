package com.aizistral.enigmaticlegacy.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

public class WrathEnchantment extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    public WrathEnchantment(EquipmentSlot... slots) {
        super(EnumRarity.rare, EnchantmentCategory.TRIDENT, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 8;
    }

    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.canEnchant(stack) && super.canApplyAtEnchantingTable(stack);
    }

    public boolean checkCompatibility(Enchantment ench) {
        return !(ench instanceof net.minecraft.enchantment.EnchantmentDamage)
            && !(ench instanceof net.minecraft.enchantment.EnchantmentDamage)
            && !(ench instanceof TorrentEnchantment)
            && super.checkCompatibility(ench);
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

    public float bonusDamageByCreature(EntityLivingBase attacker, EntityLivingBase living, int level) {
        return 0F;
    }
}
