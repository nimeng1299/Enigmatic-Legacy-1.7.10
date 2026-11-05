package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * Client half of the Charm of Treasure Hunter reach bonus.
 * <p>
 * 1.20 declares the bonus as a {@code ForgeMod.BLOCK_REACH} attribute modifier, but 1.7.10
 * hardcodes the value in the client controller and derives everything from it: the block
 * under the crosshair is picked in {@code EntityRenderer#getMouseOver} via this method, so
 * without it the player would not even be able to aim at the blocks the server lets them
 * interact with.
 */
@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    private void enigmaticlegacy$applyCharmReach(CallbackInfoReturnable<Float> cir) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null) return;

        float bonus = SuperpositionHandler.getBlockReachBonus(player);

        if (bonus > 0.0F) {
            cir.setReturnValue(
                cir.getReturnValue()
                    .floatValue() + bonus);
        }
    }

}
