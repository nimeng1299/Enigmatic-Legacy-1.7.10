package com.aizistral.enigmaticlegacy.items;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

/**
 * Implemented by items that can be bound to a specific player.
 */
public interface IBound {

    default EntityPlayer getBoundPlayer(World world, ItemStack stack) {
        if (ItemNBTHelper.verifyExistance(stack, "BoundPlayer") && ItemNBTHelper.containsUUID(stack, "BoundUUID")) {
            UUID id = ItemNBTHelper.getUUID(stack, "BoundUUID", null);

            if (id == null || MinecraftServer.getServer() == null) return null;

            for (Object candidate : MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList) {
                if (candidate instanceof EntityPlayer && id.equals(((EntityPlayer) candidate).getUniqueID()))
                    return (EntityPlayer) candidate;
            }
        }

        return null;
    }

}
