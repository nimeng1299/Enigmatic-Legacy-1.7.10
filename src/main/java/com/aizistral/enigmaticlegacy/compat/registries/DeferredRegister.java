package com.aizistral.enigmaticlegacy.compat.registries;

import java.util.function.Supplier;

/**
 * Minimal stand-in for the modern deferred registration helper.
 */
public class DeferredRegister<T> {

    private final String modid;

    private DeferredRegister(String modid) {
        this.modid = modid;
    }

    public static <T> DeferredRegister<T> create(Object registry, String modid) {
        return new DeferredRegister<>(modid);
    }

    public RegistryObject<T> register(String name, Supplier<T> supplier) {
        return RegistryObject.of(supplier.get());
    }

    public void register(Object eventBus) {
        // NO-OP
    }
}
