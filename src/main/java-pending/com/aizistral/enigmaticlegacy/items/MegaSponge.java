package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import net.minecraft.tags.FluidTags;
import com.aizistral.enigmaticlegacy.compat.TagKey;
import com.aizistral.enigmaticlegacy.compat.Mth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.tileentity.TileEntity;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class MegaSponge extends ItemBaseCurio implements Vanishable {
	public static Omniconfig.IntParameter radius;

	@SubscribeConfig
	public static void onConfig(OmniconfigWrapper builder) {
		builder.pushPrefix("Megasponge");

		radius = builder
				.comment("Radius in which Exptrapolated Megaspong absorbs water. Default 4 equals to vanilla sponge")
				.max(128)
				.getInt("Radius", 4);

		builder.popPrefix();
	}

	public MegaSponge() {
		super(ItemBaseCurio.getDefaultProperties().rarity(EnumRarity.UNCOMMON));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

		ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.megaSponge1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.megaSponge2");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@SuppressWarnings("deprecation")
	public BlockPos getCollidedWater(TagKey<Fluid> fluidTag, EntityPlayer player) {
		AxisAlignedBB axisalignedbb = player.getBoundingBox().deflate(0.001D);
		int i = Mth.floor(axisalignedbb.minX);
		int j = Mth.ceil(axisalignedbb.maxX);
		int k = Mth.floor(axisalignedbb.minY);
		int l = Mth.ceil(axisalignedbb.maxY);
		int i1 = Mth.floor(axisalignedbb.minZ);
		int j1 = Mth.ceil(axisalignedbb.maxZ);

		BlockPos pos = null;

		if (!player.worldObj.hasChunksAt(i, k, i1, j, l, j1))
			return null;
		else {
			try {
				net.minecraft.core.BlockPos.MutableBlockPos blockpos$pooledmutableblockpos = new BlockPos.MutableBlockPos();

				for (int l1 = i; l1 < j; ++l1) {
					for (int i2 = k; i2 < l; ++i2) {
						for (int j2 = i1; j2 < j1; ++j2) {
							blockpos$pooledmutableblockpos.set(l1, i2, j2);
							FluidState ifluidstate = player.worldObj.getFluidState(blockpos$pooledmutableblockpos);
							if (ifluidstate.is(fluidTag)) {
								ifluidstate.tick(player.worldObj, blockpos$pooledmutableblockpos);
								pos = new BlockPos(l1, i2, j2);
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return pos;
		}
	}

	@Override
	public void curioTick(SlotContext context, ItemStack stack) {
		if (context.entity() instanceof EntityPlayer player && !player.worldObj.isClientSide) {
			if (!player.getCooldowns().isOnCooldown(this)) {
				List<BlockPos> doomedWaterBlocks = new ArrayList<BlockPos>();
				BlockPos initialPos = this.getCollidedWater(FluidTags.WATER, player);
				BlockState initialState = initialPos != null ? player.worldObj.getBlockState(initialPos) : null;

				if (initialPos != null && initialState != null)
					if (initialState.getFluidState() != null && initialState.getFluidState().is(FluidTags.WATER)) {

						doomedWaterBlocks.add(initialPos);
						List<BlockPos> processedBlocks = new ArrayList<BlockPos>();
						processedBlocks.add(initialPos);

						for (int counter = 0; counter <= radius.getValue(); counter++) {
							List<BlockPos> outputBlocks = new ArrayList<BlockPos>();

							for (BlockPos checkedPos : processedBlocks) {
								outputBlocks.addAll(this.getNearbyWater(player.worldObj, checkedPos));
							}

							processedBlocks.clear();

							for (BlockPos thePos : outputBlocks) {
								if (!doomedWaterBlocks.contains(thePos)) {
									processedBlocks.add(thePos);
									doomedWaterBlocks.add(thePos);
								}
							}

							outputBlocks.clear();
						}

						processedBlocks.clear();

						for (BlockPos exterminatedBlock : doomedWaterBlocks) {
							this.absorbWaterBlock(exterminatedBlock, player.worldObj.getBlockState(exterminatedBlock), player.worldObj);
						}

						doomedWaterBlocks.clear();

						player.worldObj.playSound(null, CompatPos.of(player), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
						EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 64, player.worldObj.dimension())), new PacketPortalParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 40, 1.0D, false));
						player.getCooldowns().addCooldown(this, 20);

					}
			}
		}
	}

	public void absorbWaterBlock(BlockPos pos, BlockState state, World world) {
		if (state.getBlock() instanceof BucketPickup && !((BucketPickup) state.getBlock()).pickupBlock(world, pos, state).isEmpty()) {
			// Whatever
		} else if (state.getBlock() instanceof LiquidBlock) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		} else if (!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) && !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) {
			BlockEntity tileentity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
			Block.dropResources(state, world, pos, tileentity);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
	}

	public List<BlockPos> getNearbyWater(World world, BlockPos pos) {
		List<BlockPos> nearBlocks = new ArrayList<BlockPos>();
		List<BlockPos> waterBlocks = new ArrayList<BlockPos>();

		nearBlocks.add(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
		nearBlocks.add(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()));
		nearBlocks.add(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
		nearBlocks.add(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
		nearBlocks.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1));
		nearBlocks.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1));

		for (BlockPos checkedPos : nearBlocks) {
			if (world.getBlockState(checkedPos).getFluidState().is(FluidTags.WATER)) {
				waterBlocks.add(checkedPos);
			}
		}

		return waterBlocks;
	}

}
