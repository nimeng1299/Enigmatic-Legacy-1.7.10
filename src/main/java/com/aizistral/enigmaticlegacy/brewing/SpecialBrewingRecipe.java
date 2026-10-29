package com.aizistral.enigmaticlegacy.brewing;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import com.aizistral.enigmaticlegacy.compat.PotionEffects;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;

/**
 * Variation of brewing recipe sensitive to NBT of input stack.
 * 
 * @author Integral
 */

public class SpecialBrewingRecipe extends AbstractBrewingRecipe {

    @Nonnull
    private final Ingredient input;
    @Nonnull
    private final Ingredient ingredient;
    @Nonnull
    private final ItemStack output;

    public SpecialBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
        this(
            input,
            ingredient,
            output,
            new ResourceLocation(EnigmaticLegacy.MODID, ItemNBTHelper.getString(output, "EnigmaticPotion", "unknown")));
    }

    public SpecialBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output, ResourceLocation name) {
        super(name);
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }

    public boolean isInput(@Nonnull ItemStack stack) {

        if (stack != null) {
            for (ItemStack testStack : this.getInput()
                .getItems()) {
                if (testStack.getItem() == stack.getItem()
                    && PotionEffects.getPotion(testStack) == PotionEffects.getPotion(stack)) return true;
            }
        }

        return false;
    }

    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return this.isInput(input) && this.isIngredient(ingredient) ? this.getOutput()
            .copy() : null;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public boolean isIngredient(ItemStack ingredient) {
        if (ingredient != null) return this.ingredient.test(ingredient);
        else return false;
    }

}
