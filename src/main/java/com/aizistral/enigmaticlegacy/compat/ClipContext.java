package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Stand-in for the modern clip context, providing the fluid mode enum and a
 * 1.7.10 ray trace implementation.
 */
public class ClipContext {

    public enum Fluid {
        NONE,
        SOURCE_ONLY,
        ANY
    }

    public static MovingObjectPosition rayTrace(World world, EntityPlayer player, Fluid fluidMode) {
        double reach = 5.0D;
        double eyeHeight = player.getEyeHeight();
        Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
        Vec3 look = player.getLookVec();
        Vec3 end = start.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

        boolean stopOnLiquid = fluidMode != Fluid.NONE;
        boolean ignoreBlockWithoutBoundingBox = true;

        return world.func_147447_a(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
    }

}
