package com.aizistral.enigmaticlegacy.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/**
 * Registers the crafting recipes that cannot be expressed through vanilla
 * shaped or shapeless recipes. Recipes backed by JSON resources are created by
 * the JSON recipe loader through the factory methods below.
 */
public final class EnigmaticRecipeRegistrar {

    private EnigmaticRecipeRegistrar() {
        // NO-OP
    }

    /**
     * Registers the special recipes that are fully defined in code.
     */
    public static void register() {
        List<IRecipe> recipes = CraftingManager.getInstance()
            .getRecipeList();

        recipes.add(new MendingMixtureRepairRecipe());
        recipes.add(new EnchantmentTransposingRecipe());
        recipes.add(new OblivionStoneCombineRecipe());
        recipes.add(new HiddenRecipe());
    }

    /**
     * Creates a blessed shaped recipe, marking its Cosmic Heart ingredients as
     * blessed in the same way the 1.20 recipe serializer did.
     */
    public static BlessedShapedRecipe blessedShaped(int width, int height, ItemStack[] items, ItemStack result) {
        return new BlessedShapedRecipe(width, height, BlessedShapedRecipe.markBlessed(items), result);
    }

    /**
     * Creates a cursed shaped recipe, marking its taintable ingredients as
     * tainted in the same way the 1.20 recipe serializer did.
     */
    public static CursedShapedRecipe cursedShaped(int width, int height, ItemStack[] items, ItemStack result) {
        return new CursedShapedRecipe(width, height, CursedShapedRecipe.markTainted(items), result);
    }

    /**
     * Creates a shapeless recipe whose container items are not returned.
     */
    public static ShapelessNoReturnRecipe shapelessNoReturn(ItemStack output, List<ItemStack> inputs) {
        return new ShapelessNoReturnRecipe(output, inputs);
    }

    /**
     * Creates the hidden recipe instance. The recipe grids themselves are added
     * through {@link HiddenRecipe#addRecipe(ItemStack, ItemStack...)}; the
     * returned instance has to be added to the crafting manager before those
     * grids can be matched.
     */
    public static HiddenRecipe hiddenRecipe() {
        return new HiddenRecipe();
    }

}
