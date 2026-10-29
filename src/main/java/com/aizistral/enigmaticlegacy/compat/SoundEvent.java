package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.util.ResourceLocation;

/**
 * Sound event stand-in. 1.7.10 addresses sounds by string names.
 */
public class SoundEvent {

    private final ResourceLocation location;

    public SoundEvent(ResourceLocation location) {
        this.location = location;
    }

    public static SoundEvent create(ResourceLocation location) {
        return new SoundEvent(location);
    }

    public static SoundEvent create(String name) {
        return new SoundEvent(new ResourceLocation(name));
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public String getName() {
        return this.location.toString();
    }

    @Override
    public String toString() {
        return this.location.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SoundEvent && ((SoundEvent) obj).location.equals(this.location);
    }

    @Override
    public int hashCode() {
        return this.location.hashCode();
    }

}
