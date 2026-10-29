package com.aizistral.enigmaticlegacy.enchantments;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.Enchantments;

public class NemesisCurse extends Enchantment {

	public NemesisCurse(final EquipmentSlot... slots) {
		super(Enchantment.EnumRarity.RARE, EnchantmentCategory.WEAPON, slots);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 25;
	}

	@Override
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

	@Override
	public boolean canEnchant(ItemStack stack) {
		return OmniconfigHandler.isItemEnabled(this) && (super.canEnchant(stack) || Enchantments.SHARPNESS.canEnchant(stack));
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return true;
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
	protected boolean checkCompatibility(Enchantment ench) {
		return super.checkCompatibility(ench);
	}
}
