package com.aizistral.enigmaticlegacy.registries;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.DamageType;

// Used there to be simpler times...
public class EnigmaticDamageTypes {

    public static final DamageType DARKNESS = register("darkness");
    public static final DamageType NEMESIS_CURSE = register("nemesis_curse");

    private static DamageType register(String name) {
        return new DamageType(EnigmaticLegacy.MODID + ":" + name);
    }
}
