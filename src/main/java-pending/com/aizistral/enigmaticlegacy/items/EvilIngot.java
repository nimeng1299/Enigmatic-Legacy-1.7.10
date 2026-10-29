package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;

public class EvilIngot extends ItemBase implements ICursed {

	public EvilIngot() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(8).fireResistant());
	}

	@Override
	public boolean canBeHurtBy(DamageSource source) {
		return !SuperpositionHandler.isExplosion(source);
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
		//		if (Screen.hasShiftDown()) {
		//			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.evilEssence1");
		//			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.evilEssence2");
		//		} else {
		//			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		//		}

		//		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(list);
	}

}