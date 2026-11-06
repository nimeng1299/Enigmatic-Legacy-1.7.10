package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * Et Futurum Requiem owns the entire 1.7.10 elytra implementation (a port of Backlytra): the jump
 * takeoff in {@code MixinEntityPlayerSP}, the flight physics in {@code MixinEntityLivingBase},
 * cape hiding and the durability drain in {@code MixinEntityPlayer#tickElytra}, and the wing render
 * layer in {@code ClientEventHandler#renderPlayerSetArmour}. All of them ask
 * {@code ItemArmorElytra#getElytra(entity)} whether the entity is wearing an elytra, and the flight
 * flag is force-cleared every tick when that returns null.
 * <p>
 * Answering with the Enigmatic Elytra is therefore what actually makes it glide, instead of the
 * client-side only fall-speed clamp the port used to have. {@code getElytra} is not an override of
 * a vanilla method, so no refmap entry is involved and {@code remap = false} is correct.
 */
@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.items.equipment.ItemArmorElytra")
public abstract class MixinEtFuturumElytra {

    @Inject(method = "getElytra", at = @At("RETURN"), cancellable = true, remap = false)
    private static void enigmaticlegacy$resolveEnigmaticElytra(EntityLivingBase entity,
        CallbackInfoReturnable<ItemStack> cir) {
        // Et Futurum's own elytra always wins, otherwise a player wearing both would teleport
        // between the two damage/repair paths.
        if (cir.getReturnValue() != null) return;

        ItemStack stack = SuperpositionHandler.getEnigmaticElytra(entity);

        if (stack != null && stack.getItemDamage() < stack.getMaxDamage()) {
            cir.setReturnValue(stack);
        }
    }

}
