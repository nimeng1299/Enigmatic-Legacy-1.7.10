package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes PotionEffect's private duration so Blazing Core can slow Molten Heart's
 * decay, mirroring the modern implementation's direct duration writes.
 */
@Mixin(PotionEffect.class)
public interface MixinPotionEffect {

    @Accessor("duration")
    void setDuration(int duration);

}
