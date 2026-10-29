package com.aizistral.enigmaticlegacy.enchantments;

import java.util.ArrayList;
import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import com.aizistral.enigmaticlegacy.compat.EnchantmentCategory;

public class SorrowCurse extends Enchantment {
	private final List<Potion> debuffList = new ArrayList<>();

	public SorrowCurse(EquipmentSlot... slots) {
		super(Enchantment.EnumRarity.RARE, EnchantmentCategory.ARMOR, slots);

		this.debuffList.add(MobEffects.BLINDNESS);
		this.debuffList.add(MobEffects.CONFUSION);
		this.debuffList.add(MobEffects.WEAKNESS);
		this.debuffList.add(MobEffects.MOVEMENT_SLOWDOWN);
		this.debuffList.add(MobEffects.DIG_SLOWDOWN);
		this.debuffList.add(MobEffects.HUNGER);
	}

	public void maybeApplyDebuff(EntityPlayer player, float damage) {
		if (player.getRandom().nextDouble() < 0.1) {
			float severity = damage > 4 ? damage / 4 : 1;
			severity *= 0.5F + player.getRandom().nextFloat();

			int amplifier = (int) (severity / 2);

			if (amplifier > 3) {
				amplifier = 3;
			}

			Potion debuff = this.debuffList.get(player.getRandom().nextInt(this.debuffList.size()));
			PotionEffect instance = new PotionEffect(debuff, (int) (300 * severity), amplifier, false, true);

			player.addPotionEffect(instance);
		}
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
		return OmniconfigHandler.isItemEnabled(this) && super.canEnchant(stack);
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
		return true;
	}

	@Override
	protected boolean checkCompatibility(Enchantment ench) {
		return super.checkCompatibility(ench);
	}

}
