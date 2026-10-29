package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.loot.LootContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;

public class AvariceScroll extends ItemBaseCurio implements ICursed {
	public static Omniconfig.IntParameter emeraldChance;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("AvariceScroll");

		emeraldChance = builder
				.comment("Chance to get an Emerald when slaying any mob with Pact of Infinite Avarice equipped.")
				.max(100).getInt("EmeraldChance", 15);

		builder.popPrefix();
	}
	public AvariceScroll() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll1", EnumChatFormatting.GOLD, 1);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll4");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll5", EnumChatFormatting.GOLD, emeraldChance.getValue() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll6");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.avariceScroll7");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(list);
	}

	@Override
	public boolean canEquip(SlotContext context, ItemStack stack) {
		return super.canEquip(context, stack) && context.entity() instanceof EntityPlayer player && SuperpositionHandler.isTheCursedOne(player);
	}

	@Override
	public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {
		return super.getFortuneLevel(slotContext, lootContext, curio) + 1;
	}

	@Override
	public boolean makesPiglinsNeutral(ItemStack stack, EntityLivingBase wearer) {
		return true;
	}

	@Override
	public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
		return true;
	}

}
