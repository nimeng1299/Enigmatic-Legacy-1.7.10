package com.aizistral.enigmaticlegacy.compat.brewing;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class BrewingRecipeRegistry {

    private static final List<IBrewingRecipe> RECIPES = new ArrayList<>();

    public static void addRecipe(IBrewingRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static IBrewingRecipe getRecipe(ItemStack input, ItemStack ingredient) {
        if (input == null || ingredient == null) return null;

        for (IBrewingRecipe recipe : RECIPES) {
            if (recipe.isInput(input) && recipe.isIngredient(ingredient)) return recipe;
        }

        return null;
    }

    public static boolean isInput(ItemStack input) {
        for (IBrewingRecipe recipe : RECIPES) {
            if (recipe.isInput(input)) return true;
        }

        return false;
    }

    public static boolean isIngredient(ItemStack ingredient) {
        for (IBrewingRecipe recipe : RECIPES) {
            if (recipe.isIngredient(ingredient)) return true;
        }

        return false;
    }

    public static List<IBrewingRecipe> getRecipes() {
        return RECIPES;
    }

}
