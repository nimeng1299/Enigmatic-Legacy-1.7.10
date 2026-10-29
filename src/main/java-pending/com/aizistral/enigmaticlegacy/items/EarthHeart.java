package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ITaintable;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;

public class EarthHeart extends ItemBase implements ITaintable, Vanishable {

	public EarthHeart() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.UNCOMMON).stacksTo(1));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (entityIn instanceof EntityPlayer && !entityIn.worldObj.isClientSide) {
			EntityPlayer player = (EntityPlayer) entityIn;
			this.handleTaintable(stack, player);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (this.isTainted(stack)) {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.tainted1");
		}
	}

}
