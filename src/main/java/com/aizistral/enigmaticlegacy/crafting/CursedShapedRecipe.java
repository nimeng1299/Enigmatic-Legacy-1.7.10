package com.aizistral.enigmaticlegacy.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ITaintable;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

/**
 * Shaped recipe wrapper that only matches when every taintable item placed in
 * the crafting grid is tainted.
 */
public class CursedShapedRecipe implements IRecipe {

    private final ShapedRecipes wrapped;

    public CursedShapedRecipe(int width, int height, ItemStack[] items, ItemStack result) {
        this.wrapped = new ShapedRecipes(width, height, items, result);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof ITaintable
                && !((ITaintable) stack.getItem()).isTainted(stack)) {
                return false;
            }
        }

        return this.wrapped.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.wrapped.getCraftingResult(inv);
    }

    @Override
    public int getRecipeSize() {
        return this.wrapped.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.wrapped.getRecipeOutput();
    }

    /**
     * Marks every taintable item in the given ingredient array as tainted. The
     * 1.20 serializer did the same for ingredients deserialized from JSON.
     */
    public static ItemStack[] markTainted(ItemStack[] items) {
        if (items == null) return null;

        for (ItemStack stack : items) {
            if (stack != null && stack.getItem() instanceof ITaintable) {
                ItemNBTHelper.setBoolean(stack, "isTainted", true);
            }
        }

        return items;
    }

}
