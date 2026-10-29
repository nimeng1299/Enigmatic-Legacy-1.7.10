package com.aizistral.enigmaticlegacy.items;

import net.minecraft.item.EnumRarity;

import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class LivingFlame extends ItemBase {

    public LivingFlame() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.uncommon));
    }

}
