package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry;

/**
 * Allows the mod's brewing ingredients to be placed into the brewing stand's
 * ingredient slot.
 */
@Mixin(targets = "net.minecraft.inventory.ContainerBrewingStand$Ingredient")
public class MixinBrewingStandIngredientSlot {

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$allowCustomIngredients(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && BrewingRecipeRegistry.isIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }

}
