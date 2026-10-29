package com.aizistral.etherium.items;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.etherium.core.EtheriumUtil;
import com.aizistral.etherium.core.IEtheriumConfig;
import com.aizistral.etherium.items.generic.ItemEtheriumTool;
import com.google.common.collect.Sets;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.client.Minecraft;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.BlockTags;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import com.aizistral.enigmaticlegacy.compat.ClipContext;
import net.minecraft.world.World;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.phys.HitResult;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import net.minecraftforge.common.ToolActions;

public class EtheriumShovel extends ItemEtheriumTool {

	public EtheriumShovel() {
		super(2.5F, -3.0F, BlockTags.MINEABLE_WITH_SHOVEL, EtheriumUtil.defaultProperties(EtheriumShovel.class)
				.defaultDurability((int) (EtheriumConfigHandler.instance().getToolMaterial().getUses() * 1.5))
				.rarity(EnumRarity.RARE)
				.fireResistant());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (this.getConfig().getShovelMiningRadius() == -1)
			return;

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumShovel1", EnumChatFormatting.GOLD, this.getConfig().getShovelMiningRadius() + this.getConfig().getAOEBoost(Minecraft.getInstance().player), this.getConfig().getShovelMiningDepth());
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

			if (!this.getConfig().disableAOEShiftInhibition()) {
				ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumShovel2");
			}
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.etheriumShovel3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (!this.areaEffectsAllowed(stack)) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.aoeDisabled");
		}
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer && this.areaEffectsEnabled((EntityPlayer) entityLiving, stack) && this.isCorrectToolForDrops(stack, state) && !world.isClientSide && this.getConfig().getShovelMiningRadius() != -1) {
			HitResult trace = AOEMiningHelper.calcRayTrace(world, (EntityPlayer) entityLiving, ClipContext.Fluid.ANY);

			if (trace.getType() == HitResult.Type.BLOCK) {
				MovingObjectPosition blockTrace = (MovingObjectPosition) trace;
				ForgeDirection face = blockTrace.getDirection();

				AOEMiningHelper.harvestCube(world, (EntityPlayer) entityLiving, face, pos, (s) -> this.isCorrectToolForDrops(stack, s), this.getConfig().getShovelMiningRadius() + this.getConfig().getAOEBoost((EntityPlayer) entityLiving), this.getConfig().getShovelMiningDepth(), false, pos, stack, (objPos, objState) -> {
					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(EntityLiving.getEquipmentSlotForItem(stack)));
				});
			}
		}

		return super.mineBlock(stack, world, state, pos, entityLiving);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isSneaking())
			return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
		else
			return Items.DIAMOND_SHOVEL.useOn(context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);

		if (player.isSneaking()) {
			this.toggleAreaEffects(player, stack);

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else
			return super.use(world, player, hand);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}

}
