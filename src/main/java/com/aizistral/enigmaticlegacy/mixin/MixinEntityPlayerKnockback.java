package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.CosmicScroll;

/**
 * 1.7.10 applies melee knockback as a direct velocity change with no equivalent
 * of the modern knockback event, so the Architect's Favor bonus is applied at
 * the velocity call itself.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayerKnockback {

    @Redirect(
        method = "attackTargetEntityWithCurrentItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void enigmaticlegacy$applyUnchosenKnockback(Entity target, double x, double y, double z) {
        EntityPlayer player = (EntityPlayer) (Object) this;

        if (target instanceof EntityPlayer victim && !SuperpositionHandler.isTheBlessedOne(victim)
            && SuperpositionHandler.hasArchitectsFavor(player)) {
            double multiplier = CosmicScroll.unchosenKnockbackBonus.getValue()
                .asModifier(true);
            x *= multiplier;
            z *= multiplier;
        }

        target.addVelocity(x, y, z);
    }

}
