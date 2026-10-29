package com.aizistral.enigmaticlegacy.items.generic;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

import net.minecraft.item.ItemBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.EnumRarity;
import net.minecraft.block.Block;

public class GenericBlockItem extends ItemBlock implements ICreativeTabMember {
	private final Supplier<CreativeTabs> tab; // supplier cuz weird shit happens otherwise

	public GenericBlockItem(Block blockIn) {
		this(blockIn, GenericBlockItem.getDefaultProperties());
	}

	public GenericBlockItem(Block blockIn, Properties props) {
		this(blockIn, props, () -> EnigmaticTabs.MAIN);
	}

	public GenericBlockItem(Block blockIn, Properties props, Supplier<@Nullable CreativeTabs> tab) {
		super(blockIn, props);
		this.tab = tab;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return this.tab.get();
	}

	public static Properties getDefaultProperties() {
		Properties props = new Item.Properties();

		props.stacksTo(64);
		props.rarity(EnumRarity.COMMON);

		return props;
	}

}
