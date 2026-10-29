package com.aizistral.enigmaticlegacy.compat.brewing;

import net.minecraft.item.ItemStack;

/**
 * Minimal brewing recipe interface; 1.7.10 has no Forge brewing registry.
 */
public interface IBrewingRecipe {

    boolean isInput(ItemStack stack);

    boolean isIngredient(ItemStack stack);

    ItemStack getOutput(ItemStack input, ItemStack ingredient);

}
