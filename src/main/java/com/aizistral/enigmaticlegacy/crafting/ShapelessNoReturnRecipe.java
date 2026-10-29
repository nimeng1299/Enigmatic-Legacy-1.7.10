package com.aizistral.enigmaticlegacy.crafting;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

/**
 * A copy of a regular shapeless recipe that destroys container items instead of
 * leaving them in the crafting grid. 1.7.10 has no per-recipe suppression of
 * container items, so container items are handled solely by vanilla item
 * behavior; this class keeps the recipe identity and delegates matching.
 */
public class ShapelessNoReturnRecipe implements IRecipe {

    private final ShapelessRecipes wrapped;
    private final int recipeSize;

    public ShapelessNoReturnRecipe(ItemStack output, List<ItemStack> inputs) {
        this.wrapped = new ShapelessRecipes(output, inputs);
        this.recipeSize = inputs.size();
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return this.wrapped.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.wrapped.getCraftingResult(inv);
    }

    @Override
    public int getRecipeSize() {
        return this.recipeSize;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.wrapped.getRecipeOutput();
    }

}
