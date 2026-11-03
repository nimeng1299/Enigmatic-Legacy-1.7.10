package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.Attributes;

/**
 * 1.7.10 derives armor purely from equipped items, so the modern-style armor
 * attribute the port exposes to items like Heart of the Golem never affects
 * damage. Fold it into the total armor value consumed by vanilla calculations.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayerArmor {

    @Inject(method = "getTotalArmorValue", at = @At("RETURN"), cancellable = true)
    private void enigmaticlegacy$applyArmorAttribute(CallbackInfoReturnable<Integer> cir) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        IAttributeInstance armor = player.getEntityAttribute(Attributes.ARMOR.toVanilla());

        if (armor == null) return;

        int total = cir.getReturnValue() + (int) Math.round(armor.getAttributeValue());
        cir.setReturnValue(MathHelper.clamp_int(total, 0, 20));
    }

}
