package com.aizistral.enigmaticlegacy.mixin;

import java.util.Map;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

        double curioBonus = CuriosApi.getEquippedArmorBonus(player, armor);
        double armorBonus = Math
            .max(enigmaticlegacy$heldArmorBonus(player), armor == null ? 0.0D : armor.getAttributeValue());

        int total = MathHelper.clamp_int(cir.getReturnValue() + (int) Math.round(armorBonus), 0, 20);

        // 1.7.10 reduces damage with (25 - armor), so 24 is the largest value that
        // cannot turn incoming damage into healing. The 1.20 armor attribute caps at
        // 30; the port keeps the lower ceiling imposed by the vanilla formula.
        // Curio bonuses are added after the vanilla 20 point clamp so that the Iron
        // Ring still provides its +1 armor to a player in full diamond armor.
        total = MathHelper.clamp_int(total + (int) Math.round(curioBonus), 0, 24);

        cir.setReturnValue(total);
    }

    /**
     * Armor granted by the held item, read straight off its modifier map.
     * <p>
     * 1.7.10 only ever resolves held-item modifiers into the attribute map on the server
     * ({@code EntityLivingBase#onUpdate} guards that block with {@code !worldObj.isRemote}), and
     * it silently drops any key whose attribute is not registered on the player - {@code
     * generic.armor} never is. Reading the stack directly is therefore both the only way the
     * client-side armor bar sees the bonus and the only way it works at all. The 1.20 items that
     * use it are The Voracious Pan (base armor plus a permanent gain per unique kill) and, in a
     * lesser capacity, anything else that declares {@code Attributes.ARMOR} on a held stack.
     */
    private static double enigmaticlegacy$heldArmorBonus(EntityPlayer player) {
        ItemStack held = player.getHeldItem();

        if (held == null) return 0.0D;

        String armorAttribute = Attributes.ARMOR.toVanilla()
            .getAttributeUnlocalizedName();
        double bonus = 0.0D;

        for (Map.Entry<String, AttributeModifier> entry : held.getAttributeModifiers()
            .entries()) {
            AttributeModifier modifier = entry.getValue();

            if (modifier == null || modifier.getOperation() != 0) continue;
            if (!armorAttribute.equals(entry.getKey())) continue;

            bonus += modifier.getAmount();
        }

        return bonus;
    }

}
