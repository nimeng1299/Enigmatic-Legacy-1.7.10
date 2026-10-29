package com.aizistral.enigmaticlegacy.api.items;

import net.minecraft.entity.player.EntityPlayer;

import com.aizistral.enigmaticlegacy.compat.BlockState;

public interface IBaseTool {

    public float getEfficiency();

    default boolean canHarvestBlock(BlockState blockIn, EntityPlayer player) {
        return false;
    }

    /*
     * default float getDestroySpeed(ItemStack stack, BlockState state) {
     * Material material = state.getMaterial();
     * return !this.getEffectiveMaterials().contains(material) ? super.getDestroySpeed(stack, state) : this.efficiency;
     * }
     */

}
