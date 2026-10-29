package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBasePotion;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RecallPotion extends ItemBasePotion {

	public RecallPotion() {
		super(getDefaultProperties().stacksTo(1).rarity(EnumRarity.RARE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.recallPotion1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.recallPotion2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.recallPotion3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public void onConsumed(World worldIn, EntityPlayer player, ItemStack potion) {
		if (player instanceof EntityPlayerMP) {
			SuperpositionHandler.backToSpawn((EntityPlayerMP)player);
		}
	}

	@Override
	public boolean canDrink(World world, EntityPlayer player, ItemStack potion) {
		return EnigmaticLegacy.PROXY.isInVanillaDimension(player);
	}

}
