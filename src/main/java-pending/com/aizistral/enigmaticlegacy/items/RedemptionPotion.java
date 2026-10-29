package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBasePotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedemptionPotion extends ItemBasePotion {

	public RedemptionPotion() {
		super(getDefaultProperties().stacksTo(1).rarity(EnumRarity.EPIC));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.redemptionPotion1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.redemptionPotion2");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public void onConsumed(World worldIn, EntityPlayer player, ItemStack potion) {
		EnigmaticItems.FORBIDDEN_FRUIT.defineConsumedFruit(player, false);
	}

	@Override
	public boolean canDrink(World world, EntityPlayer player, ItemStack potion) {
		return EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player);
	}



}
