package com.aizistral.enigmaticlegacy.api.items;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

public interface ISpellstone {

    default void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (SuperpositionHandler.hasSpellstoneCooldown(player)) return;
    }

}
