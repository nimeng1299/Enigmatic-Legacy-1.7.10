package com.aizistral.enigmaticlegacy.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Special recipe type for repairing of damageable items with Mending Mixture.
 * 
 * @author Integral
 */
public class MendingMixtureRepairRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return this.findTool(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack tool = this.findTool(inv);

        if (tool == null) return null;

        ItemStack result = tool.copy();
        result.setItemDamage(0);

        return result;
    }

    private ItemStack findTool(InventoryCrafting inv) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slotStack = inv.getStackInSlot(i);

            if (slotStack != null && slotStack.stackSize > 0) {
                stackList.add(slotStack);
            }
        }

        if (stackList.size() != 2) return null;

        ItemStack first = stackList.get(0);
        ItemStack second = stackList.get(1);

        if (!first.isItemStackDamageable() && !second.isItemStackDamageable()) return null;

        if (first.getItem() != EnigmaticItems.MENDING_MIXTURE && second.getItem() != EnigmaticItems.MENDING_MIXTURE)
            return null;

        return first.isItemStackDamageable() ? first : second;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
