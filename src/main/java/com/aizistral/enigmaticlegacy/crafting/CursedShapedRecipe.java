package com.aizistral.enigmaticlegacy.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ITaintable;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

/**
 * Shaped recipe that only matches when every taintable item placed in the
 * crafting grid is tainted. Extends {@link ShapedRecipes} so NEI's default
 * shaped recipe handler can display it.
 */
public class CursedShapedRecipe extends ShapedRecipes {

    public CursedShapedRecipe(int width, int height, ItemStack[] items, ItemStack result) {
        super(width, height, items, result);
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

        return super.matches(inv, world);
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
