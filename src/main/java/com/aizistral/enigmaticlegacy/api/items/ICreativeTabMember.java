package com.aizistral.enigmaticlegacy.api.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

public interface ICreativeTabMember {

    @Nullable
    public CreativeTabs getCreativeTab();

    public default List<ItemStack> getCreativeTabStacks() {
        if (this instanceof Item) return ImmutableList.of(new ItemStack((Item) this));
        else return ImmutableList.of();
    }

}
