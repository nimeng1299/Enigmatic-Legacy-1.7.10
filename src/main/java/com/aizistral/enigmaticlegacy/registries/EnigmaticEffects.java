package com.aizistral.enigmaticlegacy.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.potion.Potion;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;
import com.aizistral.enigmaticlegacy.effects.BlazingStrengthEffect;
import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.effects.GrowingHungerEffect;
import com.aizistral.enigmaticlegacy.effects.MoltenHeartEffect;
import com.aizistral.enigmaticlegacy.effects.SlowFallingEffect;

public class EnigmaticEffects extends AbstractRegistry<Potion> {

    @ObjectHolder(value = MODID + ":blazing_strength", registryName = "mob_effect")
    public static BlazingStrengthEffect BLAZING_STRENGTH = null;

    @ObjectHolder(value = MODID + ":molten_heart", registryName = "mob_effect")
    public static MoltenHeartEffect MOLTEN_HEART = null;

    @ObjectHolder(value = MODID + ":growing_hunger", registryName = "mob_effect")
    public static GrowingHungerEffect GROWING_HUNGER = null;

    @ObjectHolder(value = MODID + ":growing_bloodlust", registryName = "mob_effect")
    public static GrowingBloodlustEffect GROWING_BLOODLUST = null;

    @ObjectHolder(value = MODID + ":slow_falling", registryName = "mob_effect")
    public static SlowFallingEffect SLOW_FALLING = null;

    public static void init() {
        // Forces static initialization.
    }

    /** Every effect we created, in registration order. Used by {@link #validate()}. */
    private final List<Potion> effects = new ArrayList<>();

    private EnigmaticEffects() {
        super("potion");
        this.register("blazing_strength", BlazingStrengthEffect::new);
        this.register("molten_heart", MoltenHeartEffect::new);
        this.register("growing_hunger", GrowingHungerEffect::new);
        this.register("growing_bloodlust", GrowingBloodlustEffect::new);
        this.register("slow_falling", SlowFallingEffect::new);
    }

    @Override
    protected Potion register(String name, Supplier<Potion> supplier) {
        Potion effect = super.register(name, supplier);
        this.effects.add(effect);
        return effect;
    }

    /**
     * Verifies that each of our effects still owns the slot it was allocated.
     * <p>
     * 1.7.10 potions register themselves from their constructor straight into the static
     * {@code Potion.potionTypes} array, so any mod that constructs a potion later silently
     * overwrites whatever occupied that id. That is exactly how Et Futurum's levitation used to
     * eat our Growing Bloodlust: applying id 27 then resolved to levitation, which is why holding
     * The Voracious Pan threw the player into the sky. The load order is pinned through our
     * {@code after:etfuturum} dependency, and this check makes any remaining third-party
     * collision loud instead of turning it into mysterious behaviour in-game.
     */
    public static void validate() {
        boolean clean = true;
        StringBuilder ids = new StringBuilder();

        for (Potion effect : INSTANCE.effects) {
            if (ids.length() > 0) {
                ids.append(", ");
            }
            ids.append(effectName(effect))
                .append('=')
                .append(effect.id);

            Potion occupant = effect.id >= 0 && effect.id < Potion.potionTypes.length ? Potion.potionTypes[effect.id]
                : null;

            if (occupant == effect) {
                continue;
            }

            clean = false;
            EnigmaticLegacy.LOGGER.error(
                "Effect id {} ({}) was taken over by {} - effects of this type will not work, "
                    + "and anything applying it will apply the intruder instead",
                effect.id,
                effectName(effect),
                occupant == null ? "nothing (slot cleared)"
                    : occupant.getClass()
                        .getName());
        }

        EnigmaticLegacy.LOGGER.info("Registered effects: {}", ids);

        if (!clean) {
            EnigmaticLegacy.LOGGER.error(
                "Potion id collision detected. Change the conflicting mod's ids, or move our "
                    + "registration later by adding an 'after:' dependency on it.");
        }
    }

    private static String effectName(Potion effect) {
        if (effect == BLAZING_STRENGTH) return "blazing_strength";
        if (effect == MOLTEN_HEART) return "molten_heart";
        if (effect == GROWING_HUNGER) return "growing_hunger";
        if (effect == GROWING_BLOODLUST) return "growing_bloodlust";
        if (effect == SLOW_FALLING) return "slow_falling";
        return effect.getClass()
            .getSimpleName();
    }

    private static final EnigmaticEffects INSTANCE = new EnigmaticEffects();

}
