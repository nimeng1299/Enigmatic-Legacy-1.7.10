package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.util.ResourceLocation;

public class ResourceKey<T> {

    private final ResourceLocation location;
    private final String registryName;

    protected ResourceKey(String registryName, ResourceLocation location) {
        this.registryName = registryName;
        this.location = location;
    }

    public static <T> ResourceKey<T> create(ResourceKey<?> registry, ResourceLocation location) {
        return new ResourceKey<>(registry == null ? "" : registry.registryName, location);
    }

    public static <T> ResourceKey<T> createRegistryKey(String registryName) {
        return new ResourceKey<>(registryName, new ResourceLocation(registryName));
    }

    public ResourceLocation location() {
        return this.location;
    }

    public String registryName() {
        return this.registryName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourceKey && ((ResourceKey<?>) obj).location.equals(this.location)
            && ((ResourceKey<?>) obj).registryName.equals(this.registryName);
    }

    @Override
    public int hashCode() {
        return this.location.hashCode() * 31 + this.registryName.hashCode();
    }

    @Override
    public String toString() {
        return this.registryName + "/" + this.location;
    }

}
