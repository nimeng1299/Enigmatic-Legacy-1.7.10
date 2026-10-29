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
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class IchorBottle extends ItemBasePotion {

	public IchorBottle() {
		super(getDefaultProperties().stacksTo(1).rarity(EnumRarity.UNCOMMON).fireResistant());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.ichorBottle1");
			//ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.ichorBottle2");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public boolean isFoil(ItemStack pStack) {
		return true;
	}

	@Override
	public void onConsumed(World worldIn, EntityPlayer player, ItemStack potion) {
		if (player instanceof EntityPlayerMP playerMP) {
			SuperpositionHandler.setPersistentBoolean(playerMP, "ConsumedIchorBottle", true);
			SuperpositionHandler.unlockSpecialSlot("charm", playerMP);
			playerMP.worldObj.playSound(null, CompatPos.of(playerMP), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1F);

			double multiplier = 1;

			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,            (int) (1200 * multiplier), 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION,         (int) (800 * multiplier), 4, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE,          (int) (1600 * multiplier), 2, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, (int) (3000 * multiplier), 0, false, true));
		}
	}

	@Override
	public boolean canDrink(World world, EntityPlayer player, ItemStack potion) {
		return true;
	}

}
