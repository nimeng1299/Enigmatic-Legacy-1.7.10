package com.aizistral.enigmaticlegacy.brewing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import com.aizistral.enigmaticlegacy.compat.brewing.IBrewingRecipe;

public abstract class AbstractBrewingRecipe implements IBrewingRecipe {

    public static HashMap<ResourceLocation, List<AbstractBrewingRecipe>> recipeMap = new HashMap<ResourceLocation, List<AbstractBrewingRecipe>>();
    public static final AbstractBrewingRecipe EMPTY_RECIPE = new SpecialBrewingRecipe(
        Ingredient.of((net.minecraft.item.Item) null),
        Ingredient.of((net.minecraft.item.Item) null),
        null,
        new ResourceLocation(EnigmaticLegacy.MODID, "empty_recipe"));

    public AbstractBrewingRecipe(ResourceLocation registryName) {
        if (AbstractBrewingRecipe.recipeMap.containsKey(registryName)) {
            List<AbstractBrewingRecipe> list = AbstractBrewingRecipe.recipeMap.get(registryName);
            list.add(this);
            AbstractBrewingRecipe.recipeMap.put(registryName, list);
        } else {
            List<AbstractBrewingRecipe> list = new ArrayList<AbstractBrewingRecipe>();
            list.add(this);
            AbstractBrewingRecipe.recipeMap.put(registryName, list);
        }
    }

}
