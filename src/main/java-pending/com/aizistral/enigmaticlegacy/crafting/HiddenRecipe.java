package com.aizistral.enigmaticlegacy.crafting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.CosmicHeart;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import java.util.Optional;

import net.minecraft.core.RegistryAccess;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.World;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;

public class HiddenRecipe extends CustomRecipe {
	public static final SimpleCraftingRecipeSerializer<HiddenRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(HiddenRecipe::new);
	static final Map<ItemStack[][], ItemStack> RECIPES = new HashMap<>();
	static final Entry<ItemStack[][], ItemStack> EMPTY = new Entry<ItemStack[][], ItemStack>() {
		private final ItemStack[][] recipe = new ItemStack[][] {
			{ null, null, null },
			{ null, null, null },
			{ null, null, null }
		};

		@Override
		public ItemStack[][] getKey() {
			return this.recipe;
		}

		@Override
		public ItemStack getValue() {
			return null;
		}

		@Override
		public ItemStack setValue(ItemStack value) {
			throw new UnsupportedOperationException();
		}
	};

	public HiddenRecipe(ResourceLocation id, CraftingBookCategory ctg) {
		super(id, ctg);
	}

	public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
		ItemStack output = null;

		Optional<CompoundTag> amuletNBT = Optional.empty();

		recipes: for (Map.Entry<ItemStack[][], ItemStack> entry : RECIPES.entrySet()) {
			for (int r = 0; r < 3; r++) {
				for (int i = 0; i < 3; i++) {
					ItemStack slotStack = inv.getItem(3 * r + i);

					if (slotStack.getItem() != entry.getKey()[r][i].getItem()) {
						continue recipes;
					} else {
						if (slotStack.is(EnigmaticItems.ENIGMATIC_AMULET) || slotStack.is(EnigmaticItems.ASCENSION_AMULET)) {
							amuletNBT = Optional.of(slotStack.getTag().copy());
						}
					}
				}
			}

			output = entry.getValue().copy();
			amuletNBT.ifPresent(output::setTag);
			break;
		}

		if (!OmniconfigHandler.isItemEnabled(output.getItem())) {
			output = null;
		}

		return output;
	}

	public boolean matches(CraftingContainer inv, World world) {
		recipes: for (ItemStack[][] array : RECIPES.keySet()) {
			for (int r = 0; r < 3; r++) {
				for (int i = 0; i < 3; i++) {
					ItemStack slotStack = inv.getItem(3 * r + i);

					if (slotStack.getItem() != array[r][i].getItem()) {
						continue recipes;
					} else if (RECIPES.get(array).is(EnigmaticItems.COSMIC_SCROLL) &&
							slotStack.getItem() instanceof CosmicHeart &&
							!ItemNBTHelper.getBoolean(slotStack, "isBelieverBlessed", false)) {
						continue recipes;
					}
				}
			}

			return true;
		}

		return false;
	}

	public ItemStack getResultItem(RegistryAccess access) {
		return null;
	}

	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 9;
	}

	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static void addRecipe(ItemStack output, ItemStack... inputs) {
		ItemStack[][] array = new ItemStack[][] {
			{ inputs[0], inputs[1], inputs[2] },
			{ inputs[3], inputs[4], inputs[5] },
			{ inputs[6], inputs[7], inputs[8] }
		};
		RECIPES.put(array, output);
	}

	public static Entry<ItemStack[][], ItemStack> getRecipe(ResourceLocation output) {
		return RECIPES.entrySet().stream().filter(entry -> ForgeRegistries.ITEMS.getKey(entry.getValue().getItem())
				.equals(output)).findFirst().orElse(EMPTY);
	}

}
