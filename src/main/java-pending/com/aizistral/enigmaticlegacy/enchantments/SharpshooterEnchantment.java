package com.aizistral.enigmaticlegacy.enchantments;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;
import com.aizistral.enigmaticlegacy.compat.Enchantments;

public class SharpshooterEnchantment extends Enchantment {

	public SharpshooterEnchantment(EquipmentSlot... slots) {
		super(Enchantment.EnumRarity.RARE, EnchantmentCategory.CROSSBOW, slots);
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	protected boolean checkCompatibility(final Enchantment ench) {
		return ench != Enchantments.MULTISHOT && ench != Enchantments.PIERCING && super.checkCompatibility(ench);
	}

	@Override
	public boolean canApplyAtEnchantingTable(final ItemStack stack) {
		return this.canEnchant(stack) && stack.getItem() instanceof CrossbowItem;
	}

	@Override
	public boolean isTreasureOnly() {
		return false;
	}

	@Override
	public boolean isCurse() {
		return false;
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

}
