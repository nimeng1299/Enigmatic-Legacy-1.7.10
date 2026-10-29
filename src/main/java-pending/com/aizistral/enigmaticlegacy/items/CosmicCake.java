package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.GenericBlockItem;
import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;

public class CosmicCake extends GenericBlockItem {

	public CosmicCake() {
		super(EnigmaticBlocks.COSMIC_CAKE, getDefaultProperties().stacksTo(1).rarity(EnumRarity.EPIC));
	}

	@Override
	public void appendHoverText(ItemStack stack, World level, List<Component> tooltip, TooltipFlag flag) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.cosmicCake1");
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.cosmicCake2");
		} else {
			ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
		}
	}

}
