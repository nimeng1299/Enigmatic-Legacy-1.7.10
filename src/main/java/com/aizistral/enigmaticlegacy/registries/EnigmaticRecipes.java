package com.aizistral.enigmaticlegacy.registries;

/**
 * 1.7.10 has no data-driven recipe serializers; recipes are registered directly
 * through the crafting manager by {@link EnigmaticRecipeRegistrar} instead.
 */
public class EnigmaticRecipes extends AbstractRegistry<Object> {

    private static final EnigmaticRecipes INSTANCE = new EnigmaticRecipes();

    private EnigmaticRecipes() {
        super("recipes");
    }

}
