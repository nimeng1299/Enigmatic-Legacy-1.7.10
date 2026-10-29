package com.aizistral.enigmaticlegacy.items;

import net.minecraft.item.EnumRarity;

import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class AstralDust extends ItemBase {

    public AstralDust() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic));
    }

}
