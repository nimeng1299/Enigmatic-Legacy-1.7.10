package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.PotionEffects;
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
     * 1.7.10 has no potion-applicable event, so curio-based status effect immunity is enforced
     * before the effect is ever added. This also covers instant potions, which can no longer be
     * applied in the first place.
     * <p>
     * Matches 1.20's {@code MobEffectEvent.Applicable} handler: the Void Pearl rejects every
     * effect, while the Enigmatic Item and the Non-Euclidean Cube only reject harmful ones.
     */
    @Inject(method = "isPotionApplicable", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$curioPotionImmunity(PotionEffect effect, CallbackInfoReturnable<Boolean> cir) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;

        if (!(self instanceof EntityPlayer) || EnigmaticEventHandler.isApplyingNightVision || effect == null) return;

        int id = effect.getPotionID();
        Potion potion = id >= 0 && id < Potion.potionTypes.length ? Potion.potionTypes[id] : null;

        if (potion == null) return;

        // Mana and Artifice's chrono-exhaustion is deliberately allowed through in 1.20.
        if (new ResourceLocation("mana-and-artifice", "chrono-exhaustion")
            .equals(ForgeRegistries.MOB_EFFECTS.getKey(potion))) return;

        if (SuperpositionHandler.hasCurio(self, EnigmaticItems.VOID_PEARL)) {
            cir.setReturnValue(false);
        } else if ((SuperpositionHandler.hasCurio(self, EnigmaticItems.ENIGMATIC_ITEM)
            || SuperpositionHandler.hasCurio(self, EnigmaticItems.THE_CUBE)) && !PotionEffects.isBeneficial(potion)) {
                cir.setReturnValue(false);
            }
    }

    /**
     * Non-Euclidean Cube: {@code +100%} swim speed.
     * <p>
     * 1.20 expresses this as a {@code ForgeMod.SWIM_SPEED} modifier, but 1.7.10 has no such
     * attribute - water movement is hardcoded to {@code moveFlying(strafe, forward, 0.02F)} in this
     * very method. Doubling that acceleration double the terminal swimming speed (drag stays at
     * 0.8), which is exactly what a {@code +100%} multiplier does on 1.20. The redirect only
     * touches the first {@code moveFlying} call, i.e. the water branch; lava and walking keep
     * their original values.
     */
    @Redirect(
        method = "moveEntityWithHeading",
        at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/EntityLivingBase;moveFlying(FFF)V"))
    private void enigmaticlegacy$cubeSwimSpeed(EntityLivingBase self, float strafe, float forward, float friction) {
        if (self instanceof EntityPlayer player && !player.capabilities.isFlying
            && SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
            friction *= 2.0F;
        }

        self.moveFlying(strafe, forward, friction);
    }

}
