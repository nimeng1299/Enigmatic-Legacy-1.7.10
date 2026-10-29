package com.aizistral.enigmaticlegacy.compat.registries;

public class RegistryObject<T> {

    private final T value;

    private RegistryObject(T value) {
        this.value = value;
    }

    public static <T> RegistryObject<T> of(T value) {
        return new RegistryObject<>(value);
    }

    public T get() {
        return this.value;
    }
}
