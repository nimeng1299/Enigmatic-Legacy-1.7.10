package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LoreFragment extends ItemBase {

	public LoreFragment() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.UNCOMMON).stacksTo(16));

		// TODO Lore Fragment copying recipe
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

		if (!stack.getOrCreateTagElement("display").getString("Name").equals("") || stack.getOrCreateTagElement("display").getList("Lore", 8).size() != 0)
			return;

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.loreFragment4");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

	}

}
