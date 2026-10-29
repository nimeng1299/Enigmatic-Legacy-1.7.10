package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;

public class TwistedHeart extends ItemBase implements ICursed, Vanishable {

	public TwistedHeart() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		ItemLoreHelper.indicateCursedOnesOnly(tooltip);
	}

}
