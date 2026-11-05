package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.CosmicScroll;
import com.aizistral.enigmaticlegacy.items.EnderSlayer;
import com.aizistral.enigmaticlegacy.items.TheInfinitum;
import com.aizistral.enigmaticlegacy.items.TheTwist;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * 1.7.10 has no equivalent of the modern knockback event, so the knockback bonuses
 * of The Twist, The Infinitum, The Ender Slayer and The Architect's Favor are applied
 * at the two places where a player melee hit actually moves the victim:
 *
 * <ul>
 * <li>{@code Entity#addVelocity} inside {@code attackTargetEntityWithCurrentItem} -
 * the extra push granted by the Knockback enchantment and by sprint-hits. It is
 * scaled here, mirroring how the modern event multiplies the final strength.</li>
 * <li>The fixed 0.4 knockback that {@code EntityLivingBase#attackEntityFrom} applies
 * through {@code knockBack}. That value is a hardcoded constant in 1.7.10, so instead
 * of scaling it we append the remaining velocity right after the hit resolves.</li>
 * </ul>
 *
 * On 1.20 both paths are a single strength value handed to the knockback event, which
 * is why the multiplier is computed the same way for both of them here.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayerKnockback {

    /**
     * Knockback strength the modern game uses as a baseline, i.e. the value that
     * {@code LivingKnockBackEvent#setStrength} would be scaling on 1.20.
     */
    private static final double BASE_KNOCKBACK = 0.4D;

    @Redirect(
        method = "attackTargetEntityWithCurrentItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    private void enigmaticlegacy$applyKnockbackBonus(Entity target, double x, double y, double z) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        double multiplier = this.enigmaticlegacy$knockbackMultiplier(player, target);

        if (multiplier > 1.0D) {
            x *= multiplier;
            z *= multiplier;
        }

        target.addVelocity(x, y, z);
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("TAIL"))
    private void enigmaticlegacy$applyBaseKnockbackBonus(Entity targetEntity, CallbackInfo info) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        double multiplier = this.enigmaticlegacy$knockbackMultiplier(player, targetEntity);

        if (multiplier <= 1.0D) return;
        if (!(targetEntity instanceof EntityLivingBase)) return;

        EntityLivingBase target = (EntityLivingBase) targetEntity;

        // Only hits that actually went through move the victim; a canceled or refused
        // attack (for example The Twist in the hands of a non-cursed player) must not.
        if (target.isDead || target.hurtTime <= 0) return;

        double resistance = target.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
            .getAttributeValue();

        if (resistance > 0.0D && target.worldObj.rand.nextDouble() < resistance) return;

        double dx = target.posX - player.posX;
        double dz = target.posZ - player.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 1.0E-4D) {
            // Degenerate case: victim is right on top of the attacker. Push along the
            // attacker's facing, the same fallback vanilla knockback code uses.
            dx = -Math.sin(player.rotationYaw * Math.PI / 180.0D);
            dz = Math.cos(player.rotationYaw * Math.PI / 180.0D);
            distance = 1.0D;
        }

        double extra = BASE_KNOCKBACK * (multiplier - 1.0D);
        target.addVelocity(dx / distance * extra, 0.0D, dz / distance * extra);
    }

    /**
     * Mirrors the knockback part of {@code EnigmaticEventHandler#onLivingKnockback} on
     * 1.20, minus the Phantom multiplier (1.7.10 has no Phantoms).
     */
    private double enigmaticlegacy$knockbackMultiplier(EntityPlayer player, Entity target) {
        double power = 1.0D;
        ItemStack mainhand = player.getHeldItem();

        if (mainhand != null) {
            if (mainhand.getItem() == EnigmaticItems.THE_TWIST) {
                if (SuperpositionHandler.isTheCursedOne(player)) {
                    power += TheTwist.knockbackBonus.getValue()
                        .asModifier(false);
                }
            } else if (mainhand.getItem() == EnigmaticItems.THE_INFINITUM) {
                if (SuperpositionHandler.isTheWorthyOne(player)) {
                    power += TheInfinitum.knockbackBonus.getValue()
                        .asModifier(false);
                }
            } else if (mainhand.getItem() == EnigmaticItems.ENDER_SLAYER && target instanceof EntityLivingBase) {
                if (SuperpositionHandler.isTheCursedOne(player)
                    && EnigmaticItems.ENDER_SLAYER.isEndDweller((EntityLivingBase) target)) {
                    power += EnderSlayer.endKnockbackBonus.getValue()
                        .asModifier(false);
                }
            }
        }

        if (target instanceof EntityPlayer && !SuperpositionHandler.isTheBlessedOne((EntityPlayer) target)
            && SuperpositionHandler.hasArchitectsFavor(player)) {
            power += CosmicScroll.unchosenKnockbackBonus.getValue()
                .asModifier(false);
        }

        return power;
    }

}
