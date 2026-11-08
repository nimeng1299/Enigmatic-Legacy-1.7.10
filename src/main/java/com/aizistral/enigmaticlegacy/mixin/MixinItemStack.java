package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.items.Insignia;

/**
 * Keeps an engraved Charming Insignia named "Charming Insignia" instead of adopting its engraving as
 * the item name.
 * <p>
 * 1.20 does this with {@code MixinItemStack} ({@code getHoverName} returns the item name and
 * {@code hasCustomHoverName} returns false), leaving the engraving to be shown by the dedicated
 * tooltip line. On 1.7.10 {@code ItemStack#getDisplayName} unconditionally prefers
 * {@code display.Name}, so without this the item itself would appear renamed in inventories, the
 * anvil and every inventory-sync packet.
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$insigniaDisplayName(CallbackInfoReturnable<String> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof Insignia) {
            cir.setReturnValue(
                stack.getItem()
                    .getItemStackDisplayName(stack));
        }
    }

    @Inject(method = "hasDisplayName", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$insigniaHasDisplayName(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof Insignia) {
            cir.setReturnValue(false);
        }
    }

}
