package com.aizistral.enigmaticlegacy.blocks;

import java.util.List;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.block.Block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext.Builder;

public class BlockAstralDust extends Block {

	public BlockAstralDust() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1F, 10F)
				.lightLevel(arg -> 10).noOcclusion().sound(SoundType.WOOL));
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
		return super.shouldDisplayFluidOverlay(state, world, pos, fluidState);
	}

	@Override
	public RenderShape getRenderShape(BlockState p_60550_) {
		return RenderShape.MODEL;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		return List.of(new ItemStack(this));
	}

	@Override
	public boolean hasDynamicShape() {
		return true;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}


}
