package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * 1.7.10 drops experience in {@code onDeathUpdate} with no equivalent of the
 * modern {@code LivingExperienceDropEvent}, so curio bonuses are applied at
 * the point the base amount is computed.
 */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Shadow
    protected abstract int getExperiencePoints(EntityPlayer player);

    @Redirect(
        method = "onDeathUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;getExperiencePoints(Lnet/minecraft/entity/player/EntityPlayer;)I"))
    private int enigmaticlegacy$applyExperienceBonuses(EntityLivingBase instance, EntityPlayer killer) {
        return SuperpositionHandler.applyExperienceBonuses(instance, killer, this.getExperiencePoints(killer));
    }

}
