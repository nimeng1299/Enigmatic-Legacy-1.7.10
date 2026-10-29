package com.aizistral.enigmaticlegacy.items.generic;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;

public class GenericBlockItem extends ItemBlock implements ICreativeTabMember {

    private final Supplier<CreativeTabs> tab;

    public GenericBlockItem(Block blockIn) {
        this(blockIn, GenericBlockItem.getDefaultProperties());
    }

    public GenericBlockItem(Block blockIn, ItemBase.Properties props) {
        this(blockIn, props, () -> EnigmaticTabs.MAIN);
    }

    public GenericBlockItem(Block blockIn, ItemBase.Properties props, Supplier<CreativeTabs> tab) {
        super(blockIn);
        props.apply(this);
        this.tab = tab;
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return this.tab.get();
    }

    public static ItemBase.Properties getDefaultProperties() {
        ItemBase.Properties props = new ItemBase.Properties();

        props.stacksTo(64);
        props.rarity(EnumRarity.common);

        return props;
    }

}
