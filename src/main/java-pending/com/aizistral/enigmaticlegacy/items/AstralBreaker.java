package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.IMultiblockMiningTool;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.AOEMiningHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.packets.clients.PacketFlameParticles;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.etherium.core.IEtheriumConfig;
import com.aizistral.etherium.items.EtheriumAxe;
import com.aizistral.etherium.items.EtheriumPickaxe;
import com.aizistral.etherium.items.EtheriumShovel;
import com.aizistral.omniconfig.Configuration;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;

// TODO Make sure Astral Breaker actually works
// ... and fix the thing where it works like shears?
public class AstralBreaker extends ItemBaseTool implements IMultiblockMiningTool {
	public static Omniconfig.IntParameter miningRadius;
	public static Omniconfig.IntParameter miningDepth;

	public static Omniconfig.BooleanParameter flameParticlesToggle;

	@SubscribeConfig(receiveClient = true)
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("AstralBreaker");

		if (builder.config.getSidedType() != Configuration.SidedConfigType.CLIENT) {
			miningRadius = builder
					.comment("The radius of Astral Breaker AOE mining. Set to -1 to disable the feature.")
					.min(-1)
					.max(128 - 1)
					.getInt("MiningRadius", 3);

			miningDepth = builder
					.comment("The depth of Astral Breaker AOE mining.")
					.max(128 - 1)
					.getInt("MiningDepth", 1);
		} else {
			flameParticlesToggle = builder
					.comment("Whether or not flame particles should appear when the Astral Breaker breaks a block")
					.getBoolean("FlameParticlesToggle", true);
		}

		builder.popPrefix();
	}

	@SuppressWarnings("unchecked")
	public AstralBreaker() {
		super(4F, -2.8F, EnigmaticMaterials.ETHERIUM,
				ItemBaseTool.getDefaultProperties()
				.defaultDurability(4000)
				.rarity(EnumRarity.EPIC)
				.fireResistant(),
				Sets.newHashSet(
						BlockTags.MINEABLE_WITH_PICKAXE,
						BlockTags.MINEABLE_WITH_AXE,
						BlockTags.MINEABLE_WITH_SHOVEL,
						BlockTags.MINEABLE_WITH_HOE
						));
	}

	private Item findTool(String name) {
		return SuperpositionHandler.findItem(EnigmaticLegacy.MODID, name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker1", EnumChatFormatting.GOLD, miningRadius.getValue() + EtheriumConfigHandler.instance().getAOEBoost(Minecraft.getInstance().player), miningDepth.getValue());
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker3");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralBreaker4");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (!this.areaEffectsAllowed(stack)) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.aoeDisabled");
		}
	}

	public void spawnFlameParticles(World world, BlockPos pos) {
		EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 128, world.dimension())), new PacketFlameParticles(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 18, true));
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!world.isClientSide) {
			this.spawnFlameParticles(world, pos);
		}

		if (entityLiving instanceof EntityPlayer player && this.areaEffectsEnabled(player, stack) && this.isCorrectToolForDrops(stack, state) && !world.isClientSide && miningRadius.getValue() != -1) {
			HitResult trace = AOEMiningHelper.calcRayTrace(world, player, ClipContext.Fluid.ANY);

			if (trace.getType() == HitResult.Type.BLOCK) {
				MovingObjectPosition blockTrace = (MovingObjectPosition) trace;
				ForgeDirection face = blockTrace.getDirection();

				AOEMiningHelper.harvestCube(world, player, face, pos, (s) -> this.isCorrectToolForDrops(stack, s), miningRadius.getValue() + EtheriumConfigHandler.instance().getAOEBoost(player), miningDepth.getValue(), true, pos, stack, (objPos, objState) -> {
					stack.hurtAndBreak(1, entityLiving, p -> p.broadcastBreakEvent(EntityLiving.getEquipmentSlotForItem(stack)));
					this.spawnFlameParticles(world, objPos);
				});
			}
		}

		return super.mineBlock(stack, world, state, pos, entityLiving);
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
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer().isSneaking())
			return this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
		else
			return super.useOn(context);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return toolAction == ToolActions.AXE_DIG || toolAction == ToolActions.PICKAXE_DIG
				|| toolAction == ToolActions.SHOVEL_DIG || toolAction == ToolActions.HOE_DIG
				|| toolAction == ToolActions.SWORD_DIG;
	}

}
