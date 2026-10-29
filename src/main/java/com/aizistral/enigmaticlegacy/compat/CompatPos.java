package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public final class CompatPos {

    private CompatPos() {
        // NO-OP
    }

    public static BlockPos of(Entity entity) {
        return new BlockPos(
            MathHelper.floor_double(entity.posX),
            MathHelper.floor_double(entity.posY),
            MathHelper.floor_double(entity.posZ));
    }

    public static BlockPos of(double x, double y, double z) {
        return new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public static BlockPos above(BlockPos pos) {
        return new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
    }

    public static BlockPos below(BlockPos pos) {
        return new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
    }

    public static BlockPos north(BlockPos pos) {
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
    }

    public static BlockPos south(BlockPos pos) {
        return new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
    }

    public static BlockPos east(BlockPos pos) {
        return new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
    }

    public static BlockPos west(BlockPos pos) {
        return new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
    }

    public static BlockPos offset(BlockPos pos, int x, int y, int z) {
        return new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

}
