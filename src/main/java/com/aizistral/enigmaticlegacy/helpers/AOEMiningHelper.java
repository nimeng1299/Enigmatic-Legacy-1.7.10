package com.aizistral.enigmaticlegacy.helpers;

import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.ClipContext;
import com.aizistral.enigmaticlegacy.compat.CompatPos;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class AOEMiningHelper {

    public static final Random random = new Random();

    /** Attempt to break blocks around the given pos in a 3x3x1 square relative to the targeted face. */
    public static void attemptBreakNeighbors(World world, BlockPos pos, EntityPlayer player, Set<Block> effectiveOn,
        Predicate<BlockState> predicate, boolean checkHarvestLevel) {
        MovingObjectPosition trace = AOEMiningHelper.calcRayTrace(world, player, ClipContext.Fluid.ANY);

        if (trace != null && trace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            ForgeDirection face = ForgeDirection.getOrientation(trace.sideHit);

            int fortuneLevel = com.aizistral.enigmaticlegacy.compat.Enchantments
                .getEnchantmentLevel(com.aizistral.enigmaticlegacy.compat.Enchantments.FORTUNE, player.getHeldItem());
            int silkLevel = com.aizistral.enigmaticlegacy.compat.Enchantments.getEnchantmentLevel(
                com.aizistral.enigmaticlegacy.compat.Enchantments.SILK_TOUCH,
                player.getHeldItem());

            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (a == 0 && b == 0) {
                        continue;
                    }

                    BlockPos target = null;

                    if (face == ForgeDirection.UP || face == ForgeDirection.DOWN) {
                        target = CompatPos.offset(pos, a, 0, b);
                    }
                    if (face == ForgeDirection.NORTH || face == ForgeDirection.SOUTH) {
                        target = CompatPos.offset(pos, a, b, 0);
                    }
                    if (face == ForgeDirection.EAST || face == ForgeDirection.WEST) {
                        target = CompatPos.offset(pos, 0, a, b);
                    }

                    AOEMiningHelper.attemptBreak(
                        world,
                        target,
                        player,
                        effectiveOn,
                        predicate,
                        fortuneLevel,
                        silkLevel,
                        checkHarvestLevel,
                        null,
                        (objPos, objState) -> {});
                }
            }
        }
    }

    public static void attemptBreak(World world, BlockPos pos, EntityPlayer player, Set<Block> effectiveOn,
        Predicate<BlockState> predicate, int fortuneLevel, int silkLevel, boolean checkHarvestLevel, ItemStack tool,
        BiConsumer<BlockPos, BlockState> toolDamageConsumer) {
        if (pos == null) return;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Block block = world.getBlock(x, y, z);

        if (block == null || block.getMaterial() == Material.air) return;

        BlockState state = new BlockState(block, world.getBlockMetadata(x, y, z));

        ItemStack held = tool != null ? tool : player.getHeldItem();
        boolean validHarvest = !checkHarvestLevel || held != null && held.func_150998_b(block);
        boolean isEffective = effectiveOn.contains(block) || predicate.test(state);
        boolean unbreakable = block == Blocks.mob_spawner || block.getBlockHardness(world, x, y, z) < 0F;

        if (validHarvest && isEffective && !unbreakable) {
            world.func_147480_a(x, y, z, true);
            toolDamageConsumer.accept(pos, state);

            int exp = block.getExpDrop(world, state.getMetadata(), fortuneLevel);
            if (exp > 0) {
                block.dropXpOnBlockBreak(world, x, y, z, exp);
            }
        }
    }

    public static MovingObjectPosition calcRayTrace(World worldIn, EntityPlayer player, ClipContext.Fluid fluidMode) {
        return ItemBase.rayTrace(worldIn, player, fluidMode);
    }

    public static Vector3 calcRayTrace(World worldIn, EntityPlayer player, ClipContext.Fluid fluidMode,
        double distance) {
        Vec3 vector3d = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLookVec();
        Vec3 vector3d1 = vector3d.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        MovingObjectPosition result = worldIn.rayTraceBlocks(vector3d, vector3d1, fluidMode != ClipContext.Fluid.NONE);

        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return new Vector3(result.hitVec);
        else {
            return new Vector3(player.getLookVec()).multiply(64F)
                .add(new Vector3(player.posX, player.posY, player.posZ));
        }
    }

    public static void harvestPlane(World world, EntityPlayer player, ForgeDirection dir, BlockPos pos,
        Predicate<BlockState> predicate, int radius, boolean harvestLevelCheck, @Nullable BlockPos excludedBlock,
        ItemStack tool, BiConsumer<BlockPos, BlockState> toolDamageConsumer) {
        int fortuneLevel = com.aizistral.enigmaticlegacy.compat.Enchantments
            .getEnchantmentLevel(com.aizistral.enigmaticlegacy.compat.Enchantments.FORTUNE, player.getHeldItem());
        int silkLevel = com.aizistral.enigmaticlegacy.compat.Enchantments
            .getEnchantmentLevel(com.aizistral.enigmaticlegacy.compat.Enchantments.SILK_TOUCH, player.getHeldItem());
        int supRad = (radius - 1) / 2;

        for (int a = -supRad; a <= supRad; a++) {
            for (int b = -supRad; b <= supRad; b++) {
                BlockPos target = null;

                if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
                    target = CompatPos.offset(pos, a, 0, b);
                }
                if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
                    target = CompatPos.offset(pos, a, b, 0);
                }
                if (dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
                    target = CompatPos.offset(pos, 0, a, b);
                }

                if (excludedBlock != null && target != null && target.equals(excludedBlock)) {
                    continue;
                }

                AOEMiningHelper.attemptBreak(
                    world,
                    target,
                    player,
                    Sets.newHashSet(),
                    predicate,
                    fortuneLevel,
                    silkLevel,
                    harvestLevelCheck,
                    tool,
                    toolDamageConsumer);
            }
        }
    }

    public static void harvestCube(World world, EntityPlayer player, ForgeDirection dir, BlockPos centralPos,
        Predicate<BlockState> predicate, int planeRadius, int depth, boolean harvestLevelCheck,
        @Nullable BlockPos excludedBlock, ItemStack tool, BiConsumer<BlockPos, BlockState> toolDamageConsumer) {

        for (int a = 0; a < depth; a++) {
            int x = 0;
            int y = 0;
            int z = 0;

            if (dir == ForgeDirection.UP) {
                y -= a;
            }
            if (dir == ForgeDirection.DOWN) {
                y += a;
            }
            if (dir == ForgeDirection.SOUTH) {
                z -= a;
            }
            if (dir == ForgeDirection.NORTH) {
                z += a;
            }
            if (dir == ForgeDirection.EAST) {
                x -= a;
            }
            if (dir == ForgeDirection.WEST) {
                x += a;
            }

            AOEMiningHelper.harvestPlane(
                world,
                player,
                dir,
                CompatPos.offset(centralPos, x, y, z),
                predicate,
                planeRadius,
                harvestLevelCheck,
                excludedBlock,
                tool,
                toolDamageConsumer);
        }
    }

}
