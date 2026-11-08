package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.WeightedRandomChestContent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.aizistral.enigmaticlegacy.objects.SpecialLootModifier;
import com.aizistral.enigmaticlegacy.registries.EnigmaticLootModifiers;

/**
 * Appends the Tattered Tome to already-populated chests, replicating the independent loot pools
 * 1.20 adds on top of the vanilla contents. See
 * {@link EnigmaticLootModifiers#onChestContentsGenerated} for the probability model.
 * <p>
 * Also flags the chest as structure loot, which is what allows
 * {@link SpecialLootModifier#onChestOpened} to tell a dungeon chest from a player's own storage.
 */
@Mixin(WeightedRandomChestContent.class)
public class MixinWeightedRandomChestContent {

    @Inject(method = "generateChestContents", at = @At("RETURN"))
    private static void enigmaticlegacy$markLootChest(Random rand, WeightedRandomChestContent[] items,
        IInventory inventory, int count, CallbackInfo ci) {
        SpecialLootModifier.markLootChest(inventory);
    }

    @Inject(method = "generateChestContents", at = @At("RETURN"))
    private static void enigmaticlegacy$appendLiterature(Random rand, WeightedRandomChestContent[] items,
        IInventory inventory, int count, CallbackInfo ci) {
        EnigmaticLootModifiers.onChestContentsGenerated(rand, items, inventory);
    }

}
