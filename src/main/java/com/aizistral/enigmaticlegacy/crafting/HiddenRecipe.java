package com.aizistral.enigmaticlegacy.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Special 3x3 recipe type. Recipes are stored as flat nine-slot grids that are
 * matched directly against the crafting grid. The grids are also exposed
 * through {@link #getRecipes()} so recipe viewers such as NEI can display them,
 * while this class keeps its custom output and NBT handling.
 *
 * @author Integral
 */
public class HiddenRecipe implements IRecipe {

    private static final Map<ItemStack[], ItemStack> RECIPES = new LinkedHashMap<ItemStack[], ItemStack>();
    private static final int GRID_SIZE = 9;

    public static void addRecipe(ItemStack output, ItemStack... inputs) {
        if (inputs.length != GRID_SIZE) throw new IllegalArgumentException("Hidden recipe must have exactly 9 inputs");

        ItemStack[] grid = new ItemStack[GRID_SIZE];
        System.arraycopy(inputs, 0, grid, 0, GRID_SIZE);

        RECIPES.put(grid, output);
    }

    /**
     * Returns immutable snapshots of every registered grid, in registration
     * order. Intended for recipe viewers; callers receive copies and cannot
     * mutate the recipes used for matching.
     */
    public static List<RecipeGrid> getRecipes() {
        List<RecipeGrid> snapshots = new ArrayList<RecipeGrid>(RECIPES.size());

        for (Map.Entry<ItemStack[], ItemStack> entry : RECIPES.entrySet()) {
            ItemStack[] inputs = new ItemStack[GRID_SIZE];

            for (int i = 0; i < GRID_SIZE; i++) {
                ItemStack input = entry.getKey()[i];
                inputs[i] = input == null ? null : input.copy();
            }

            ItemStack output = entry.getValue();

            if (output == null || output.getItem() == null) continue;

            snapshots.add(new RecipeGrid(inputs, output.copy()));
        }

        return Collections.unmodifiableList(snapshots);
    }

    /**
     * Read-only snapshot of a single hidden recipe grid.
     */
    public static final class RecipeGrid {

        private final ItemStack[] inputs;
        private final ItemStack output;

        private RecipeGrid(ItemStack[] inputs, ItemStack output) {
            this.inputs = inputs;
            this.output = output;
        }

        public ItemStack[] getInputs() {
            ItemStack[] copy = new ItemStack[this.inputs.length];

            for (int i = 0; i < this.inputs.length; i++) {
                copy[i] = this.inputs[i] == null ? null : this.inputs[i].copy();
            }

            return copy;
        }

        public ItemStack getOutput() {
            return this.output == null ? null : this.output.copy();
        }
    }

    private static Map.Entry<ItemStack[], ItemStack> findRecipe(InventoryCrafting inv) {
        if (inv.getSizeInventory() < GRID_SIZE) return null;

        for (Map.Entry<ItemStack[], ItemStack> entry : RECIPES.entrySet()) {
            if (HiddenRecipe.matchesGrid(inv, entry)) return entry;
        }

        return null;
    }

    private static boolean matchesGrid(InventoryCrafting inv, Map.Entry<ItemStack[], ItemStack> entry) {
        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack slotStack = inv.getStackInSlot(i);
            ItemStack requiredStack = entry.getKey()[i];

            if (requiredStack == null || requiredStack.getItem() == null) {
                if (slotStack != null && slotStack.getItem() != null) return false;

                continue;
            }

            if (slotStack == null || slotStack.getItem() != requiredStack.getItem()) return false;
        }

        return true;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return HiddenRecipe.findRecipe(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        Map.Entry<ItemStack[], ItemStack> entry = HiddenRecipe.findRecipe(inv);

        if (entry == null) return null;

        ItemStack output = entry.getValue()
            .copy();

        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if (slotStack != null
                && (slotStack.getItem() == EnigmaticItems.ENIGMATIC_AMULET
                    || slotStack.getItem() == EnigmaticItems.ASCENSION_AMULET)
                && slotStack.hasTagCompound()) {
                output.setTagCompound(
                    (NBTTagCompound) slotStack.getTagCompound()
                        .copy());
            }
        }

        if (!OmniconfigHandler.isItemEnabled(output.getItem())) return null;

        return output;
    }

    @Override
    public int getRecipeSize() {
        return GRID_SIZE;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
