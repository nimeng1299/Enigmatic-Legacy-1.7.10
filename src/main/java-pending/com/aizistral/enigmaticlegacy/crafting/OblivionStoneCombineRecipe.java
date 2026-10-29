package com.aizistral.enigmaticlegacy.crafting;

import java.util.ArrayList;
import java.util.List;

import com.aizistral.enigmaticlegacy.items.OblivionStone;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.World;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;

/**
 * Special recipe type for adding items to list of Keystone of The Oblivion.
 * @author Integral
 */

public class OblivionStoneCombineRecipe extends CustomRecipe {
	public static final SimpleCraftingRecipeSerializer<OblivionStoneCombineRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(OblivionStoneCombineRecipe::new);

	public OblivionStoneCombineRecipe(ResourceLocation id, CraftingBookCategory ctg) {
		super(id, ctg);
	}

	public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack voidStone = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.stackSize > 0) {
				if (checkedItemStack.getItem() == EnigmaticItems.VOID_STONE) {
					if (voidStone == null) {
						voidStone = checkedItemStack;
					} else
						return null;
				} else {
					stackList.add(checkedItemStack);
				}

			}

		}

		if (voidStone != null && stackList.size() == 1) {
			ItemStack savedStack = stackList.get(0).copy();

			CompoundTag nbt = voidStone.getOrCreateTag();

			ListTag arr = nbt.getList("SupersolidID", 8);
			int counter = 0;

			if (arr.size() >= OblivionStone.itemHardcap.getValue())
				return null;

			for (Tag s_uncast : arr) {
				counter++;

				String s = ((StringTag)s_uncast).getAsString();

				if (s.equals(ForgeRegistries.ITEMS.getKey(savedStack.getItem()).toString()))
					return null;
			}

			ListTag arrCopy = arr.copy();
			CompoundTag nbtCopy = nbt.copy();

			arrCopy.add(StringTag.valueOf(ForgeRegistries.ITEMS.getKey(savedStack.getItem()).toString()));

			nbtCopy.put("SupersolidID", arrCopy);

			ItemStack returnedStack = voidStone.copy();
			returnedStack.setTag(nbtCopy);

			return returnedStack;

		} else if (voidStone != null && stackList.size() == 0) {
			ItemStack returnedStack = new ItemStack(EnigmaticItems.VOID_STONE, 1);
			returnedStack.setTag(voidStone.getOrCreateTag().copy());
			returnedStack.removeTagKey("SupersolidID");
			return returnedStack;
		} else
			return null;
	}

	public boolean matches(CraftingContainer inv, World world) {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack voidStone = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.stackSize > 0) {
				if (checkedItemStack.getItem() == EnigmaticItems.VOID_STONE) {
					if (voidStone == null) {
						voidStone = checkedItemStack;
					} else
						return false;
				} else {
					stackList.add(checkedItemStack);
				}

			}

		}

		if (voidStone != null && stackList.size() == 1) {
			ItemStack savedStack = stackList.get(0).copy();

			CompoundTag nbt = voidStone.getOrCreateTag();

			ListTag arr = nbt.getList("SupersolidID", 8);
			int counter = 0;

			if (arr.size() >= OblivionStone.itemHardcap.getValue())
				return false;

			for (Tag s_uncast : arr) {
				counter++;

				String s = ((StringTag)s_uncast).getAsString();

				if (s.equals(ForgeRegistries.ITEMS.getKey(savedStack.getItem()).toString()))
					return false;
			}

			return true;

		} else if (voidStone != null && stackList.size() == 0)
			return true;
		else
			return false;
	}

	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getContainerSize(), null);
		return nonnulllist;
	}

	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

}
