package com.aizistral.etherium.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

public interface IEtheriumTool {

    default IEtheriumConfig getConfig() {
        return EtheriumConfigHandler.instance();
    }

    default boolean areaEffectsEnabled(EntityPlayer player, ItemStack stack) {
        return this.areaEffectsAllowed(stack) && (!player.isSneaking() || this.getConfig()
            .disableAOEShiftInhibition());
    }

    default boolean areaEffectsAllowed(ItemStack stack) {
        if (stack.getItem() instanceof IEtheriumTool)
            return ItemNBTHelper.getBoolean(stack, "MultiblockEffectsEnabled", true);

        return false;
    }

    default void enableAreaEffects(EntityPlayer player, ItemStack stack) {

        if (stack.getItem() instanceof IEtheriumTool) {
            ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", true);

            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(
                    player,
                    (this.getConfig()
                        .getAOESoundOn()).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        }

    }

    default void disableAreaEffects(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof IEtheriumTool) {
            ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", false);

            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(
                    player,
                    (this.getConfig()
                        .getAOESoundOff()).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        }
    }

    default void toggleAreaEffects(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof IEtheriumTool) {
            if (this.areaEffectsAllowed(stack)) {
                this.disableAreaEffects(player, stack);
            } else {
                this.enableAreaEffects(player, stack);
            }
        }
    }

}
