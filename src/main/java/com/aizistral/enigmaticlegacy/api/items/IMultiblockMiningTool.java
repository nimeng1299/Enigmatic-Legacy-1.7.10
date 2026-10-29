package com.aizistral.enigmaticlegacy.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;

public interface IMultiblockMiningTool {

    default boolean areaEffectsEnabled(EntityPlayer player, ItemStack stack) {
        return this.areaEffectsAllowed(stack)
            && (!player.isSneaking() || OmniconfigHandler.disableAOEShiftSuppression.getValue());
    }

    default boolean areaEffectsAllowed(ItemStack stack) {
        if (stack.getItem() instanceof IMultiblockMiningTool)
            return ItemNBTHelper.getBoolean(stack, "MultiblockEffectsEnabled", true);

        return false;
    }

    default void enableAreaEffects(EntityPlayer player, ItemStack stack) {

        if (stack.getItem() instanceof IMultiblockMiningTool) {
            ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", true);

            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_ON).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        }

    }

    default void disableAreaEffects(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof IMultiblockMiningTool) {
            ItemNBTHelper.setBoolean(stack, "MultiblockEffectsEnabled", false);

            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(
                    player,
                    (EnigmaticSounds.CHARGED_OFF).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2F)));
            }
        }
    }

    default void toggleAreaEffects(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof IMultiblockMiningTool) {
            if (this.areaEffectsAllowed(stack)) {
                this.disableAreaEffects(player, stack);
            } else {
                this.enableAreaEffects(player, stack);
            }
        }
    }

}
