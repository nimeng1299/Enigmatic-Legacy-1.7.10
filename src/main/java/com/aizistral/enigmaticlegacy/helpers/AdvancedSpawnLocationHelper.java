package com.aizistral.enigmaticlegacy.helpers;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class AdvancedSpawnLocationHelper {

    public static int getPlayerRespawnDimension(EntityPlayerMP player) {
        return player.dimension;
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

    /**
     * Retrieves the player respawn location.
     */
    public static Optional<Vec3> getValidSpawn(final WorldServer world, final EntityPlayerMP player) {
        ChunkCoordinates blockpos = player.getBedLocation(player.dimension);

        if (world != null && blockpos != null) {
            int y = world.getTopSolidOrLiquidBlock(blockpos.posX, blockpos.posZ);
            return Optional.of(Vec3.createVectorHelper(blockpos.posX + 0.5D, y, blockpos.posZ + 0.5D));
        }

        return Optional.empty();
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
