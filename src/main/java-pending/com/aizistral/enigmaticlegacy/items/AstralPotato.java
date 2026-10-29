package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseFood;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.entity.player.EntityPlayer;
import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class AstralPotato extends ItemBaseFood {

	public AstralPotato() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).fireResistant(),
				new FoodProperties.Builder().nutrition(8).saturationMod(1.4F).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralPotato1");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return super.isFoil(pStack);
	}

	@Override
	public void onConsumed(World worldIn, EntityPlayer player, ItemStack food) {
		if (player instanceof EntityPlayerMP playerMP) {
			player.worldObj.playSound(null, CompatPos.of(player), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.5F, 1);
			player.teleportTo(player.getX() + (0.5 - random.nextDouble()) * 4.0, player.getY(), player.getZ() + (0.5 - random.nextDouble()) * 4.0);
		}
	}

}