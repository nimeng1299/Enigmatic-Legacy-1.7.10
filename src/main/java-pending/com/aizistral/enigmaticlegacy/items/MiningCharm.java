package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.tags.FluidTags;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.potion.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.loot.LootContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class MiningCharm extends ItemBaseCurio {
	public static Omniconfig.PerhapsParameter breakSpeedBonus;
	public static Omniconfig.DoubleParameter reachDistanceBonus;
	public static Omniconfig.BooleanParameter enableNightVision;
	//public static EnigmaConfig.BooleanParameter bonusFortuneEnabled;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("MiningCharm");

		breakSpeedBonus = builder
				.comment("Mining speed boost granted by Charm of Treasure Hunter. Defined as percentage.")
				.max(1000)
				.getPerhaps("BreakSpeed", 30);

		reachDistanceBonus = builder
				.comment("Additional block reach granted by Charm of Treasure Hunter.")
				.max(16)
				.getDouble("ReachDistance", 2.15);

		enableNightVision = builder
				.comment("Whether Night Vision ability of Charm of Treasure Hunter should be enabled.")
				.getBoolean("EnableNightVision", true);

		builder.popPrefix();
	}

	public final int nightVisionDuration = 310;

	public MiningCharm() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.RARE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		Component mode = Component.translatable("tooltip.enigmaticlegacy.enabled");

		if (ItemNBTHelper.verifyExistance(stack, "nightVisionEnabled"))
			if (!ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true)) {
				mode = Component.translatable("tooltip.enigmaticlegacy.disabled");
			}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharm1", EnumChatFormatting.GOLD, breakSpeedBonus.getValue().asPercentage() + "%");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharm2", EnumChatFormatting.GOLD, 1);
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharm3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharm4");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharm5");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.miningCharmNightVision", null, mode.getString());
	}

	public void removeNightVisionEffect(EntityPlayer player, int duration) {
		if (player.getEffect(MobEffects.NIGHT_VISION) != null) {
			PotionEffect effect = player.getEffect(MobEffects.NIGHT_VISION);

			if (effect.getDuration() <= (duration - 1)) {
				player.removePotionEffect(MobEffects.NIGHT_VISION);
			}
		}
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player && !context.entity().worldObj.isClientSide)
			if (SuperpositionHandler.hasCurio(player, EnigmaticItems.MINING_CHARM)) {
				if (ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true)) {
					if (enableNightVision.getValue()) {
						if (player.getY() < 50 && !player.worldObj.dimension().equals(World.NETHER)
								&& !player.worldObj.dimension().equals(World.END)
								&& !player.isEyeInFluid(FluidTags.WATER)
								&& !player.worldObj.canSeeSkyFromBelowWater(CompatPos.of(player))
								/*&& player.level.getMaxLocalRawBrightness(player.blockPosition(), 0) <= 8*/) {

							EnigmaticEventHandler.isApplyingNightVision = true;
							player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, this.nightVisionDuration, 0, true, false));
							EnigmaticEventHandler.isApplyingNightVision = false;
						} else {
							//this.removeNightVisionEffect(player, this.nightVisionDuration);
						}
					} else {
						ItemNBTHelper.setBoolean(stack, "nightVisionEnabled", false);
					}
				}
			}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand handIn) {
		ItemStack stack = player.getItemInHand(handIn);

		if (ItemNBTHelper.getBoolean(stack, "nightVisionEnabled", true)) {
			ItemNBTHelper.setBoolean(stack, "nightVisionEnabled", false);
			world.playSound(null, CompatPos.of(player), EnigmaticSounds.CHARGED_OFF, SoundSource.PLAYERS, (float) (0.8F + (Math.random() * 0.2F)), (float) (0.8F + (Math.random() * 0.2F)));
		} else {
			if (enableNightVision.getValue()) {
				ItemNBTHelper.setBoolean(stack, "nightVisionEnabled", true);
				world.playSound(null, CompatPos.of(player), EnigmaticSounds.CHARGED_ON, SoundSource.PLAYERS, (float) (0.8F + (Math.random() * 0.2F)), (float) (0.8F + (Math.random() * 0.2F)));
			}
		}

		player.swing(handIn);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public void onUnequip(SlotContext context, ItemStack newStack, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player) {
			this.removeNightVisionEffect(player, this.nightVisionDuration);
		}
	}

	@Override
	public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
		return false;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> atts = HashMultimap.create();

		atts.put(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get(), new AttributeModifier(UUID.fromString("08c3c83d-7137-4b42-880f-b146bcb64d2e"), "Reach bonus", reachDistanceBonus.getValue(), AttributeModifier.Operation.ADDITION));

		return atts;
	}

	@Override
	public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack curio) {
		return super.getFortuneLevel(slotContext, lootContext, curio) + 1;
	}

}
