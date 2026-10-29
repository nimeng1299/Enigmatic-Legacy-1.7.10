package com.aizistral.enigmaticlegacy.handlers;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Teleport helper; 1.7.10 handles cross-dimension travel through
 * {@code ServerTravelToDimensionEvent} / direct positioning instead of the
 * modern {@code ITeleporter} interface.
 */
public class RealSmoothTeleporter {

    public RealSmoothTeleporter() {

    }

    public RealSmoothTeleporter(EntityPlayerMP serverPlayer, double x, double y, double z) {
        serverPlayer.setPositionAndUpdate(x, y, z);
    }

}
