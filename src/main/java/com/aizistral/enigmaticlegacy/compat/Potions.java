package com.aizistral.enigmaticlegacy.compat;

import java.util.List;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.aizistral.enigmaticlegacy.registries.EnigmaticEffects;

/**
 * Modern Potions constants mapped onto 1.7.10 potion objects. AWKWARD and
 * MUNDANE have no 1.7.10 counterpart, so they are represented as raw item
 * damage values instead of fake potion types (which would steal ids from real
 * mod effects).
 */
public final class Potions {

    public static final int AWKWARD = 16;
    public static final int MUNDANE = 64;

    public static final Potion LONG_FIRE_RESISTANCE = Potion.fireResistance;
    public static final Potion LONG_INVISIBILITY = Potion.invisibility;
    public static final Potion LONG_LEAPING = Potion.jump;
    public static final Potion LONG_NIGHT_VISION = Potion.nightVision;
    public static final Potion LONG_POISON = Potion.poison;
    public static final Potion LONG_REGENERATION = Potion.regeneration;
    public static final Potion LONG_SLOWNESS = Potion.moveSlowdown;
    public static final Potion LONG_SLOW_FALLING = EnigmaticEffects.SLOW_FALLING;
    public static final Potion LONG_STRENGTH = Potion.damageBoost;
    public static final Potion LONG_SWIFTNESS = Potion.moveSpeed;
    public static final Potion LONG_TURTLE_MASTER = Potion.resistance;
    public static final Potion LONG_WATER_BREATHING = Potion.waterBreathing;
    public static final Potion LONG_WEAKNESS = Potion.weakness;

    public static final Potion STRONG_HARMING = Potion.harm;
    public static final Potion STRONG_HEALING = Potion.heal;
    public static final Potion STRONG_LEAPING = Potion.jump;
    public static final Potion STRONG_POISON = Potion.poison;
    public static final Potion STRONG_REGENERATION = Potion.regeneration;
    public static final Potion STRONG_SLOWNESS = Potion.moveSlowdown;
    public static final Potion STRONG_STRENGTH = Potion.damageBoost;
    public static final Potion STRONG_SWIFTNESS = Potion.moveSpeed;
    public static final Potion STRONG_TURTLE_MASTER = Potion.resistance;

    private Potions() {
        // NO-OP
    }

    /**
     * Builds the 1.7.10 item damage value for a potion. 1.7.10 encodes potion
     * item variants as a bit pattern (bit 13 for drinkable, bit 14 for splash,
     * bit 6 for extended duration, bit 5 for level II) rather than as a potion
     * id, so the low bits have to be resolved from the vanilla requirement
     * table. Returns -1 for potions that no 1.7.10 item can represent.
     */
    public static int damageFor(Potion potion, int extraBits) {
        int index = indexOf(potion);
        return index < 0 ? -1 : index | 8192 | extraBits;
    }

    public static int indexOf(Potion potion) {
        if (potion == null) return -1;

        for (int i = 1; i <= 15; i++) {
            List<PotionEffect> effects = net.minecraft.potion.PotionHelper.getPotionEffects(i | 8192, false);

            if (effects != null && effects.size() == 1
                && effects.get(0)
                    .getPotionID() == potion.getId())
                return i;
        }

        return -1;
    }

}
