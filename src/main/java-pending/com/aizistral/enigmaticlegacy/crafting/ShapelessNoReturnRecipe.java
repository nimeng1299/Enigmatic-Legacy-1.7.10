package com.aizistral.enigmaticlegacy.crafting;

import com.aizistral.enigmaticlegacy.registries.EnigmaticRecipes;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * A copy of regular Shapeless Recipe, but any container items
 * are destroyed instead of remaining in a crafting grid.
 * @author Integral
 */

public class ShapelessNoReturnRecipe extends ShapelessRecipe {

	private final String group;
	private final ItemStack recipeOutput;
	private final NonNullList<Ingredient> recipeItems;

	public ShapelessNoReturnRecipe(ResourceLocation id, String group, CraftingBookCategory category, ItemStack output, NonNullList<Ingredient> inputs) {
		super(id, group, category, output, inputs);

		this.group = group;
		this.recipeOutput = output;
		this.recipeItems = inputs;
	}

	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), null);
		return nonnulllist;
	}

	public RecipeSerializer<?> getSerializer() {
		return EnigmaticRecipes.SHAPELESS_NO_RETURN;
	}

	public static class Serialize implements RecipeSerializer<ShapelessNoReturnRecipe> {
		public ShapelessNoReturnRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ShapelessRecipe recipe = SHAPELESS_RECIPE.fromJson(recipeId, json);
			return new ShapelessNoReturnRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
		}

		public ShapelessNoReturnRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ShapelessRecipe recipe = SHAPELESS_RECIPE.fromNetwork(recipeId, buffer);
			return new ShapelessNoReturnRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
		}

		public void toNetwork(FriendlyByteBuf buffer, ShapelessNoReturnRecipe recipe) {
			SHAPELESS_RECIPE.toNetwork(buffer, recipe);
		}

	}
}
