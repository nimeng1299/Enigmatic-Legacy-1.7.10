package com.aizistral.enigmaticlegacy.api.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface ICreativeTabMember {

	@Nullable
	public CreativeTabs getCreativeTab();

	public default List<ItemStack> getCreativeTabStacks() {
		if (this instanceof ItemLike item)
			return ImmutableList.of(new ItemStack(item));
		else
			return ImmutableList.of();
	}

}
