package com.aizistral.enigmaticlegacy.crafting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.CosmicHeart;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Hidden 3x3 recipe type. Recipes are stored as flat nine-slot grids and are
 * matched in any position, but never shown by recipe viewers.
 * 
 * @author Integral
 */
public class HiddenRecipe implements IRecipe {

    private static final Map<ItemStack[], ItemStack> RECIPES = new HashMap<ItemStack[], ItemStack>();
    private static final int GRID_SIZE = 9;

    public static void addRecipe(ItemStack output, ItemStack... inputs) {
        if (inputs.length != GRID_SIZE) throw new IllegalArgumentException("Hidden recipe must have exactly 9 inputs");

        ItemStack[] grid = new ItemStack[GRID_SIZE];
        System.arraycopy(inputs, 0, grid, 0, GRID_SIZE);

        RECIPES.put(grid, output);
    }

    private static Map.Entry<ItemStack[], ItemStack> findRecipe(InventoryCrafting inv) {
        if (inv.getSizeInventory() < GRID_SIZE) return null;

        for (Map.Entry<ItemStack[], ItemStack> entry : RECIPES.entrySet()) {
            if (HiddenRecipe.matchesGrid(inv, entry)) return entry;
        }

        return null;
    }

    private static boolean matchesGrid(InventoryCrafting inv, Map.Entry<ItemStack[], ItemStack> entry) {
        ItemStack output = entry.getValue();
        boolean cosmicScroll = output != null && output.getItem() == EnigmaticItems.COSMIC_SCROLL;

        for (int i = 0; i < GRID_SIZE; i++) {
            ItemStack slotStack = inv.getStackInSlot(i);
            ItemStack requiredStack = entry.getKey()[i];

            if (requiredStack == null || requiredStack.getItem() == null) {
                if (slotStack != null && slotStack.getItem() != null) return false;

                continue;
            }

            if (slotStack == null || slotStack.getItem() != requiredStack.getItem()) return false;

            if (cosmicScroll && slotStack.getItem() instanceof CosmicHeart
                && !ItemNBTHelper.getBoolean(slotStack, "isBelieverBlessed", false)) return false;
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
