package com.aizistral.enigmaticlegacy.compat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Very small stand-in for the modern crafting ingredient type. Only the parts
 * required by the ported item/base code are implemented.
 */
public class Ingredient {

    public static final Ingredient EMPTY = new Ingredient(new ItemStack[0]);

    private final ItemStack[] matchingStacks;

    protected Ingredient(ItemStack[] matchingStacks) {
        this.matchingStacks = matchingStacks;
    }

    public static Ingredient of(ItemStack... stacks) {
        return new Ingredient(stacks);
    }

    public static Ingredient of(Item item) {
        return new Ingredient(new ItemStack[] { new ItemStack(item) });
    }

    public static Ingredient of(net.minecraft.block.Block block) {
        return new Ingredient(new ItemStack[] { new ItemStack(block) });
    }

    public ItemStack[] getItems() {
        return this.matchingStacks;
    }

    public List<ItemStack> getMatchingStacks() {
        return Collections.unmodifiableList(Arrays.asList(this.matchingStacks));
    }

    public boolean test(ItemStack stack) {
        if (stack == null) return false;

        for (ItemStack candidate : this.matchingStacks) {
            if (candidate != null && candidate.getItem() == stack.getItem()) {
                if (candidate.getItemDamage() == 32767 || candidate.getItemDamage() == stack.getItemDamage())
                    return true;
            }
        }

        return false;
    }

}
