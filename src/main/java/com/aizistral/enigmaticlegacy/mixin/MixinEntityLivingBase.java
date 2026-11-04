package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

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

    /**
     * 1.7.10 has no potion-applicable event, so the Void Pearl's status effect
     * immunity is enforced before the effect is ever added. This also covers
     * instant potions, which can no longer be applied in the first place.
     */
    @Inject(method = "isPotionApplicable", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$voidPearlPotionImmunity(PotionEffect effect, CallbackInfoReturnable<Boolean> cir) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;

        if (self instanceof EntityPlayer && !EnigmaticEventHandler.isApplyingNightVision
            && SuperpositionHandler.hasCurio(self, EnigmaticItems.VOID_PEARL)) {
            cir.setReturnValue(false);
        }
    }

}
