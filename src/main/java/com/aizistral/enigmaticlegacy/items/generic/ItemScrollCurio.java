package com.aizistral.enigmaticlegacy.items.generic;

import net.minecraft.item.ItemStack;

import baubles.api.expanded.IBaubleExpanded;

public abstract class ItemScrollCurio extends ItemBaseCurio implements IBaubleExpanded {

    public static final String SCROLL_SLOT = "scroll";

    public ItemScrollCurio() {
        super(ItemBaseCurio.getDefaultProperties());
    }

    public ItemScrollCurio(Properties properties) {
        super(properties);
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return new String[] { SCROLL_SLOT };
    }

    @Override
    public String getBaubleSlotType(ItemStack stack) {
        return SCROLL_SLOT;
    }

}
