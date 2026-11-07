package com.aizistral.enigmaticlegacy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20 blocks shulker teleportation through {@code MixinShulker#onTeleportSomewhere}. Shulkers are
 * not vanilla on 1.7.10 - they come from Et Futurum, whose {@code EntityShulker} teleports through
 * {@code tryTeleportToNewPosition()} without firing {@code EnderTeleportEvent}, so the
 * {@code ELTeleportBlock} check in {@code EnigmaticEventHandler#onEnderTeleport} never sees them.
 * <p>
 * The target method is Et Futurum's own, not a vanilla override, hence {@code remap = false}.
 */
@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.entities.EntityShulker")
public abstract class MixinEtFuturumShulkerTeleport {

    @Inject(method = "tryTeleportToNewPosition", at = @At("HEAD"), cancellable = true, remap = false)
    private void enigmaticlegacy$blockTeleport(CallbackInfoReturnable<Boolean> cir) {
        if (((net.minecraft.entity.Entity) (Object) this).getEntityData()
            .hasKey("ELTeleportBlock")) {
            cir.setReturnValue(false);
        }
    }

}
