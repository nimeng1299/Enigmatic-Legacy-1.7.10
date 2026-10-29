package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import com.aizistral.enigmaticlegacy.compat.Screen;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.UseOnContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.VineBlock;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.FakePlayerFactory;

public class Infinimeal extends ItemBase implements Vanishable {
	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new OptionalDispenseItemBehavior() {

		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			this.setSuccess(true);
			World level = source.getLevel();
			BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

			if (!tryApply(stack, level, pos, Optional.empty(), Optional.empty())) {
				this.setSuccess(false);
			}

			return stack;
		};

	};


	public Infinimeal() {
		super(getDefaultProperties().stacksTo(1).rarity(EnumRarity.UNCOMMON));
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal2");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.infinimeal3");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		World level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		boolean success = tryApply(stack, level, pos, Optional.of(context.getPlayer()), Optional.of(context.getClickedFace()));

		if (success)
			return InteractionResult.sidedSuccess(level.isClientSide);
		else
			return InteractionResult.PASS;
	}

	private static boolean tryApply(ItemStack stack, World world, BlockPos pos, Optional<EntityPlayer> optionalPlayer,
			Optional<ForgeDirection> clickedFace) {
		ItemStack stackCopy = new ItemStack(stack.getItem());

		if (applyVanillaBonemeal(stackCopy, world, pos, optionalPlayer, clickedFace)) {
			if (!world.isClientSide) {
				world.levelEvent(1505, pos, 0);
			}

			return true;
		}

		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof CactusBlock || block instanceof SugarCaneBlock) {
			BlockPos topMostPos = findTopmostGrowable(world, pos, block, true);
			BlockState topMostState = world.getBlockState(topMostPos);

			if (topMostState.hasProperty(BlockStateProperties.AGE_15) && world.isEmptyBlock(topMostPos.above())) {
				int age = topMostState.getValue(BlockStateProperties.AGE_15);

				int plantHeight;
				for(plantHeight = 1; world.getBlockState(topMostPos.below(plantHeight)).is(block); ++plantHeight) {}

				if (plantHeight >= 3)
					return false;

				if (!world.isClientSide) {
					world.levelEvent(2005, pos, 0);
				}

				age += world.random.nextInt(20);
				world.setBlock(topMostPos, topMostState.setValue(BlockStateProperties.AGE_15, Integer.valueOf(Math.min(age, 15))), 4);

				if (world instanceof WorldServer) {
					world.getBlockState(topMostPos).randomTick((WorldServer)world, topMostPos, world.random);
				}

				return true;
			}
		} else if (block instanceof VineBlock) {
			if (!block.isRandomlyTicking(state))
				return false;

			if (world.isClientSide) {
				EnigmaticLegacy.PROXY.spawnBonemealParticles(world, pos, 0);
			}

			int cycles = 7+world.random.nextInt(7);

			if (world instanceof WorldServer) {
				for (int i = 0; i <= cycles; i++) {
					state.randomTick((WorldServer)world, pos, world.random);
				}

				state.updateNeighbourShapes(world, pos, 4);
			}

			return true;
		} else if (block instanceof NetherWartBlock) {
			if (!block.isRandomlyTicking(state))
				return false;

			if (!world.isClientSide) {
				world.levelEvent(2005, pos, 0);
			}

			int cycles = 1+world.random.nextInt(1);
			cycles*=11;

			if (world instanceof WorldServer) {
				for (int i = 0; i <= cycles; i++) {
					state.randomTick((WorldServer)world, pos, world.random);
				}
			}

			return true;
		} else if (block instanceof ChorusPlantBlock || block instanceof ChorusFlowerBlock) {
			if (!world.isClientSide) {
				world.levelEvent(2005, pos, 0);
			}

			if (world instanceof WorldServer serverWorld) {
				List<BlockPos> flowers = findChorusFlowers(world, pos);

				flowers.forEach(flowerPos -> {
					int cycles = 1 + world.random.nextInt(2);
					cycles *= 11;

					for (int i = 0; i <= cycles; i++) {
						BlockState flowerState = world.getBlockState(flowerPos);
						flowerState.randomTick(serverWorld, flowerPos, world.random);
					}
				});
			}

			return true;
		}

		return false;
	}

	public static boolean applyVanillaBonemeal(ItemStack stack, World level, BlockPos pos, Optional<EntityPlayer> optionalPlayer,
			Optional<ForgeDirection> clickedFace) {
		if (!growCrop(stack, level, pos, optionalPlayer))
			return BoneMealItem.growWaterPlant(stack, level, pos, clickedFace.orElse(null));
		else
			return true;
	}

	public static boolean growCrop(ItemStack stack, World level, BlockPos pos, Optional<EntityPlayer> optionalPlayer) {
		if (!optionalPlayer.isPresent()) {
			if (level instanceof WorldServer)
				return BoneMealItem.applyBonemeal(stack, level, pos, FakePlayerFactory.getMinecraft((WorldServer)level));
			return false;
		} else
			return BoneMealItem.applyBonemeal(stack, level, pos, optionalPlayer.get());
	}

	private static List<BlockPos> findChorusFlowers(World level, BlockPos pos) {
		List<BlockPos> chorusTree = new ArrayList<>();
		chorusTree.add(pos);

		while (true) {
			int formerSize = chorusTree.size();
			for (BlockPos treePos : new ArrayList<>(chorusTree)) {
				chorusTree.addAll(getNeighboringBlocks(level, treePos, chorusTree, ChorusFlowerBlock.class,
						ChorusPlantBlock.class));
			}

			if (formerSize == chorusTree.size()) {
				break;
			}
		}

		return chorusTree.stream().filter(p -> level.getBlockState(p).getBlock() instanceof ChorusFlowerBlock)
				.collect(Collectors.toList());
	}

	@SafeVarargs
	private static List<BlockPos> getNeighboringBlocks(World level, BlockPos pos, List<BlockPos> exclude, Class<? extends Block>... classes) {
		BlockPos[] neighbors = new BlockPos[] { pos.above(), pos.below(), pos.east(), pos.north(), pos.south(), pos.west() };

		return Arrays.stream(neighbors).filter(neighbor -> !exclude.contains(neighbor) && Arrays.stream(classes)
				.anyMatch(theClass -> theClass.isInstance(level.getBlockState(neighbor).getBlock()))).collect(Collectors.toList());
	}

	private static BlockPos findTopmostGrowable(World world, BlockPos pos, Block block, boolean goUp) {
		BlockPos top = pos;

		while (true) {
			if (world.getBlockState(top) != null && world.getBlockState(top).getBlock() == block) {
				BlockPos nextUp = goUp ? top.above() : top.below();

				if (world.getBlockState(nextUp) == null || world.getBlockState(nextUp).getBlock() != block)
					return top;
				else {
					top = nextUp;
					continue;
				}
			} else
				return pos;
		}
	}

}
