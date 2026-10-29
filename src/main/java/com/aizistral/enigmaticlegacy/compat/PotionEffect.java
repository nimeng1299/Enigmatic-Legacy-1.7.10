package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.potion.Potion;

/**
 * Thin adapter mirroring the modern MobEffectInstance surface on top of the
 * 1.7.10 PotionEffect.
 */
public class PotionEffect extends net.minecraft.potion.PotionEffect {

    public PotionEffect(Potion effect, int duration) {
        this(effect.id, duration, 0);
    }

    public PotionEffect(int id, int duration, int amplifier) {
        super(id, duration, amplifier);
    }

    public PotionEffect(int id, int duration, int amplifier, boolean ambient) {
        super(id, duration, amplifier, ambient);
    }

    public PotionEffect(int id, int duration, int amplifier, boolean ambient, boolean visible) {
        super(id, duration, amplifier, ambient);
    }

    public PotionEffect(Potion effect, int duration, int amplifier) {
        this(effect.id, duration, amplifier);
    }

    public PotionEffect(Potion effect, int duration, int amplifier, boolean ambient) {
        this(effect.id, duration, amplifier, ambient);
    }

    public PotionEffect(Potion effect, int duration, int amplifier, boolean ambient, boolean visible) {
        this(effect.id, duration, amplifier, ambient);
    }

    public PotionEffect(net.minecraft.potion.PotionEffect other) {
        this(other.getPotionID(), other.getDuration(), other.getAmplifier(), other.getIsAmbient());
    }

    public PotionEffect(PotionEffect other) {
        this(other.getPotionID(), other.getDuration(), other.getAmplifier(), other.getIsAmbient());
    }

    public Potion getEffect() {
        return Potion.potionTypes[this.getPotionID()];
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isAmbient() {
        return this.getIsAmbient();
    }

    public boolean isInfiniteDuration() {
        return this.getDuration() == Integer.MAX_VALUE;
    }

    public PotionEffect withDuration(int duration) {
        return new PotionEffect(this.getPotionID(), duration, this.getAmplifier(), this.getIsAmbient());
    }

    public PotionEffect withAmplifier(int amplifier) {
        return new PotionEffect(this.getPotionID(), this.getDuration(), amplifier, this.getIsAmbient());
    }

}
