package com.aizistral.enigmaticlegacy.crafting;

import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.CosmicHeart;
import com.aizistral.enigmaticlegacy.registries.EnigmaticRecipes;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import com.aizistral.enigmaticlegacy.compat.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.World;

public class BlessedShapedRecipe extends ShapedRecipe {

	public BlessedShapedRecipe(ResourceLocation id, String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result) {
		super(id, group, category, width, height, recipeItems, result);
	}

	public RecipeSerializer<?> getSerializer() {
		return EnigmaticRecipes.BLESSED_SHAPED;
	}

	public boolean matches(CraftingContainer inv, World worldIn) {
		boolean isAllBlessed = true;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.getItem() instanceof CosmicHeart && !ItemNBTHelper.getBoolean(checkedItemStack, "isBelieverBlessed", false)) {
				isAllBlessed = false;
			}
		}

		return isAllBlessed && super.matches(inv, worldIn);
	}

	public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
		return super.assemble(inv, access);
	}

	public static class Serializer implements RecipeSerializer<BlessedShapedRecipe> {

		private NonNullList<Ingredient> handleBlessed(NonNullList<Ingredient> ingredientList) {
			for (Ingredient ing : ingredientList) {
				for (ItemStack stack : ing.getItems()) {
					if (stack.getItem() instanceof CosmicHeart) {
						ItemNBTHelper.setBoolean(stack, "isBelieverBlessed", true);
					}
				}
			}

			return ingredientList;
		}

		public BlessedShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ShapedRecipe recipe = SHAPED_RECIPE.fromJson(recipeId, json);
			return new BlessedShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), this.handleBlessed(recipe.getIngredients()), recipe.getResultItem(null));
		}

		public BlessedShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			ShapedRecipe recipe = SHAPED_RECIPE.fromNetwork(recipeId, buffer);
			return new BlessedShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), this.handleBlessed(recipe.getIngredients()), recipe.getResultItem(null));
		}

		public void toNetwork(FriendlyByteBuf buffer, BlessedShapedRecipe recipe) {
			SHAPED_RECIPE.toNetwork(buffer, recipe);
		}

	}

}