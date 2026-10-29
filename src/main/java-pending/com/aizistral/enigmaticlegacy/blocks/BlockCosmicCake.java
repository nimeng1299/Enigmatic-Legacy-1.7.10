package com.aizistral.enigmaticlegacy.blocks;

import java.util.Random;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.Stats;
import net.minecraft.util.RandomSource;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import net.minecraft.entity.player.EntityPlayer;
import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.LevelEvent;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class BlockCosmicCake extends CakeBlock {
	private static final FoodProperties AS_TASTY_AS = Foods.GOLDEN_CARROT;
	protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[] {
			Block.box(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 2.5D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 4.5D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 6.5D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 8.5D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 10.5D, 15.0D, 8.0D, 15.0D),
			Block.box(1.0D, 0.0D, 12.0D, 15.0D, 8.0D, 15.0D)
	};

	public BlockCosmicCake() {
		super(Properties.copy(Blocks.CAKE));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE_BY_BITE[pState.getValue(BITES)];
	}

	@Override
	public InteractionResult use(BlockState pState, World pLevel, BlockPos pPos, EntityPlayer pPlayer, InteractionHand pHand, MovingObjectPosition pHit) {
		if (pState.getValue(BITES) > 5)
			return InteractionResult.PASS;

		ItemStack itemstack = pPlayer.getItemInHand(pHand);

		if (pLevel.isClientSide) {
			if (eat(pLevel, pPos, pState, pPlayer).consumesAction())
				return InteractionResult.SUCCESS;

			if (itemstack.isEmpty())
				return InteractionResult.CONSUME;
		}

		return eat(pLevel, pPos, pState, pPlayer);
	}

	protected static InteractionResult eat(LevelAccessor pLevel, BlockPos pPos, BlockState pState, EntityPlayer pPlayer) {
		if (!pPlayer.canEat(false))
			return InteractionResult.PASS;
		else {
			pPlayer.awardStat(Stats.EAT_CAKE_SLICE);
			pPlayer.getFoodData().eat(AS_TASTY_AS.getNutrition(), AS_TASTY_AS.getSaturationModifier());
			int i = pState.getValue(BITES);
			pLevel.gameEvent(pPlayer, GameEvent.EAT, pPos);

			pLevel.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pPos, Block.getId(pState));

			if (i < 6) {
				pLevel.setBlock(pPos, pState.setValue(BITES, Integer.valueOf(i + 1)), 3);
			} else {
				pLevel.removeBlock(pPos, false);
				pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DESTROY, pPos);
			}

			pLevel.playSound(null, CompatPos.of(pPlayer), SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1F, 0.5F + (float) Math.random() * 0.5F);

			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public void randomTick(BlockState state, WorldServer level, BlockPos pos, RandomSource random) {
		int bites = 0;
		if ((bites = state.getValue(BITES)) > 0) {
			level.setBlock(pos, state.setValue(BITES, bites - 1), 3);
			level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
			level.playSound(null, pos, EnigmaticSounds.EAT_REVERSE, SoundSource.BLOCKS, 1F, 0.5F + (float) Math.random() * 0.5F);
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		return pState.getValue(BITES) > 0;
	}

}
