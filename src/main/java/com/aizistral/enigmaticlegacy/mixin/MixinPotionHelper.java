package com.aizistral.enigmaticlegacy.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.registries.EnigmaticEffects;

/**
 * 1.7.10's potion metadata decoder has no requirement entries for jump,
 * resistance or slow falling, so potion items for them cannot exist. The
 * mod's 1.20.x recipes rely on those vanilla potions as inputs; this teaches
 * the decoder the three unused bit patterns so they can be brewed.
 */
@Mixin(PotionHelper.class)
public class MixinPotionHelper {

    @Inject(method = "getPotionEffects", at = @At("RETURN"), cancellable = true)
    private static void enigmaticlegacy$addMissingPotionTypes(int combined, boolean isNotWater,
        CallbackInfoReturnable<List<PotionEffect>> cir) {
        // Only real potion item damage values carry the 0x2000 flag.
        if ((combined & 8192) == 0) return;

        List<PotionEffect> vanilla = cir.getReturnValue();
        if (vanilla != null && !vanilla.isEmpty()) return;

        int type = combined & 15;
        Potion potion;

        if (type == 7) {
            potion = Potion.jump;
        } else if (type == 11) {
            potion = Potion.resistance;
        } else if (type == 15) {
            potion = EnigmaticEffects.SLOW_FALLING;
        } else {
            return;
        }

        if (potion == null) return;

        int duration = (combined & 64) != 0 ? 9600 : (combined & 32) != 0 ? 1800 : 3600;

        if ((combined & 16384) != 0) {
            duration = (int) Math.round(duration * 0.75D + 0.5D);
        }

        int amplifier = (combined & 32) != 0 ? 1 : 0;
        PotionEffect effect = new PotionEffect(potion.id, duration, amplifier);

        if ((combined & 16384) != 0) {
            effect.setSplashPotion(true);
        }

        List<PotionEffect> result = new ArrayList<>();
        result.add(effect);
        cir.setReturnValue(result);
    }

}
