package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.util.DamageSource;

/**
 * Damage type descriptor; 1.7.10 identifies damage sources by string keys.
 */
public class DamageType {

    private final String id;

    public DamageType(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public DamageSource getSource() {
        return CompatDamage.getSource(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DamageType && ((DamageType) obj).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id;
    }

}
