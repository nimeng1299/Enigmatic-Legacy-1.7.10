package com.aizistral.enigmaticlegacy.objects;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Notification data for curio slot unlocks; rendering is handled by the client
 * overlay handler.
 */
@SideOnly(Side.CLIENT)
public class SlotUnlockedToast {

    private final ItemStack drawnStack;
    private final String identifier;
    private long firstDrawTime;

    public SlotUnlockedToast(ItemStack stack, String id) {
        this.drawnStack = stack;
        this.identifier = id;
        this.firstDrawTime = System.currentTimeMillis();
    }

    public ItemStack getStack() {
        return this.drawnStack;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public long getFirstDrawTime() {
        return this.firstDrawTime;
    }

}
