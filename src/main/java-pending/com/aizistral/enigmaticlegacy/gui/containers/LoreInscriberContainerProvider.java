package com.aizistral.enigmaticlegacy.gui.containers;

import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class LoreInscriberContainerProvider implements MenuProvider {
	private Component name;

	public LoreInscriberContainerProvider(Component name) {
		this.name = name;
	}

	public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, EntityPlayer player) {
		return new LoreInscriberContainer(syncId, playerInv);
	}

	public Component getDisplayName() {
		return this.name;
	}

}
