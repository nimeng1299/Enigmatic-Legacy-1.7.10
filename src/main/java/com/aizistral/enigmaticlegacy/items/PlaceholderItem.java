package com.aizistral.enigmaticlegacy.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;

import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class PlaceholderItem extends ItemBase {

    public PlaceholderItem(String name, EnumRarity rarity) {
        this(name, rarity, 1);
    }

    public PlaceholderItem(String name, EnumRarity rarity, int maxStackSize) {
        super(
            ItemBase.getDefaultProperties()
                .rarity(rarity)
                .stacksTo(maxStackSize));
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return null;
    }

}
