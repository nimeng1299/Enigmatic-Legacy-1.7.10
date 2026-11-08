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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.PotionEffects;
import com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnigmaticAmulet.AmuletColor;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * 1.7.10 drops experience in {@code onDeathUpdate} with no equivalent of the
 * modern {@code LivingExperienceDropEvent}, so curio bonuses are applied at
 * the point the base amount is computed.
 */
// The amulet gravity modifier deliberately does NOT use @ModifyConstant on the 0.08D literal:
// that injector is exclusive per instruction node, and Galacticraft modifies the same literal,
// so one of the two mods would be silently dropped and its require check would crash the game.
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
     * Swim speed: {@code +100%} from the Non-Euclidean Cube, {@code +25%} from a BLUE Enigmatic
     * Amulet (the Ascension Amulet counts as every color).
     * <p>
     * 1.20 expresses these as {@code ForgeMod.SWIM_SPEED} modifiers, but 1.7.10 has no such
     * attribute - water movement is hardcoded to {@code moveFlying(strafe, forward, 0.02F)} in this
     * very method. Scaling that acceleration scales the terminal swimming speed (drag stays at
     * 0.8). MULTIPLY_TOTAL modifiers add up on 1.20, so the sources are summed rather than
     * multiplied: cube + blue amulet gives {@code 0.02F * 2.25F}. The redirect only touches the
     * first {@code moveFlying} call, i.e. the water branch; lava and walking keep their original
     * values.
     */
    @Redirect(
        method = "moveEntityWithHeading",
        at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/entity/EntityLivingBase;moveFlying(FFF)V"))
    private void enigmaticlegacy$cubeSwimSpeed(EntityLivingBase self, float strafe, float forward, float friction) {
        if (self instanceof EntityPlayer player && !player.capabilities.isFlying) {
            float factor = 1.0F;

            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.THE_CUBE)) {
                factor += 1.0F;
            }

            if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.BLUE)) {
                factor += 0.25F;
            }

            friction *= factor;
        }

        self.moveFlying(strafe, forward, friction);
    }

    /**
     * MAGENTA Enigmatic Amulet: {@code -25%} gravity (the Ascension Amulet counts as every color).
     * <p>
     * 1.20 applies this as a {@code ForgeMod.ENTITY_GRAVITY} MULTIPLY_TOTAL modifier. The obvious
     * 1.7.10 port is a {@code @ModifyConstant} on the hardcoded {@code 0.08D} fall acceleration in
     * {@code moveEntityWithHeading}, but that injector is redirect-family: it claims the constant
     * instruction node exclusively, so any other mod touching the same literal gets skipped. That is
     * not academic - Galacticraft does exactly this, its injection check then fails and
     * {@code EntityLivingBase} cannot load at all (the whole game dies on startup).
     * <p>
     * Callbacks do chain with everything, so the same physics is expressed here as a pre-gravity
     * impulse: {@code 0.08D} is subtracted later in the very same tick, so adding
     * {@code 0.08D * 0.25D} up front leaves the tick with {@code motionY - 0.06D} and identical
     * downstream integration. Water and lava branches never apply fall gravity, so they are skipped;
     * a flying player never reaches the branch either.
     */
    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"))
    private void enigmaticlegacy$amuletGravity(float strafe, float forward, CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;

        if (!(self instanceof EntityPlayer player) || player.capabilities.isFlying) return;
        if (player.isInWater() || player.handleLavaMovement()) return;

        if (EnigmaticItems.ENIGMATIC_AMULET.hasColor(player, AmuletColor.MAGENTA)) {
            player.motionY += 0.08D * 0.25D;
        }
    }

}
