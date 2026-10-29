package com.aizistral.enigmaticlegacy.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.items.OblivionStone;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Special recipe type for adding items to the list of Keystone of The Oblivion.
 * 
 * @author Integral
 */
public class OblivionStoneCombineRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return this.createResult(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return this.createResult(inv);
    }

    private ItemStack createResult(InventoryCrafting inv) {
        if (OblivionStone.itemHardcap == null) return null;

        ItemStack voidStone = null;
        ItemStack savedStack = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkedItemStack = inv.getStackInSlot(i);

            if (checkedItemStack != null && checkedItemStack.stackSize > 0) {
                if (checkedItemStack.getItem() == EnigmaticItems.VOID_STONE) {
                    if (voidStone != null) return null;

                    voidStone = checkedItemStack;
                } else {
                    if (savedStack != null) return null;

                    savedStack = checkedItemStack;
                }
            }
        }

        if (voidStone == null) return null;

        if (savedStack == null) {
            ItemStack reset = new ItemStack(EnigmaticItems.VOID_STONE, 1);
            NBTTagCompound nbt = voidStone.hasTagCompound() ? (NBTTagCompound) voidStone.getTagCompound()
                .copy() : new NBTTagCompound();

            nbt.removeTag("SupersolidID");
            reset.setTagCompound(nbt);

            return reset;
        }

        String itemId = Item.itemRegistry.getNameForObject(savedStack.getItem());

        if (itemId == null) return null;

        NBTTagCompound nbt = voidStone.hasTagCompound() ? (NBTTagCompound) voidStone.getTagCompound()
            .copy() : new NBTTagCompound();
        NBTTagList list = nbt.getTagList("SupersolidID", 8);

        if (list.tagCount() >= OblivionStone.itemHardcap.getValue()) return null;

        for (int i = 0; i < list.tagCount(); i++) {
            if (itemId.equals(list.getStringTagAt(i))) return null;
        }

        list.appendTag(new NBTTagString(itemId));
        nbt.setTag("SupersolidID", list);

        ItemStack result = voidStone.copy();
        result.setTagCompound(nbt);

        return result;
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
