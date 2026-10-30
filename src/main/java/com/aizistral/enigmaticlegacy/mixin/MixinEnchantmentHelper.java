package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

}
