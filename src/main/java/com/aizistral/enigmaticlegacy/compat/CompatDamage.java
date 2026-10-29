package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.util.DamageSource;

public final class CompatDamage {

    private CompatDamage() {
        // NO-OP
    }

    public static boolean is(DamageSource source, DamageType type) {
        return source != null && type != null
            && type.id()
                .equals(source.getDamageType());
    }

    public static DamageSource getSource(DamageType type) {
        if (type == null) return DamageSource.generic;

        switch (type.id()) {
            case "inFire":
                return DamageSource.inFire;
            case "onFire":
                return DamageSource.onFire;
            case "lava":
                return DamageSource.lava;
            case "inWall":
                return DamageSource.inWall;
            case "drown":
                return DamageSource.drown;
            case "starve":
                return DamageSource.starve;
            case "cactus":
                return DamageSource.cactus;
            case "fall":
                return DamageSource.fall;
            case "outOfWorld":
                return DamageSource.outOfWorld;
            case "generic":
                return DamageSource.generic;
            case "magic":
                return DamageSource.magic;
            case "wither":
                return DamageSource.wither;
            case "anvil":
                return DamageSource.anvil;
            case "fallingBlock":
                return DamageSource.fallingBlock;
            default:
                return new DamageSource(type.id());
        }
    }

}
