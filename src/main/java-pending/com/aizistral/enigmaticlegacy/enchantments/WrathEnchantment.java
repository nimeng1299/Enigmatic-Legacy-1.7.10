package com.aizistral.enigmaticlegacy.enchantments;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.entity.MobType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;

public class WrathEnchantment extends Enchantment {

	public WrathEnchantment(EquipmentSlot... slots) {
		super(Enchantment.EnumRarity.RARE, EnchantmentCategory.TRIDENT, slots);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 1 + (enchantmentLevel - 1) * 8;
	}

	@Override
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

	@Override
	public boolean checkCompatibility(Enchantment ench) {
		return !(ench instanceof DamageEnchantment) && !(ench instanceof TridentImpalerEnchantment) && !(ench instanceof TorrentEnchantment) && super.checkCompatibility(ench);
	}

	@Override
	public boolean isAllowedOnBooks() {
		return OmniconfigHandler.isItemEnabled(this);
	}

	@Override
	public boolean isDiscoverable() {
		return OmniconfigHandler.isItemEnabled(this);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return OmniconfigHandler.isItemEnabled(this) && stack.canApplyAtEnchantingTable(this);
	}

	public float bonusDamageByCreature(EntityLivingBase attacker, EntityLivingBase living, int level) {
		return 0F;
	}

	@Override
	public float getDamageBonus(int level, MobType creatureType) {
		return level * 1.25F;
	}
}
