package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.item.ItemStack;

/**
 * ItemStack helpers mirroring the modern split API.
 */
public final class StackHelper {

    private StackHelper() {
        // NO-OP
    }

    public static ItemStack split(ItemStack stack, int amount) {
        if (stack == null) return null;

        amount = Math.min(amount, stack.stackSize);
        ItemStack result = stack.copy();
        result.stackSize = amount;
        stack.stackSize -= amount;

        return result;
    }

    public static void shrink(ItemStack stack, int amount) {
        if (stack != null) {
            stack.stackSize -= amount;
        }
    }

}
