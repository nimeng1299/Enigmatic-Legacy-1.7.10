package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.aizistral.enigmaticlegacy.items.EnigmaticElytra;

/**
 * Et Futurum's wing layer is not usable for the Enigmatic Elytra: besides drawing the vanilla
 * elytra model it picks the player's cape texture whenever the player has a cape, which is
 * meaningless for an item with its own wings. It is suppressed here and re-implemented by
 * {@code EnigmaticElytraLayer} with {@code textures/models/misc/elytra.png}.
 */
@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra")
public abstract class MixinEtFuturumElytraLayer {

    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true, remap = false)
    private static void enigmaticlegacy$skipVanillaElytraLayer(EntityLivingBase entity, float limbSwing,
        float limbSwingAmount, float partialTicks, float ageInTicks, float scale, CallbackInfo ci) {
        if (EnigmaticElytra.getActiveElytra(entity) != null) {
            ci.cancel();
        }
    }

}
