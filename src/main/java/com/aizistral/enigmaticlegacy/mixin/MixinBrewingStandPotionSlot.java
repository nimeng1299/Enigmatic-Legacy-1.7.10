package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.helpers.PotionHelper;

/**
 * Allows the mod's advanced potions (which are not vanilla {@code ItemPotion}s)
 * to be placed into the brewing stand's potion slots.
 */
@Mixin(targets = "net.minecraft.inventory.ContainerBrewingStand$Potion")
public class MixinBrewingStandPotionSlot {

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$allowAdvancedPotions(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && PotionHelper.isAdvancedPotion(stack)) {
            cir.setReturnValue(true);
        }
    }

}
