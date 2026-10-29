package com.aizistral.enigmaticlegacy.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.items.EnchantmentTransposer;

/**
 * Special recipe type for transposing enchantments.
 * <p>
 * 1.7.10 has no getRemainingItems hook, so the item with the leftover
 * enchantments that 1.20 returned to the crafting grid is not preserved.
 * 
 * @author Integral
 */
public class EnchantmentTransposingRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack transposer = null;
        List<ItemStack> stackList = new ArrayList<ItemStack>();

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkedItemStack = inv.getStackInSlot(i);

            if (checkedItemStack != null && checkedItemStack.stackSize > 0) {
                if (checkedItemStack.getItem() instanceof EnchantmentTransposer) {
                    if (transposer != null) return false;

                    transposer = checkedItemStack;
                } else {
                    stackList.add(checkedItemStack);
                }
            }
        }

        if (transposer == null || stackList.size() != 1) return false;

        ItemStack target = stackList.get(0);

        return Enchantments.getEnchantments(target)
            .size() > 0 && this.canDisenchant(transposer, target);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack transposer = null;
        ItemStack target = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkedItemStack = inv.getStackInSlot(i);

            if (checkedItemStack != null && checkedItemStack.stackSize > 0) {
                if (checkedItemStack.getItem() instanceof EnchantmentTransposer) {
                    if (transposer != null) return null;

                    transposer = checkedItemStack;
                } else {
                    if (target != null) return null;

                    target = checkedItemStack;
                }
            }
        }

        if (transposer == null || target == null) return null;

        if (Enchantments.getEnchantments(target)
            .size() <= 0 || !this.canDisenchant(transposer, target)) return null;

        return this.disenchant(transposer, target);
    }

    private ItemStack disenchant(ItemStack transposer, ItemStack target) {
        EnchantmentTransposer transposerItem = (EnchantmentTransposer) transposer.getItem();

        Map<Enchantment, Integer> transposed = new HashMap<Enchantment, Integer>(Enchantments.getEnchantments(target));

        Iterator<Enchantment> iterator = transposed.keySet()
            .iterator();

        while (iterator.hasNext()) {
            if (!transposerItem.canTranspose(iterator.next())) {
                iterator.remove();
            }
        }

        ItemStack book = new ItemStack(Items.enchanted_book);
        Enchantments.setEnchantments(transposed, book);

        return book;
    }

    private boolean canDisenchant(ItemStack transposer, ItemStack target) {
        EnchantmentTransposer transposerItem = (EnchantmentTransposer) transposer.getItem();

        for (Enchantment enchantment : Enchantments.getEnchantments(target)
            .keySet()) {
            if (transposerItem.canTranspose(enchantment)) return true;
        }

        return false;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

}
