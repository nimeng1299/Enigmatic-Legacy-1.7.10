package com.aizistral.enigmaticlegacy.helpers;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.blocks.BlockEndAnchor;
import com.aizistral.enigmaticlegacy.items.EndAnchor;
import com.aizistral.enigmaticlegacy.objects.AnchorSearchResult;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class AdvancedSpawnLocationHelper {

    /**
     * Dimensions 1.7.10 can enumerate spawn points for. The port keeps one spawn point per
     * dimension instead of the single one 1.20 has, and the map holding the non-overworld ones
     * is private with no public accessor, so the vanilla dimensions are probed directly.
     */
    private static final int[] SPAWN_DIMENSIONS = { 0, -1, 1 };

    /**
     * 1.20 counterpart: {@code player.getRespawnDimension()}, i.e. the dimension stored together
     * with the player's respawn point. Here the current dimension wins if it holds a spawn point
     * at all; otherwise a dimension with a usable Dimensional Anchor is preferred over the plain
     * overworld fallback used before.
     */
    public static int getPlayerRespawnDimension(EntityPlayerMP player) {
        if (player.getBedLocation(player.dimension) != null) return player.dimension;

        int anchorDimension = findEndAnchorSpawnDimension(player);

        if (anchorDimension != Integer.MIN_VALUE) return anchorDimension;

        return 0;
    }

    /**
     * @return the dimension the player has a charged Dimensional Anchor registered in as their
     *         respawn point, or {@link Integer#MIN_VALUE} if there is none.
     */
    public static int findEndAnchorSpawnDimension(EntityPlayerMP player) {
        MinecraftServer server = MinecraftServer.getServer();

        if (server == null) return Integer.MIN_VALUE;

        for (int dimension : SPAWN_DIMENSIONS) {
            ChunkCoordinates spawn = player.getBedLocation(dimension);

            if (spawn == null) continue;

            WorldServer world = server.worldServerForDimension(dimension);

            if (world == null || !isUsableEndAnchor(world, spawn)) continue;

            return dimension;
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Whether the block at the given spawn point is a charged Dimensional Anchor that is allowed
     * to work in its dimension.
     */
    public static boolean isUsableEndAnchor(WorldServer world, @Nullable ChunkCoordinates pos) {
        if (world == null || pos == null) return false;

        Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);

        if (!(block instanceof BlockEndAnchor)) return false;
        if (world.getBlockMetadata(pos.posX, pos.posY, pos.posZ) <= 0) return false;

        return BlockEndAnchor.canSetSpawn(world);
    }

    /**
     * Retrieves the player respawn location the 1.20 way: for a Dimensional Anchor this is the
     * stand-up position next to it, and a charge is spent unless {@code ChargeSaveChance} saves
     * it. Bed (or any other) spawn points keep the previous top-of-column behaviour.
     */
    public static Optional<Vec3> getValidSpawn(final WorldServer world, final EntityPlayerMP player) {
        if (world == null) return Optional.empty();

        int dimension = world.provider.dimensionId;
        ChunkCoordinates spawn = player.getBedLocation(dimension);

        if (spawn == null) return Optional.empty();

        if (isUsableEndAnchor(world, spawn)) {
            AnchorSearchResult result = EndAnchor.findAndUseEndAnchor(
                world,
                new BlockPos(spawn.posX, spawn.posY, spawn.posZ),
                0.0F,
                player.isSpawnForced(dimension),
                false);
            return result.location();
        }

        int y = world.getTopSolidOrLiquidBlock(spawn.posX, spawn.posZ);
        return Optional.of(Vec3.createVectorHelper(spawn.posX + 0.5D, y, spawn.posZ + 0.5D));
    }

    public static BlockPos getRespawnLocation(WorldServer world, int x, int z, boolean flag) {
        return getOverworldRespawnPos(world, x, z, flag);
    }

    public static void fuckBackToSpawn(WorldServer worldIn, EntityPlayerMP playerIn) {
        ChunkCoordinates blockpos = worldIn.getSpawnPoint();
        playerIn.setPositionAndUpdate(blockpos.posX + 0.5D, blockpos.posY, blockpos.posZ + 0.5D);

        int i = Math.max(0, 0);

        if (i > 0) {
            int k = i * 2 + 1;
            int i1 = k * k;
            int k1 = (new Random()).nextInt(i1);

            for (int l1 = 0; l1 < i1; ++l1) {
                int i2 = (k1 + l1) % i1;
                int j2 = i2 % k;
                int k2 = i2 / k;
                BlockPos blockpos1 = getRespawnLocation(worldIn, blockpos.posX + j2 - i, blockpos.posZ + k2 - i, false);

                if (blockpos1 != null) {
                    playerIn.setPositionAndUpdate(blockpos1.getX() + 0.5D, blockpos1.getY(), blockpos1.getZ() + 0.5D);

                    if (worldIn.checkNoEntityCollision(playerIn.boundingBox)) {
                        break;
                    }
                }
            }
        }

        while (!worldIn.checkNoEntityCollision(playerIn.boundingBox) && playerIn.posY < 256.0D) {
            playerIn.setPositionAndUpdate(playerIn.posX, playerIn.posY + 1.0D, playerIn.posZ);
        }
    }

    @Nullable
    protected static BlockPos getOverworldRespawnPos(WorldServer world, int x, int z, boolean flag) {
        int top = world.getTopSolidOrLiquidBlock(x, z);

        if (top < 0) return null;

        for (int y = top; y >= 0; --y) {
            if (world.getBlock(x, y - 1, z)
                .getMaterial()
                .isSolid() && world.isAirBlock(x, y, z)
                && world.isAirBlock(x, y + 1, z)) return new BlockPos(x, y, z);
        }

        return null;
    }

    @Nullable
    public static BlockPos getSpawnPosInChunk(WorldServer world, int chunkX, int chunkZ, boolean flag) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        for (int i = baseX; i < baseX + 16; ++i) {
            for (int j = baseZ; j < baseZ + 16; ++j) {
                BlockPos blockpos = getOverworldRespawnPos(world, i, j, flag);

                if (blockpos != null) return blockpos;
            }
        }

        return null;
    }

}
