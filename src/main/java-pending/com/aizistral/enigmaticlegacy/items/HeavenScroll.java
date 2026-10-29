package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;

public class HeavenScroll extends ItemBaseCurio {
	public static Omniconfig.DoubleParameter xpCostModifier;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("HeavenScroll");

		xpCostModifier = builder
				.comment("Multiplier for experience consumption by Gift of the Heaven.")
				.getDouble("XPCostModifier", 1.0);

		builder.popPrefix();
	}

	public Map<EntityPlayer, Integer> flyMap = new WeakHashMap<EntityPlayer, Integer>();
	public final double baseXpConsumptionProbability = 0.025D/2D;

	public HeavenScroll() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.EPIC));
	}

	public HeavenScroll(Properties properties) {
		super(properties);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.heavenTome1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.heavenTome2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.heavenTome3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.heavenTome4");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	// Don't charge xp if the player is in creative mode for some reason
	// but do only charge them when they are actually flying.
	protected boolean shouldCheckXpDrain(EntityPlayer player) {
		return !player.isCreative() && player.getAbilities().flying;
	}

	protected boolean canFly(EntityPlayer player, boolean inRangeCheckedAndSucceeded)
	{
		return ExperienceHelper.getPlayerXP(player) > 0 && (inRangeCheckedAndSucceeded || SuperpositionHandler.isInBeaconRange(player));
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		if (context.entity().worldObj.isClientSide)
			return;

		if (context.entity() instanceof EntityPlayer player) {
			//since we don't need to check if in range of beacon here, we can run this xp check every frame.
			if (this.shouldCheckXpDrain(player) && Math.random() <= this.baseXpConsumptionProbability) {
				//hook into xp cost modifier from config.
				ExperienceHelper.drainPlayerXP(player, (int) xpCostModifier.getValue());
			}

			// we pass false here, because we only want heaven scroll to check beacon
			// range once per 20 ticks which is handled in the below function.
			this.handleFlight(player, false);
		}
	}

	protected void handleFlight(EntityPlayer player, boolean inRangeCheckedAndSucceeded) {
		//don't check in range every tick as it is expensive. Particularly for multiple people.
		//instead, check once per second, based on the player's tick count
		//If we're here from fabulous scroll, we automatically know we can skip this
		if (player.ticksExisted % 20 != 0)
			return;

		try {
			if (this.canFly(player, inRangeCheckedAndSucceeded)) {
				player.getAbilities().mayfly = true;
				//since we're only updating once per 20 ticks, we can leave this here as we won't be spamming update packets
				player.onUpdateAbilities();
				//reduced from 100 down to 5, as we now only run this code once per second.
				this.flyMap.put(player, 5);

			} else if (this.flyMap.get(player) > 1) {
				this.flyMap.put(player, this.flyMap.get(player)-1);
			} else if (this.flyMap.get(player) == 1) {
				if (!player.isCreative()) {
					player.getAbilities().mayfly = false;
					player.getAbilities().flying = false;
					player.onUpdateAbilities();
					player.addPotionEffect(new PotionEffect(MobEffects.SLOW_FALLING, 200, 0, true, false));
				}

				this.flyMap.put(player, 0);
			}

		} catch (NullPointerException ex) {
			ex.printStackTrace();
			this.flyMap.put(player, 0);
		}
	}

	@Override
	public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player) {
			if (!player.isCreative()) {
				player.getAbilities().mayfly = false;
				player.getAbilities().flying = false;
				player.onUpdateAbilities();
			}

			this.flyMap.put(player, 0);
		}
	}

}
