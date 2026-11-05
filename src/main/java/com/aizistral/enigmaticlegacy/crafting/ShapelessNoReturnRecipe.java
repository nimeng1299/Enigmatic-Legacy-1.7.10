package com.aizistral.enigmaticlegacy.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

/**
 * A shapeless recipe that destroys container items instead of leaving them in
 * the crafting grid. 1.7.10 has no per-recipe suppression of container items,
 * so container items are handled solely by vanilla item behavior; this class
 * keeps the recipe identity. Extends {@link ShapelessRecipes} so NEI's default
 * shapeless recipe handler can display it.
 */
public class ShapelessNoReturnRecipe extends ShapelessRecipes {

    public ShapelessNoReturnRecipe(ItemStack output, List<ItemStack> inputs) {
        super(output, inputs);
    }

}
