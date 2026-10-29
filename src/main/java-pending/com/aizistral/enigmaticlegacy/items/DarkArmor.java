package com.aizistral.enigmaticlegacy.items;

import javax.annotation.Nonnull;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseArmor;

import net.minecraft.entity.Entity;
import com.aizistral.enigmaticlegacy.compat.EquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;

public class DarkArmor extends ItemBaseArmor {
	public final String TEXTURE = EnigmaticLegacy.MODID + ":textures/models/armor/dark_armor.png";

	public DarkArmor(ArmorMaterial materialIn, ItemArmor.Type slot) {
		super(materialIn, slot, ItemBaseArmor.getDefaultProperties().rarity(EnumRarity.RARE).fireResistant());
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return null;
	}

	/*
	@Override
	@OnlyIn(Dist.CLIENT)
	public HumanoidModel<?> provideArmorModelForSlot(EquipmentSlot slot, HumanoidModel<?> original) {
		DarkArmorModel model = new DarkArmorModel(slot);
		return model;
	}
	 */

	@Nonnull
	@Override
	public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return this.TEXTURE;
	}

}
