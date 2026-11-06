package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.items.EnigmaticElytra;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.network.StartElytraFlyingMessage;

/**
 * Et Futurum's takeoff packet handler is the only place the flight flag can be switched on - the
 * per-tick {@code tickElytra} merely sustains it - and it hard checks {@code instanceof
 * ItemArmorElytra} before doing so. Answering {@code getElytra} with the Enigmatic Elytra
 * (see {@code MixinEtFuturumElytra}) gets the client to send the packet, but the check still
 * rejects it, so the takeoff is repeated here for this item only. Et Futurum's own elytra keeps
 * going through the untouched original.
 */
@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.network.StartElytraFlyingHandler")
public abstract class MixinEtFuturumElytraTakeoff {

    @Inject(method = "onMessage", at = @At("HEAD"), cancellable = true, remap = false)
    private void enigmaticlegacy$startEnigmaticElytraFlight(StartElytraFlyingMessage message, MessageContext ctx,
        CallbackInfoReturnable<IMessage> cir) {
        if (!(ctx.netHandler instanceof NetHandlerPlayServer)) return;

        EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;

        if (player == null) return;

        // A real Et Futurum elytra wins over the bauble, so leave the original logic to it.
        if (EnigmaticElytra.getActiveElytra(player) == null) return;

        if (!player.onGround && !player.isInWater()
            && (ConfigMixins.enableNewElytraTakeoffLogic || player.motionY < 0.0D)
            && !EnigmaticElytra.isFallFlying(player)) {
            ((IElytraPlayer) player).etfu$setElytraFlying(true);
        }

        cir.setReturnValue(null);
    }

}
