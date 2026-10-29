package com.aizistral.enigmaticlegacy.compat.registries;

/**
 * Placeholder for the modern registration event; 1.7.10 registration happens
 * eagerly through the registry adapters.
 */
public class RegisterEvent {

    public <T> void register(Object registryKey, Object name, java.util.function.Supplier<T> supplier) {
        // NO-OP
    }
}
