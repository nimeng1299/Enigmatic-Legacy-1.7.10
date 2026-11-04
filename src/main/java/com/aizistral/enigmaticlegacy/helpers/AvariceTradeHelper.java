package com.aizistral.enigmaticlegacy.helpers;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;

/**
 * Replicates the per-player trade discount of Pact of Infinite Avarice. 1.7.10
 * stores villager prices on shared {@link MerchantRecipe} instances, so each
 * player gets a discounted copy while trade uses are reported back to the
 * original recipe.
 */
public final class AvariceTradeHelper {

    private static final double DISCOUNT_RATE = 0.35D;
    private static final Map<MerchantRecipe, MerchantRecipe> DISCOUNTED_TO_ORIGINAL = Collections
        .synchronizedMap(new WeakHashMap<>());

    private AvariceTradeHelper() {
        // NO-OP
    }

    public static MerchantRecipe discountedCopy(MerchantRecipe original) {
        if (original == null) return null;

        ItemStack cost = original.getItemToBuy();

        if (cost == null || cost.stackSize <= 0) return original;

        int discount = Math.max(1, (int) Math.floor(DISCOUNT_RATE * cost.stackSize));
        int discountedCount = Math.max(1, cost.stackSize - discount);

        if (discountedCount >= cost.stackSize) return original;

        NBTTagCompound tag = original.writeToTags();
        NBTTagCompound buyTag = tag.getCompoundTag("buy");
        buyTag.setByte("Count", (byte) discountedCount);
        tag.setTag("buy", buyTag);

        MerchantRecipe discounted = new MerchantRecipe(tag);
        DISCOUNTED_TO_ORIGINAL.put(discounted, original);
        return discounted;
    }

    public static void propagateUse(MerchantRecipe recipe) {
        MerchantRecipe original = DISCOUNTED_TO_ORIGINAL.get(recipe);

        if (original != null) {
            original.incrementToolUses();
        }
    }

}
