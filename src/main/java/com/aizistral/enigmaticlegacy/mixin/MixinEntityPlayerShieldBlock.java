package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EldritchPan;
import com.aizistral.enigmaticlegacy.items.InfernalShield;

/**
 * Drives the shield-blocking implementation of The Bulwark of Blazing Pride (and of the Eldritch
 * Pan, which shares the same handler). On 1.20 both are real shields: {@code LivingEntity#hurt}
 * asks {@code isDamageSourceBlocked} before armour is applied, and a successful block negates the
 * hit entirely. 1.7.10 has no shield system at all - all {@code isBlocking()} does there is halve
 * incoming damage for right-clicking with a sword - so the queries in
 * {@link SuperpositionHandler#onDamageSourceBlocking} had no caller and nothing ever blocked.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayerShieldBlock {

    /**
     * Early-returns before armour, absorption, potions and knockback are handled, matching the
     * order the modern game resolves a blocked hit in. Cancelling here also keeps
     * {@code ForgeHooks.onLivingHurt} from firing, which is what preserves Blazing Might - on 1.20
     * only damage that actually goes through strips the effect.
     */
    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$blockWithShield(DamageSource source, float amount,
        CallbackInfoReturnable<Boolean> cir) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        ItemStack useItem = player.getItemInUse();

        if (useItem == null) return;

        if (SuperpositionHandler.onDamageSourceBlocking(player, useItem, source, amount)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * The legacy sword-block reduction ({@code (1 + damage) / 2}) must not stack on top of a real
     * shield: hits that slip past it - attacks from behind, in particular - would end up weaker
     * than the unblocked 150% the tooltip promises.
     */
    @Redirect(
        method = "damageEntity",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isBlocking()Z"))
    private boolean enigmaticlegacy$skipLegacySwordBlock(EntityPlayer player) {
        ItemStack useItem = player.getItemInUse();

        if (useItem != null
            && (useItem.getItem() instanceof InfernalShield || useItem.getItem() instanceof EldritchPan)) return false;

        return player.isBlocking();
    }

}
