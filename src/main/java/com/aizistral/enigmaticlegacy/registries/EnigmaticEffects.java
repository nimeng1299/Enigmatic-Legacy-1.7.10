package com.aizistral.enigmaticlegacy.registries;

import net.minecraft.potion.Potion;

import com.aizistral.enigmaticlegacy.compat.registries.ObjectHolder;
import com.aizistral.enigmaticlegacy.effects.BlazingStrengthEffect;
import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.effects.GrowingHungerEffect;
import com.aizistral.enigmaticlegacy.effects.MoltenHeartEffect;

public class EnigmaticEffects extends AbstractRegistry<Potion> {

    @ObjectHolder(value = MODID + ":blazing_strength", registryName = "mob_effect")
    public static BlazingStrengthEffect BLAZING_STRENGTH = null;

    @ObjectHolder(value = MODID + ":molten_heart", registryName = "mob_effect")
    public static MoltenHeartEffect MOLTEN_HEART = null;

    @ObjectHolder(value = MODID + ":growing_hunger", registryName = "mob_effect")
    public static GrowingHungerEffect GROWING_HUNGER = null;

    @ObjectHolder(value = MODID + ":growing_bloodlust", registryName = "mob_effect")
    public static GrowingBloodlustEffect GROWING_BLOODLUST = null;

    public static void init() {
        // Forces static initialization.
    }

    private EnigmaticEffects() {
        super("potion");
        this.register("blazing_strength", BlazingStrengthEffect::new);
        this.register("molten_heart", MoltenHeartEffect::new);
        this.register("growing_hunger", GrowingHungerEffect::new);
        this.register("growing_bloodlust", GrowingBloodlustEffect::new);
    }

    private static final EnigmaticEffects INSTANCE = new EnigmaticEffects();

}
