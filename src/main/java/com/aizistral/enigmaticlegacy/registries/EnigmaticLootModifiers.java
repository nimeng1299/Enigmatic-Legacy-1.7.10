package com.aizistral.enigmaticlegacy.registries;

/**
 * Global loot modifiers do not exist on 1.7.10; loot is injected through
 * {@code ChestGenHooks} instead.
 */
public class EnigmaticLootModifiers {

    private static final EnigmaticLootModifiers INSTANCE = new EnigmaticLootModifiers();

    private EnigmaticLootModifiers() {
        // NO-OP
    }

}
