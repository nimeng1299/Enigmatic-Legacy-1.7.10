package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.curios.CuriosApi;

/**
 * 1.7.10 derives armor purely from equipped items, so the modern-style armor
 * attribute the port exposes to items like Heart of the Golem never affects
 * damage. Fold it into the total armor value consumed by vanilla calculations.
 *
 * <p>
 * Curios normally applies {@code ICurioItem#getAttributeModifiers} to the player
 * while a curio is equipped (this is what gives the Iron Ring its +1 armor on
 * 1.20). Baubles has no such hook, so equipped curio armor bonuses are read
 * directly here. The calculation is stateless and therefore stays correct on
 * both sides and across logins, dimension changes and respawns without any
 * modifier synchronization.
 */
@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayerArmor {

    @Inject(method = "getTotalArmorValue", at = @At("RETURN"), cancellable = true)
    private void enigmaticlegacy$applyArmorAttribute(CallbackInfoReturnable<Integer> cir) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        IAttributeInstance armor = player.getEntityAttribute(Attributes.ARMOR.toVanilla());

        double attributeBonus = armor == null ? 0.0D : armor.getAttributeValue();
        double curioBonus = CuriosApi.getEquippedArmorBonus(player, armor);

        int total = MathHelper.clamp_int(cir.getReturnValue() + (int) Math.round(attributeBonus), 0, 20);

        // 1.7.10 reduces damage with (25 - armor), so 24 is the largest value that
        // cannot turn incoming damage into healing. The 1.20 armor attribute caps at
        // 30; the port keeps the lower ceiling imposed by the vanilla formula.
        // Curio bonuses are added after the vanilla 20 point clamp so that the Iron
        // Ring still provides its +1 armor to a player in full diamond armor.
        total = MathHelper.clamp_int(total + (int) Math.round(curioBonus), 0, 24);

        cir.setReturnValue(total);
    }

}
