package com.aizistral.enigmaticlegacy.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

import com.aizistral.enigmaticlegacy.compat.PotionIds;

/**
 * 1.7.10 has no slow falling effect, so the port provides its own: it caps
 * downward velocity and keeps fall distance at zero while active.
 */
public class SlowFallingEffect extends Potion {

    public SlowFallingEffect() {
        super(PotionIds.next(), false, 0x98D8FF);
        this.setPotionName("effect.slow_falling");
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        if (living.motionY < -0.05D) {
            living.motionY = -0.05D;
        }

        living.fallDistance = 0.0F;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

}
