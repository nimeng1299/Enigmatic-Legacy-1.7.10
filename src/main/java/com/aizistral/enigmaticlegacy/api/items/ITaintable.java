package com.aizistral.enigmaticlegacy.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

public interface ITaintable {

    default void handleTaintable(ItemStack stack, EntityPlayer player) {
        if (SuperpositionHandler.isTheCursedOne(player)) {
            if (!ItemNBTHelper.getBoolean(stack, "isTainted", false)) {
                ItemNBTHelper.setBoolean(stack, "isTainted", true);
            }
        } else {
            if (ItemNBTHelper.getBoolean(stack, "isTainted", false)) {
                ItemNBTHelper.setBoolean(stack, "isTainted", false);
            }
        }
    }

    default boolean isTainted(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, "isTainted", false);
    }

}
