package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.curios.CuriosApi;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * 1.7.10 has no Curios dispatch for loot levels, so bauble bonuses are folded
 * into the vanilla helpers every drop calculation goes through.
 */
@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "getFortuneModifier", at = @At("RETURN"), cancellable = true)
    private static void enigmaticlegacy$addCurioFortune(EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + SuperpositionHandler.getCurioFortuneBonus(entity));
    }

    @Inject(method = "getLootingModifier", at = @At("RETURN"), cancellable = true)
    private static void enigmaticlegacy$addCurioLooting(EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + SuperpositionHandler.getCurioLootingBonus(entity, null, null));
    }

    /**
     * Feeds curio luck into fishing.
     * <p>
     * 1.20 rolls fishing loot from a loot table whose luck parameter comes from the player's
     * {@code Attributes.LUCK}; 1.7.10 instead passes an explicit luck level into Forge's
     * {@code FishingHooks}, so the curio luck bonus is added to the Luck of the Sea level here.
     * Fishing is the only vanilla mechanic on this version that consumes luck (chest loot is
     * rolled at world generation, long before any player could influence it), and this method
     * has exactly one caller - {@code EntityFishHook}.
     */
    @Inject(method = "func_151386_g", at = @At("RETURN"), cancellable = true)
    private static void enigmaticlegacy$addCurioFishingLuck(EntityLivingBase entity,
        CallbackInfoReturnable<Integer> cir) {
        if (!(entity instanceof EntityPlayer)) return;

        double bonus = CuriosApi.getEquippedLuckBonus((EntityPlayer) entity);

        if (bonus > 0.0D) {
            cir.setReturnValue(cir.getReturnValue() + (int) bonus);
        }
    }

}
