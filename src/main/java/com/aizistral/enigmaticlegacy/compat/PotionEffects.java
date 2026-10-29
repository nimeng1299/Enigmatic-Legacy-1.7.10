package com.aizistral.enigmaticlegacy.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/**
 * Helpers replacing the modern PotionUtils / MobEffectInstance APIs.
 */
public final class PotionEffects {

    private PotionEffects() {
        // NO-OP
    }

    public static Potion getPotion(PotionEffect effect) {
        return effect == null ? null : Potion.potionTypes[effect.getPotionID()];
    }

    public static boolean isBeneficial(PotionEffect effect) {
        Potion potion = getPotion(effect);
        return potion != null && !potion.isBadEffect();
    }

    public static boolean isInstant(PotionEffect effect) {
        Potion potion = getPotion(effect);
        return potion != null && potion.isInstant();
    }

    public static boolean isBeneficial(Potion potion) {
        return potion != null && !potion.isBadEffect();
    }

    public static List<PotionEffect> copyList(Collection<PotionEffect> effects) {
        List<PotionEffect> result = new ArrayList<>();

        for (PotionEffect effect : effects) {
            result.add(new PotionEffect(effect));
        }

        return result;
    }

    public static ResourceLocation getKey(Potion potion) {
        if (potion == null) return null;

        String name = potion.getName();

        if (name.startsWith("potion.")) {
            name = name.substring("potion.".length());
        }

        return new ResourceLocation("minecraft", name);
    }

    public static ItemStack setPotion(ItemStack stack, Potion potion) {
        if (stack != null && potion != null) {
            stack.setItemDamage(potion.id);
        }
        return stack;
    }

    public static Potion getPotion(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof net.minecraft.item.ItemPotion)) return null;

        List<PotionEffect> effects = net.minecraft.potion.PotionHelper.getPotionEffects(stack.getItemDamage(), false);

        if (effects != null && !effects.isEmpty()) return Potion.potionTypes[effects.get(0)
            .getPotionID()];

        return null;
    }

}
