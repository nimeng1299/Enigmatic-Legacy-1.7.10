package com.aizistral.enigmaticlegacy.enchantments;

import static com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack.getRegisteredAttackStregth;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

public class SlayerEnchantment extends com.aizistral.enigmaticlegacy.compat.CompatEnchantment {

    public SlayerEnchantment(EquipmentSlot... slots) {
        super(EnumRarity.common, EnchantmentCategory.WEAPON, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 5 + (enchantmentLevel - 1) * 8;
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
        return !(ench instanceof net.minecraft.enchantment.EnchantmentDamage) && super.checkCompatibility(ench);
    }

    public boolean canEnchant(ItemStack stack) {
        return OmniconfigHandler.isItemEnabled(this);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public boolean isDiscoverable() {
        return OmniconfigHandler.isItemEnabled(this);
    }

    public float bonusDamageByCreature(EntityLivingBase attacker, EntityLivingBase living, int level) {
        float calculated = living instanceof IMob ? level * 1.5F : 0F;
        calculated *= getRegisteredAttackStregth(attacker);
        return calculated;
    }
}
