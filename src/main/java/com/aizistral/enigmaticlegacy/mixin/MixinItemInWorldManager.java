package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ItemInWorldManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * Server half of the Charm of Treasure Hunter reach bonus.
 * <p>
 * Unlike modern versions, vanilla 1.7.10 does validate reach on the server:
 * {@code NetHandlerPlayServer} rejects digging and placing when the block is farther than
 * {@code ItemInWorldManager#getBlockReachDistance() + 1}, and
 * {@code Item#getMovingObjectPositionFromPlayer} uses the same value for item ray traces.
 * Boosting only the client would therefore desync aim and server, so the bonus is applied
 * to both sides of the equation.
 */
@Mixin(ItemInWorldManager.class)
public class MixinItemInWorldManager {

    // getBlockReachDistance is a Forge addition (vanilla 1.7.10 has neither the method nor
    // the reach field), so it carries no SRG name and must not be remapped.
    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true, remap = false)
    private void enigmaticlegacy$applyCharmReach(CallbackInfoReturnable<Double> cir) {
        EntityPlayerMP player = ((MixinItemInWorldManagerAccessor) (Object) this).enigmaticlegacy$getThisPlayerMP();

        if (player == null) return;

        float bonus = SuperpositionHandler.getBlockReachBonus(player);

        if (bonus > 0.0F) {
            cir.setReturnValue(cir.getReturnValue() + bonus);
        }
    }

}
