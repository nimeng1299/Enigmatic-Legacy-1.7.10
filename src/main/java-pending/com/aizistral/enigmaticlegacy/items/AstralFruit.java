package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseFood;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class AstralFruit extends ItemBaseFood implements ICursed, Vanishable {

	public AstralFruit() {
		super(getDefaultProperties().rarity(EnumRarity.EPIC).fireResistant(),
				new FoodProperties.Builder().nutrition(5).saturationMod(20).alwaysEat().build());
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralFruit1");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.indicateCursedOnesOnly(list);
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return true;
	}

	@Override
	public void onConsumed(World worldIn, EntityPlayer player, ItemStack food) {
		if (player instanceof EntityPlayerMP playerMP) {
			SuperpositionHandler.setPersistentBoolean(playerMP, "ConsumedAstralFruit", true);
			SuperpositionHandler.unlockSpecialSlot("ring", playerMP);
			playerMP.worldObj.playSound(null, CompatPos.of(playerMP), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1F);

			double multiplier = 1;

			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,            (int) (3000 * multiplier), 3, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE,         (int) (3000 * multiplier), 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_BOOST,          (int) (4000 * multiplier), 3, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, (int) (5000 * multiplier), 0, false, true));
		}
	}

	@Override
	public boolean canEat(World world, EntityPlayer player, ItemStack food) {
		return SuperpositionHandler.isTheCursedOne(player);
	}

}
