package com.aizistral.enigmaticlegacy.client.nei;

import net.minecraft.item.ItemStack;

import com.aizistral.enigmaticlegacy.crafting.HiddenRecipe;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapedRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Makes the mod's hidden 3x3 grids visible in NotEnoughItems. A single
 * {@link HiddenRecipe} instance holds every grid and reports no recipe output,
 * so NEI's default shaped handler cannot enumerate any of them. This handler
 * renders those grids as ordinary shaped recipes while crafting itself still
 * goes through {@code HiddenRecipe}, keeping its output and NBT handling.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticHiddenRecipeHandler extends ShapedRecipeHandler {

    private static final int GRID_WIDTH = 3;
    private static final String HIDDEN_OUTPUT_ID = "enigmaticlegacy:hidden_crafting";

    @Override
    public String getRecipeName() {
        return NEIClientUtils.translate("recipe.shaped") + " (Enigmatic Legacy)";
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if ("item".equals(outputId)) {
            super.loadCraftingRecipes(outputId, results);
            return;
        }

        if ("crafting".equals(outputId) || "all".equals(outputId) || HIDDEN_OUTPUT_ID.equals(outputId)) {
            for (HiddenRecipe.RecipeGrid grid : HiddenRecipe.getRecipes()) {
                this.arecipes.add(cached(grid));
            }

            return;
        }

        super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) return;

        for (HiddenRecipe.RecipeGrid grid : HiddenRecipe.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(grid.getOutput(), result)) {
                this.arecipes.add(cached(grid));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) return;

        for (HiddenRecipe.RecipeGrid grid : HiddenRecipe.getRecipes()) {
            CachedShapedRecipe recipe = cached(grid);

            if (!recipe.contains(recipe.ingredients, ingredient)) continue;

            recipe.setIngredientPermutation(recipe.ingredients, ingredient);
            this.arecipes.add(recipe);
        }
    }

    private CachedShapedRecipe cached(HiddenRecipe.RecipeGrid grid) {
        CachedShapedRecipe recipe = new CachedShapedRecipe(GRID_WIDTH, GRID_WIDTH, grid.getInputs(), grid.getOutput());
        recipe.computeVisuals();
        return recipe;
    }

}
