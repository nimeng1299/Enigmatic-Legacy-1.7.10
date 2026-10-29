package com.aizistral.enigmaticlegacy.items;

import net.minecraft.item.EnumRarity;

import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class DarkestScroll extends ItemBase {

    public DarkestScroll() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.uncommon)
                .stacksTo(1));
    }

}
