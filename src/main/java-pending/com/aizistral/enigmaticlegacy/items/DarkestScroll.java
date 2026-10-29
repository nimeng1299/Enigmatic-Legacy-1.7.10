package com.aizistral.enigmaticlegacy.items;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.EnumRarity;

public class DarkestScroll extends ItemBase {

	public DarkestScroll() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.UNCOMMON).stacksTo(1));
	}

}
