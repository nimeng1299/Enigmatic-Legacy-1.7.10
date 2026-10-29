package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;

public class SuperMagnetRing extends MagnetRing {
	public static Omniconfig.IntParameter range;
	public static Omniconfig.BooleanParameter soundEnabled;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("SuperMagnetRing");

		range = builder
				.comment("The radius in which Dislocation Ring will collect items.")
				.min(1)
				.max(256)
				.getInt("Range", 16);

		soundEnabled = builder
				.comment("Whether or not Dislocation Ring should play sound effect when teleporting items to player.")
				.getBoolean("Sound", false);

		builder.popPrefix();
	}

	public SuperMagnetRing() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.EPIC));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.superMagnetRing1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.superMagnetRing2", EnumChatFormatting.GOLD, range.getValue());
			ItemLoreHelper.addLocalizedString(list, invertShift.getValue() ? "tooltip.enigmaticlegacy.superMagnetRing3_alt" : "tooltip.enigmaticlegacy.superMagnetRing3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		EntityLivingBase living = context.entity();

		if (invertShift.getValue() ? !living.isSneaking() : living.isSneaking() || living.worldObj.isClientSide || !(living instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) living;

		if (this.hasMagnetEffectsDisabled(player))
			return;

		double x = living.getX();
		double y = living.getY() + 0.75;
		double z = living.getZ();

		List<ItemEntity> items = living.worldObj.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(x - range.getValue(), y - range.getValue(), z - range.getValue(), x + range.getValue(), y + range.getValue(), z + range.getValue()));
		int pulled = 0;
		for (ItemEntity item : items)
			if (this.canPullItem(item)) {
				if (pulled > 512) {
					break;
				}

				if (!SuperpositionHandler.canPickStack(player, item.getItem())) {
					continue;
				}

				/*
				EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(item.getPosX(), item.getPosY(), item.getPosZ(), 24, item.world.getDimensionKey())), new PacketPortalParticles(item.getPosX(), item.getPosY() + (item.getHeight() / 2), item.getPosZ(), 24, 0.75D, true));

				if (ConfigHandler.SUPER_MAGNET_RING_SOUND.getValue())
					item.world.playSound(null, item.getPosition(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2D)));
				 */

				item.setNoPickUpDelay();
				item.playerTouch(player);

				pulled++;
			}

	}

	@Override
	public boolean canEquip(SlotContext context, ItemStack stack) {
		return super.canEquip(context, stack) && !SuperpositionHandler.hasCurio(context.entity(), EnigmaticItems.MAGNET_RING);
	}

	@Override
	protected boolean canPullItem(ItemEntity item) {
		return super.canPullItem(item);
	}

}
