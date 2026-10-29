package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry;
import com.aizistral.enigmaticlegacy.compat.brewing.IBrewingRecipe;

/**
 * Lets the vanilla brewing stand start and maintain a brew cycle for the mod's
 * custom potion recipes, which the hardcoded 1.7.10 {@code canBrew} check would
 * otherwise reject. The actual items are replaced in
 * {@code EnigmaticEventHandler#onPotionBrewAttempt} when
 * {@code PotionBrewEvent.Pre} fires at the end of the cycle.
 */
@Mixin(TileEntityBrewingStand.class)
public class MixinTileEntityBrewingStand {

    @Shadow
    private ItemStack[] brewingItemStacks;

    @Inject(method = "canBrew", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$allowCustomBrewing(CallbackInfoReturnable<Boolean> cir) {
        ItemStack ingredient = this.brewingItemStacks[3];

        if (ingredient == null || ingredient.stackSize <= 0) return;

        for (int i = 0; i < 3; i++) {
            ItemStack input = this.brewingItemStacks[i];

            if (input == null) continue;

            IBrewingRecipe recipe = BrewingRecipeRegistry.getRecipe(input, ingredient);

            if (recipe != null && recipe.getOutput(input, ingredient) != null) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "isItemValidForSlot", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$allowCustomSlots(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;

        if (index == 3) {
            if (BrewingRecipeRegistry.isIngredient(stack)) {
                cir.setReturnValue(Boolean.TRUE);
            }
        } else if (index >= 0 && index <= 2) {
            if (com.aizistral.enigmaticlegacy.helpers.PotionHelper.isAdvancedPotion(stack)) {
                cir.setReturnValue(Boolean.TRUE);
            }
        }
    }

}
