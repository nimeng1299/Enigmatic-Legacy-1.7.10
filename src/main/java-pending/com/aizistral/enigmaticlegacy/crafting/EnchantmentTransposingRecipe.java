package com.aizistral.enigmaticlegacy.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizistral.enigmaticlegacy.items.EnchantmentTransposer;

import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.Tuple;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;

/**
 * Special recipe type for transposing enchantments.
 * @author Integral
 */

public class EnchantmentTransposingRecipe extends CustomRecipe {
	public static final SimpleCraftingRecipeSerializer<EnchantmentTransposingRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(EnchantmentTransposingRecipe::new);

	public EnchantmentTransposingRecipe(ResourceLocation id, CraftingBookCategory ctg) {
		super(id, ctg);
	}

	public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack transposer = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.stackSize > 0) {
				if (checkedItemStack.getItem() instanceof EnchantmentTransposer) {
					if (transposer == null) {
						transposer = checkedItemStack.copy();
					} else
						return null;
				} else {
					stackList.add(checkedItemStack);
				}
			}
		}

		if (transposer != null && stackList.size() == 1 && stackList.get(0).isEnchanted()
				&& this.canDisenchant(transposer, stackList.get(0)))
			return this.disenchant(transposer, stackList.get(0)).getA();

		return null;
	}

	public boolean matches(CraftingContainer inv, World world) {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack transposer = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.stackSize > 0) {
				if (checkedItemStack.getItem() instanceof EnchantmentTransposer) {
					if (transposer == null) {
						transposer = checkedItemStack.copy();
					} else
						return false;
				} else {
					stackList.add(checkedItemStack);
				}

			}

		}

		if (transposer != null && stackList.size() == 1 && stackList.get(0).isEnchanted()
				&& this.canDisenchant(transposer, stackList.get(0)))
			return true;

		return false;
	}

	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), null);
		Map<ItemStack, Integer> stackList = new HashMap<>();
		ItemStack transposer = null;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack checkedItemStack = inv.getItem(i);

			if (checkedItemStack.stackSize > 0) {
				if (checkedItemStack.getItem() instanceof EnchantmentTransposer) {
					if (transposer == null) {
						transposer = checkedItemStack.copy();
					} else
						return remaining;
				} else {
					stackList.put(checkedItemStack, i);
				}
			}
		}

		if (transposer != null && stackList.size() == 1) {
			ItemStack returned = stackList.keySet().iterator().next();

			if (returned.isEnchanted() && this.canDisenchant(transposer, returned)) {
				remaining.set(stackList.get(returned), this.disenchant(transposer, returned).getB());
			}
		}

		return remaining;
	}

	private Tuple<ItemStack, ItemStack> disenchant(ItemStack transposer, ItemStack target) {
		Map<Enchantment, Integer> transposed = EnchantmentHelper.getEnchantments(target);
		Map<Enchantment, Integer> leftover = EnchantmentHelper.getEnchantments(target);
		transposed.keySet().removeIf(enchant -> !((EnchantmentTransposer)transposer.getItem()).canTranspose(enchant));
		leftover.keySet().removeIf(enchant -> ((EnchantmentTransposer)transposer.getItem()).canTranspose(enchant));

		ItemStack book = new ItemStack(Items.enchanted_book);
		EnchantmentHelper.setEnchantments(transposed, book);

		ItemStack item = target.copy();
		EnchantmentHelper.setEnchantments(leftover, item);

		return new Tuple<>(book, item);
	}

	private boolean canDisenchant(ItemStack transposer, ItemStack target) {
		return EnchantmentHelper.getEnchantments(target).keySet().stream()
				.anyMatch(((EnchantmentTransposer)transposer.getItem())::canTranspose);
	}

	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
