package com.aizistral.enigmaticlegacy.enchantments;

import static com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack.getRegisteredAttackStregth;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;

public class SlayerEnchantment extends Enchantment {

	public SlayerEnchantment(EquipmentSlot... slots) {
		super(Enchantment.EnumRarity.COMMON, EnchantmentCategory.WEAPON, slots);
	}

	@Override
	public int getMinCost(int enchantmentLevel) {
		return 5 + (enchantmentLevel - 1) * 8;
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
		return !(ench instanceof DamageEnchantment) && super.checkCompatibility(ench);
	}

	@Override
	public boolean canEnchant(ItemStack stack) {
		return OmniconfigHandler.isItemEnabled(this) && (stack.getItem() instanceof ItemAxe ? true : stack.canApplyAtEnchantingTable(this));
	}

	@Override
	public boolean isAllowedOnBooks() {
		return OmniconfigHandler.isItemEnabled(this);
	}

	@Override
	public boolean isDiscoverable() {
		return OmniconfigHandler.isItemEnabled(this);
	}

	public float bonusDamageByCreature(EntityLivingBase attacker, EntityLivingBase living, int level) {
		float calculated = living instanceof IMob ? level * 1.5F : 0F;
		calculated*= getRegisteredAttackStregth(attacker);
		return calculated;
	}
}
