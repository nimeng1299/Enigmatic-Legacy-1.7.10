package com.aizistral.enigmaticlegacy.objects;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Notification data for revelation tome usage; rendering is handled by the
 * client overlay handler.
 */
@SideOnly(Side.CLIENT)
public class RevelationTomeToast {

    private final ItemStack tome;
    private final int xpPoints;
    private final int revelationPoints;
    private long firstDrawTime;

    public RevelationTomeToast(ItemStack tome, int xpPoints, int revelationPoints) {
        this.tome = tome;
        this.xpPoints = xpPoints;
        this.revelationPoints = revelationPoints;
        this.firstDrawTime = System.currentTimeMillis();
    }

    public ItemStack getTome() {
        return this.tome;
    }

    public int getXpPoints() {
        return this.xpPoints;
    }

    public int getRevelationPoints() {
        return this.revelationPoints;
    }

    public long getFirstDrawTime() {
        return this.firstDrawTime;
    }

}
