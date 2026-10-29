package com.aizistral.enigmaticlegacy.api.items;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ISpellstone {

	default void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
		if (SuperpositionHandler.hasSpellstoneCooldown(player))
			return;
	}

}
